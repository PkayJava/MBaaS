package com.angkorteam.mbaas.server.background;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.records.CpuRecord;
import com.angkorteam.mbaas.model.entity.tables.records.DiskRecord;
import com.angkorteam.mbaas.model.entity.tables.records.MemRecord;
import com.angkorteam.mbaas.server.MBaaS;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Khauv Socheat on 4/20/2016.
 */
@Service
public class PerformanceBackground {

    @Autowired
    private DSLContext context;

    private boolean error = false;

    @Scheduled(cron = "* * * * * *")
    public void collect() throws IOException {
        if (error) {
            return;
        }
        try {
            {
                CommandLine commandLine = CommandLine.parse("/usr/bin/iostat -m");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
                DefaultExecutor executor = new DefaultExecutor();
                executor.setStreamHandler(streamHandler);
                executor.execute(commandLine);
                List<String> lines = IOUtils.readLines(new ByteArrayInputStream(outputStream.toByteArray()));
                CpuInfo cpuInfo = parseCpuInfo(lines);
                CpuRecord cpuRecord = context.newRecord(Tables.CPU);
                cpuRecord.setUser(cpuInfo.getUser());
                cpuRecord.setSteal(cpuInfo.getSteal());
                cpuRecord.setNice(cpuInfo.getNice());
                cpuRecord.setIowait(cpuInfo.getIowait());
                cpuRecord.setSystem(cpuInfo.getSystem());
                cpuRecord.setIdle(cpuInfo.getIdle());
                cpuRecord.setDateCreated(new Date());
                cpuRecord.setCpuId(UUID.randomUUID().toString());
                cpuRecord.store();
                List<DiskInfo> diskInfos = parseDiskInfo(lines);
                for (DiskInfo diskInfo : diskInfos) {
                    DiskRecord diskRecord = context.newRecord(Tables.DISK);
                    diskRecord.setDevice(diskInfo.getDevice());
                    diskRecord.setWrite(diskInfo.getWrite());
                    diskRecord.setRead(diskInfo.getRead());
                    diskRecord.setDiskId(UUID.randomUUID().toString());
                    diskRecord.setDateCreated(new Date());
                    diskRecord.store();
                }
            }
            {
                CommandLine commandLine = CommandLine.parse("/usr/bin/free -m");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
                DefaultExecutor executor = new DefaultExecutor();
                executor.setStreamHandler(streamHandler);
                executor.execute(commandLine);
                List<String> lines = IOUtils.readLines(new ByteArrayInputStream(outputStream.toByteArray()));
                List<MemInfo> memInfos = parseMemInfo(lines);
                for (MemInfo memInfo : memInfos) {
                    MemRecord memRecord = context.newRecord(Tables.MEM);
                    memRecord.setMemId(UUID.randomUUID().toString());
                    memRecord.setDevice(memInfo.getDevice());
                    memRecord.setTotal(memInfo.getTotal());
                    memRecord.setUsed(memInfo.getUsed());
                    memRecord.setFree(memInfo.getFree());
                    memRecord.setDateCreated(new Date());
                    memRecord.store();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            error = true;
        }
    }

    protected List<DiskInfo> parseDiskInfo(List<String> lines) {
        int deviceIndex = 0;
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line != null && line.startsWith("Device:")) {
                deviceIndex = index + 1;
                break;
            }
        }
        List<DiskInfo> diskInfos = new ArrayList<>();
        for (int index = deviceIndex; index < lines.size(); index++) {
            String device = lines.get(index);
            if (device != null && !"".equals(device)) {
                device = device.trim().replaceAll("\\s+", " ");
                String[] infos = device.split(" ");
                DiskInfo diskInfo = new DiskInfo();
                diskInfo.setDevice(infos[0]);
                diskInfo.setRead(Double.valueOf(infos[2]));
                diskInfo.setWrite(Double.valueOf(infos[3]));
                diskInfos.add(diskInfo);
            }
        }
        return diskInfos;
    }

    protected List<MemInfo> parseMemInfo(List<String> lines) {
        List<MemInfo> memInfos = new ArrayList<>();
        for (String line : lines) {
            if (line != null && !"".equals(line)) {
                if (line.startsWith("Mem:")) {
                    String[] mems = line.substring(4).trim().replaceAll("\\s+", " ").split(" ");
                    MemInfo memInfo = new MemInfo();
                    memInfo.setDevice("Memory");
                    memInfo.setTotal(Double.valueOf(mems[0]));
                    memInfo.setUsed(Double.valueOf(mems[1]));
                    memInfo.setFree(Double.valueOf(mems[2]));
                    memInfos.add(memInfo);
                } else if (line.startsWith("Swap:")) {
                    String[] swaps = line.substring(5).trim().replaceAll("\\s+", " ").split(" ");
                    MemInfo memInfo = new MemInfo();
                    memInfo.setDevice("Swap");
                    memInfo.setTotal(Double.valueOf(swaps[0]));
                    memInfo.setUsed(Double.valueOf(swaps[1]));
                    memInfo.setFree(Double.valueOf(swaps[2]));
                    memInfos.add(memInfo);
                }
            }
        }
        return memInfos;
    }

    protected CpuInfo parseCpuInfo(List<String> lines) {

        String cpu = null;
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line != null && line.startsWith("avg-cpu")) {
                cpu = lines.get(index + 1);
                break;
            }
        }
        List<String> params = new ArrayList<>();
        cpu = cpu.trim().replaceAll("\\s+", " ");
        for (String info : cpu.split(" ")) {
            params.add(info);
        }
        CpuInfo cpuInfo = new CpuInfo();
        cpuInfo.setUser(Double.valueOf(params.get(0)));
        cpuInfo.setNice(Double.valueOf(params.get(1)));
        cpuInfo.setSystem(Double.valueOf(params.get(2)));
        cpuInfo.setIowait(Double.valueOf(params.get(3)));
        cpuInfo.setSteal(Double.valueOf(params.get(4)));
        cpuInfo.setIdle(Double.valueOf(params.get(5)));
        return cpuInfo;
    }
}
