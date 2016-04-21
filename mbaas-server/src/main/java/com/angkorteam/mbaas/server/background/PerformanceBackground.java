package com.angkorteam.mbaas.server.background;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.records.CpuRecord;
import com.angkorteam.mbaas.model.entity.tables.records.DiskRecord;
import com.angkorteam.mbaas.server.MBaaS;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    @Autowired
    private DSLContext context;

    private boolean error = false;

    @Scheduled(cron = "0 * * * * *")
    public void collect() throws IOException {
        if (error) {
            return;
        }
        try {
            String uuid = UUID.randomUUID().toString();
            File fileUuid = new java.io.File(FileUtils.getTempDirectory(), uuid + ".txt");
            Runtime.getRuntime().exec("iostat -m > \"" + fileUuid.getAbsolutePath() + "\"");
            List<String> lines = FileUtils.readLines(fileUuid);
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
        } catch (Throwable e) {
            e.printStackTrace();
            error = true;
        }
    }

    public static void main(String[] args) throws IOException {
        String uuid = UUID.randomUUID().toString();
        CommandLine cmdLine = CommandLine.parse("iostat");
        cmdLine.addArgument("-m", false);
        cmdLine.addArgument(">", false);
        cmdLine.addArgument("/tmp/" + uuid + ".txt", true);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.execute(cmdLine);
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