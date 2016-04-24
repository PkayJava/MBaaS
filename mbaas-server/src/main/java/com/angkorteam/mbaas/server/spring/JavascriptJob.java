package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JobTable;
import com.angkorteam.mbaas.model.entity.tables.records.JobRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.nashorn.JavaFilter;
import com.angkorteam.mbaas.server.nashorn.JavascripUtils;
import com.angkorteam.mbaas.server.nashorn.MBaaS;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import java.util.Date;

/**
 * Created by socheat on 4/23/16.
 */
public final class JavascriptJob implements Runnable {

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    private final String jobId;

    public JavascriptJob(DSLContext context, JdbcTemplate jdbcTemplate, String jobId) {
        this.jobId = jobId;
        this.jdbcTemplate = jdbcTemplate;
        this.context = context;
    }

    @Override
    public final void run() {
        JobTable jobTable = Tables.JOB.as("jobTable");
        JobRecord jobRecord = context.select(jobTable.fields()).from(jobTable).where(jobTable.JOB_ID.eq(jobId)).fetchOneInto(jobTable);
        if (jobRecord == null || jobRecord.getSecurity().equals(SecurityEnum.Denied.getLiteral())) {
            return;
        }
        try {
            NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
            ScriptEngine engine = factory.getScriptEngine(new JavaFilter(context));
            Bindings bindings = engine.createBindings();
            engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
            bindings.put("MBaaS", new MBaaS(context, null, jdbcTemplate, null));
            bindings.put("Context", context);
            JavascripUtils.eval(engine);
            String javascript = jobRecord.getJavascript();
            engine.eval(javascript);
            jobRecord.setErrorClass("");
            jobRecord.setErrorMessage("");
            jobRecord.setDateLastExecuted(new Date());
            jobRecord.update();
        } catch (Throwable e) {
            jobRecord.setErrorClass(e.getClass().getSimpleName());
            jobRecord.setErrorMessage(e.getMessage());
            jobRecord.setDateLastExecuted(new Date());
            jobRecord.update();
        }
    }

}
