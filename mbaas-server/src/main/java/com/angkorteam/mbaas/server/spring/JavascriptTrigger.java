package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JobTable;
import com.angkorteam.mbaas.model.entity.tables.records.JobRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import org.jooq.DSLContext;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by socheat on 4/23/16.
 */
public class JavascriptTrigger extends CronTrigger {

    private final DSLContext context;
    private final String jobId;

    public JavascriptTrigger(DSLContext context, String jobId, String expression) {
        super(expression);
        this.jobId = jobId;
        this.context = context;
    }

    public JavascriptTrigger(DSLContext context, String jobId, String expression, TimeZone timeZone) {
        super(expression, timeZone);
        this.jobId = jobId;
        this.context = context;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date next = super.nextExecutionTime(triggerContext);
        JobTable jobTable = Tables.JOB.as("jobTable");
        JobRecord jobRecord = context.select(jobTable.fields()).from(jobTable).where(jobTable.JOB_ID.eq(this.jobId)).fetchOneInto(jobTable);
        if (jobRecord == null) {
            return null;
        }
        return next;
    }
}
