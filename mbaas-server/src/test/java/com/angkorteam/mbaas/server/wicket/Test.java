package com.angkorteam.mbaas.server.wicket;

import org.eclipse.jetty.websocket.common.io.IOState;
import org.hyperic.sigar.*;
import org.hyperic.sigar.cmd.Iostat;
import org.hyperic.sigar.cmd.Runner;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.util.derby.sys.Sys;

/**
 * Created by socheat on 4/10/16.
 */
public class Test {

    public static void main(String args[]) throws Exception {
        Sigar sigar = new Sigar();
        while (true) {
            Thread.sleep(1000);
            for (FileSystem system : sigar.getFileSystemList()) {
                if (system.getType() == 2) {
                    FileSystemUsage usage = sigar.getFileSystemUsage(system.getDirName());
                    System.out.println(system.getDevName());
                    System.out.println(system.getDirName());
                }
            }
        }
        //sigar.close();
        // System.out.println(cpuPerc.getIdle() + cpuPerc.getCombined() + cpuPerc.getIrq() + cpuPerc.getNice() + cpuPerc.getSoftIrq() + cpuPerc.getStolen() + cpuPerc.getSys() + cpuPerc.getUser() + cpuPerc.getWait());
        //Runner.main(args);
    }
}
