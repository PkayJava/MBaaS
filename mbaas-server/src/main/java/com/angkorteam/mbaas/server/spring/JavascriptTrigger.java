package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by socheat on 4/23/16.
 */
public class JavascriptTrigger extends CronTrigger {

    private final String jobId;
    private final String applicationCode;
    private final ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource;
    private final DSLContext context;

    public JavascriptTrigger(DSLContext context, ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource, String applicationCode, String jobId, String expression) {
        super(expression);
        this.context = context;
        this.jobId = jobId;
        this.applicationDataSource = applicationDataSource;
        this.applicationCode = applicationCode;
    }

    public JavascriptTrigger(DSLContext context, ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource, String applicationCode, String jobId, String expression, TimeZone timeZone) {
        super(expression, timeZone);
        this.context = context;
        this.jobId = jobId;
        this.applicationDataSource = applicationDataSource;
        this.applicationCode = applicationCode;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        DSLContext context = this.context;
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.CODE.eq(applicationCode)).fetchOneInto(applicationTable);
        String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
        JdbcTemplate jdbcTemplate = this.applicationDataSource.getJdbcTemplate(applicationCode, jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
        Map<String, Object> jobRecord = null;
        jobRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JOB + " WHERE " + Jdbc.Job.JOB_ID + " = ?", this.jobId);
        if (jobRecord == null) {
            return null;
        }
        Date next = super.nextExecutionTime(triggerContext);
        return next;
    }
}
