/*
 * $Id$
 */
package org.jnode.test.fs;

import javax.naming.NameNotFoundException;

import org.jnode.driver.ApiNotFoundException;
import org.jnode.driver.Device;
import org.jnode.driver.DeviceManager;
import org.jnode.driver.DeviceNotFoundException;
import org.jnode.driver.scsi.CDB;
import org.jnode.driver.scsi.SCSIDeviceAPI;
import org.jnode.driver.scsi.SCSIException;
import org.jnode.driver.scsi.cdb.mmc.CDBGetConfiguration;
import org.jnode.driver.scsi.cdb.mmc.CDBMediaRemoval;
import org.jnode.driver.scsi.cdb.mmc.CDBRead10;
import org.jnode.driver.scsi.cdb.mmc.CDBReadCapacity;
import org.jnode.driver.scsi.cdb.mmc.CapacityData;
import org.jnode.driver.scsi.cdb.spc.CDBReportLuns;
import org.jnode.driver.scsi.cdb.spc.CDBRequestSense;
import org.jnode.driver.scsi.cdb.spc.RequestSenseData;
import org.jnode.naming.InitialNaming;
import org.jnode.util.NumberUtils;
import org.jnode.util.TimeoutException;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class SCSITest {

    private final SCSIDeviceAPI api;

    private final byte[] data = new byte[ 4096];

    private String name = "sg0";

    private boolean lock = false;

    private boolean unlock = false;

    private boolean eject = false;

    public static void main(String[] args) throws Exception {
        SCSITest test = new SCSITest(args);
        test.run();
    }

    public SCSITest(String[] args) throws ApiNotFoundException,
            NameNotFoundException, DeviceNotFoundException {
        processArgs(args);
        final DeviceManager dm = (DeviceManager) InitialNaming
                .lookup(DeviceManager.NAME);
        final Device dev = dm.getDevice(name);
        api = (SCSIDeviceAPI) dev.getAPI(SCSIDeviceAPI.class);
    }

    public void run() throws SCSIException, TimeoutException,
            InterruptedException {
        try {
            if (lock) {
                setMediaRemoval(true, false);
            } else if (unlock) {
                setMediaRemoval(false, false);
            } else {
                final CapacityData cap = readCapacity();
                readBlocks(cap, 16, 1);
                reportLuns();
                getConfig();
            }
        } catch (SCSIException ex) {
            System.out.println("Error; Sense " + requestSense());
        }
    }

    private void processArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            final String arg = args[ i];
            if (arg.startsWith("-")) {
                if (arg.equals("-lock")) {
                    lock = true;
                } else if (arg.equals("-unlock")) {
                    unlock = true;
                } else if (arg.equals("-eject")) {
                    eject = true;
                }
            } else {
                name = arg;
            }
        }
    }

    private void setMediaRemoval(boolean prevent, boolean persistent)
            throws SCSIException, TimeoutException, InterruptedException {
        final CDB cdb = new CDBMediaRemoval(prevent, persistent);
        api.executeCommand(cdb, null, 0, 5000);
    }

    public RequestSenseData requestSense() throws SCSIException,
            TimeoutException, InterruptedException {
        final byte[] data = new byte[ 256];
        final CDB cdb = new CDBRequestSense(data.length);
        api.executeCommand(cdb, data, 0, 5000);
        return new RequestSenseData(data);
    }

    private CapacityData readCapacity() throws SCSIException, TimeoutException,
            InterruptedException {
        final CDB cdb = new CDBReadCapacity();
        api.executeCommand(cdb, data, 0, 5000);
        CapacityData rc = new CapacityData(data);
        System.out.println("ReadCapacity " + rc);
        return rc;
    }

    private void readBlocks(CapacityData cap, int lba, int nrBlocks)
            throws SCSIException, TimeoutException, InterruptedException {
        final byte[] data = new byte[ nrBlocks * cap.getBlockLength()];
        final CDB cdb = new CDBRead10(lba, nrBlocks);
        final int len = api.executeCommand(cdb, data, 0, 5000);
        System.out.println("Read " + NumberUtils.hex(data, 0, len));
    }

    private void reportLuns() throws SCSIException, TimeoutException,
            InterruptedException {
        final byte[] data = new byte[ 4096];
        final CDB cdb = new CDBReportLuns(data.length);
        final int len = api.executeCommand(cdb, data, 0, 5000);
        System.out.println("ReportLuns" + NumberUtils.hex(data, 0, len));
    }

    private void getConfig() throws SCSIException, TimeoutException,
            InterruptedException {
        final byte[] data = new byte[ 4096];
        final CDB cdb = new CDBGetConfiguration(0, 0);
        final int len = api.executeCommand(cdb, data, 0, 5000);
        System.out.println("GetConfig " + NumberUtils.hex(data, 0, len));
    }

}