package com.angkorteam.mbaas.server.background;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.records.StatisticsRecord;
import com.angkorteam.mbaas.server.MBaaS;
import org.hyperic.sigar.*;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Khauv Socheat on 4/20/2016.
 */
@Service
public class PerformanceBackground {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private boolean error = false;

    @Scheduled(cron = "0 * * * * *")
    public void collect() {
        if (error) {
            return;
        }
        try {
//            Sigar sigar = new Sigar();
//            long processId = sigar.getPid();
//            ProcCpu procCpu = sigar.getProcCpu(processId);
//            ProcMem procMem = sigar.getProcMem(processId);
//            StatisticsRecord statisticsRecord = context.newRecord(Tables.STATISTICS);
//            statisticsRecord.setStatisticsId(UUID.randomUUID().toString());
//            statisticsRecord.setMemory(Double.valueOf(procMem.getSize()));
//            statisticsRecord.setCpu(procCpu.getPercent());
//            statisticsRecord.setDateCreated(new Date());
//            statisticsRecord.store();
//            LOGGER.info("cpu {} mem {}", statisticsRecord.getCpu(), statisticsRecord.getMemory());
//            sigar.close();
        } catch (Throwable e) {
            e.printStackTrace();
            error = true;
        }
    }
}
