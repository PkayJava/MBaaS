package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import com.angkorteam.mbaas.server.nashorn.JavascripUtils;
import com.angkorteam.mbaas.server.nashorn.MBaaS;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Date;
import java.util.Map;

/**
 * Created by socheat on 4/23/16.
 */
public final class JavascriptJob implements Runnable {

    private final ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource;

    private final DSLContext context;

    private final String jobId;

    private final String applicationCode;

    private final ScriptEngineFactory scriptEngineFactory;

    private final ClassFilter classFilter;

    public JavascriptJob(ScriptEngineFactory scriptEngineFactory, ClassFilter classFilter, DSLContext context, ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource, String applicationCode, String jobId) {
        this.context = context;
        this.jobId = jobId;
        this.scriptEngineFactory = scriptEngineFactory;
        this.applicationDataSource = applicationDataSource;
        this.applicationCode = applicationCode;
        this.classFilter = classFilter;
    }

    @Override
    public final void run() {
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ApplicationRecord applicationRecord = this.context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.CODE.eq(this.applicationCode)).fetchOneInto(applicationTable);
        String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
        JdbcTemplate jdbcTemplate = this.applicationDataSource.getJdbcTemplate(this.applicationCode, jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
        DSLContext context = this.applicationDataSource.getDSLContext(this.applicationCode, jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
        if (jdbcTemplate == null) {
            return;
        }
        Map<String, Object> jobRecord = null;
        jobRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JOB + " WHERE " + Jdbc.Job.JOB_ID + " = ?", this.jobId);
        if (jobRecord == null || jobRecord.get(Jdbc.Job.SECURITY).equals(SecurityEnum.Denied.getLiteral())) {
            return;
        }
        long start;
        try {
            ScriptEngine engine = null;
            if (this.scriptEngineFactory instanceof NashornScriptEngineFactory) {
                engine = ((NashornScriptEngineFactory) this.scriptEngineFactory).getScriptEngine(this.classFilter);
            } else {
                engine = this.scriptEngineFactory.getScriptEngine();
            }
            Bindings bindings = engine.createBindings();
            engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
            bindings.put("MBaaS", new MBaaS(context, null, jdbcTemplate, null));
            bindings.put("Context", context);
            JavascripUtils.eval(engine);
            String javascript = (String) jobRecord.get(Jdbc.Job.JAVASCRIPT);
            start = System.currentTimeMillis();
            engine.eval(javascript);
            start = System.currentTimeMillis() - start;
            double consume = ((double) start) / 1000d;
            jdbcTemplate.update("UPDATE " + Jdbc.JOB + " SET " + Jdbc.Job.CONSUME + " = ?, " + Jdbc.Job.ERROR_CLASS + " = ?, " + Jdbc.Job.ERROR_MESSAGE + " = ?, " + Jdbc.Job.DATE_LAST_EXECUTED + " = ? WHERE " + Jdbc.Job.JOB_ID + " = ?", consume, "", "", new Date(), jobRecord.get(Jdbc.Job.JOB_ID));
        } catch (Throwable e) {
            start = System.currentTimeMillis();
            start = System.currentTimeMillis() - start;
            double consume = ((double) start) / 1000d;
            jdbcTemplate.update("UPDATE " + Jdbc.JOB + " SET " + Jdbc.Job.CONSUME + " = ?, " + Jdbc.Job.ERROR_CLASS + " = ?, " + Jdbc.Job.ERROR_MESSAGE + " = ?, " + Jdbc.Job.DATE_LAST_EXECUTED + " = ? WHERE " + Jdbc.Job.JOB_ID + " = ?", consume, "", "", new Date(), jobRecord.get(Jdbc.Job.JOB_ID));
        }
    }

}
