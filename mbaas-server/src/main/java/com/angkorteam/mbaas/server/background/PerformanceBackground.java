//package com.angkorteam.mbaas.server.background;
//
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.records.CpuRecord;
//import com.angkorteam.mbaas.model.entity.tables.records.DiskRecord;
//import com.angkorteam.mbaas.model.entity.tables.records.MemRecord;
//import org.hyperic.sigar.*;
//import org.jooq.DSLContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.*;
//
///**
// * Created by Khauv Socheat on 4/20/2016.
// */
//@Service
//public class PerformanceBackground {
//
//    @Autowired
//    private DSLContext context;
//
//    private boolean error = false;
//
//    private final Map<String, Long> writes = new HashMap<>();
//    private final Map<String, Long> reads = new HashMap<>();
//
//    @Autowired
//    private Sigar sigar;
//
//    public static Double format(double val) {
//        String p = String.valueOf(val * 100.0D);
//        int ix = p.indexOf(".") + 1;
//        String percent = p.substring(0, ix) + p.substring(ix, ix + 1);
//        return Double.valueOf(percent);
//    }
//
//    @Scheduled(cron = "* * * * * *")
//    public void collect() throws IOException {
//        if (error) {
//            return;
//        }
//        try {
//            {
//                CpuPerc cpuPerc = sigar.getCpuPerc();
//                CpuRecord cpuRecord = context.newRecord(Tables.CPU);
//                cpuRecord.setUser(format(cpuPerc.getUser()));
//                cpuRecord.setNice(format(cpuPerc.getNice()));
//                cpuRecord.setSystem(format(cpuPerc.getSys()));
//                cpuRecord.setIdle(format(cpuPerc.getIdle()));
//                cpuRecord.setDateCreated(new Date());
//                cpuRecord.setCpuId(UUID.randomUUID().toString());
//                cpuRecord.store();
//
//                for (FileSystem system : sigar.getFileSystemList()) {
//                    if (system.getType() == 2) {
//                        String key = system.getDirName() + " [" + system.getDevName() + "]";
//                        FileSystemUsage usage = sigar.getFileSystemUsage(system.getDirName());
//                        if (!writes.containsKey(system.getDirName())) {
//                            writes.put(key, usage.getDiskWriteBytes());
//                        }
//                        if (!reads.containsKey(system.getDirName())) {
//                            reads.put(key, usage.getDiskReadBytes());
//                        }
//                        long lastReadBytes = usage.getDiskReadBytes();
//                        long lastWriteBytes = usage.getDiskWriteBytes();
//                        double writeBytes = lastWriteBytes - writes.get(key);
//                        double readBytes = lastReadBytes - reads.get(key);
//                        DiskRecord diskRecord = context.newRecord(Tables.DISK);
//                        diskRecord.setDiskId(UUID.randomUUID().toString());
//                        diskRecord.setDevice(key);
//                        diskRecord.setWrite(writeBytes);
//                        diskRecord.setRead(readBytes);
//                        diskRecord.setDateCreated(new Date());
//                        diskRecord.store();
//                        writes.put(key, lastWriteBytes);
//                        reads.put(key, lastReadBytes);
//                    }
//                }
//            }
//            {
//                Mem mem = sigar.getMem();
//                MemRecord memRecord = context.newRecord(Tables.MEM);
//                memRecord.setMemId(UUID.randomUUID().toString());
//                memRecord.setDevice("Memory");
//                memRecord.setTotal((mem.getTotal() / 1024d / 1024d));
//                memRecord.setUsed((mem.getUsed() / 1024d / 1024d));
//                memRecord.setFree((mem.getFree() / 1024d / 1024d));
//                memRecord.setDateCreated(new Date());
//                memRecord.store();
//            }
//            {
//                Swap swap = sigar.getSwap();
//                if (swap != null) {
//                    MemRecord memRecord = context.newRecord(Tables.MEM);
//                    memRecord.setMemId(UUID.randomUUID().toString());
//                    memRecord.setDevice("Memory");
//                    memRecord.setTotal((swap.getTotal() / 1024d / 1024d));
//                    memRecord.setUsed((swap.getUsed() / 1024d / 1024d));
//                    memRecord.setFree((swap.getFree() / 1024d / 1024d));
//                    memRecord.setDateCreated(new Date());
//                    memRecord.store();
//                }
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//            error = true;
//        }
//    }
//
//    protected List<DiskInfo> parseDiskInfo(List<String> lines) {
//        int deviceIndex = 0;
//        for (int index = 0; index < lines.size(); index++) {
//            String line = lines.get(index);
//            if (line != null && line.startsWith("Device:")) {
//                deviceIndex = index + 1;
//                break;
//            }
//        }
//        List<DiskInfo> diskInfos = new ArrayList<>();
//        for (int index = deviceIndex; index < lines.size(); index++) {
//            String device = lines.get(index);
//            if (device != null && !"".equals(device)) {
//                device = device.trim().replaceAll("\\s+", " ");
//                String[] infos = device.split(" ");
//                DiskInfo diskInfo = new DiskInfo();
//                diskInfo.setDevice(infos[0]);
//                diskInfo.setRead(Double.valueOf(infos[2]));
//                diskInfo.setWrite(Double.valueOf(infos[3]));
//                diskInfos.add(diskInfo);
//            }
//        }
//        return diskInfos;
//    }
//
//    protected List<MemInfo> parseMemInfo(List<String> lines) {
//        List<MemInfo> memInfos = new ArrayList<>();
//        for (String line : lines) {
//            if (line != null && !"".equals(line)) {
//                if (line.startsWith("Mem:")) {
//                    String[] mems = line.substring(4).trim().replaceAll("\\s+", " ").split(" ");
//                    MemInfo memInfo = new MemInfo();
//                    memInfo.setDevice("Memory");
//                    memInfo.setTotal(Double.valueOf(mems[0]));
//                    memInfo.setUsed(Double.valueOf(mems[1]));
//                    memInfo.setFree(Double.valueOf(mems[2]));
//                    memInfos.add(memInfo);
//                } else if (line.startsWith("Swap:")) {
//                    String[] swaps = line.substring(5).trim().replaceAll("\\s+", " ").split(" ");
//                    MemInfo memInfo = new MemInfo();
//                    memInfo.setDevice("Swap");
//                    memInfo.setTotal(Double.valueOf(swaps[0]));
//                    memInfo.setUsed(Double.valueOf(swaps[1]));
//                    memInfo.setFree(Double.valueOf(swaps[2]));
//                    memInfos.add(memInfo);
//                }
//            }
//        }
//        return memInfos;
//    }
//
//    protected CpuInfo parseCpuInfo(List<String> lines) {
//
//        String cpu = null;
//        for (int index = 0; index < lines.size(); index++) {
//            String line = lines.get(index);
//            if (line != null && line.startsWith("avg-cpu")) {
//                cpu = lines.get(index + 1);
//                break;
//            }
//        }
//        List<String> params = new ArrayList<>();
//        cpu = cpu.trim().replaceAll("\\s+", " ");
//        for (String info : cpu.split(" ")) {
//            params.add(info);
//        }
//        CpuInfo cpuInfo = new CpuInfo();
//        cpuInfo.setUser(Double.valueOf(params.get(0)));
//        cpuInfo.setNice(Double.valueOf(params.get(1)));
//        cpuInfo.setSystem(Double.valueOf(params.get(2)));
//        cpuInfo.setIowait(Double.valueOf(params.get(3)));
//        cpuInfo.setSteal(Double.valueOf(params.get(4)));
//        cpuInfo.setIdle(Double.valueOf(params.get(5)));
//        return cpuInfo;
//    }
//}
