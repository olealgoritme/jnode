/*
 * $Id$
 *
 * JNode.org
 * Copyright (C) 2004 JNode.org
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */
package org.jnode.vm.memmgr.def;

import org.jnode.vm.memmgr.GCStatistics;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
final class DefGCStatistics extends GCStatistics {

    long lastGCTime;
    long lastMarkDuration;
    int lastMarkIterations;
    long lastSweepDuration;
    long lastCleanupDuration;
    long lastVerifyDuration;
    long lastFreedBytes;
    long lastMarkedObjects;

    public String toString() {
        return "lastGCTime          " + lastGCTime + "\n" +
               "lastMarkIterations  " + lastMarkIterations + "\n" +
               "lastMarkDuration    " + lastMarkDuration + "\n" +
               "lastSweepDuration   " + lastSweepDuration + "\n" +
               "lastCleanupDuration " + lastCleanupDuration + "\n" +
               "lastVerifyDuration  " + lastVerifyDuration + "\n" +
               "lastMarkedObjects   " + lastMarkedObjects + "\n" +
               "lastFreedBytes      " + lastFreedBytes;
    }
    
}
