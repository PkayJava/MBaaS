package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JobTable;
import com.angkorteam.mbaas.model.entity.tables.records.JobRecord;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import com.angkorteam.mbaas.server.spring.JavascriptJob;
import com.angkorteam.mbaas.server.spring.JavascriptTrigger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 4/23/16.
 */
public class JavascriptServiceFactoryBean implements FactoryBean<JavascriptServiceFactoryBean.JavascriptService>, InitializingBean, ServletContextAware {

    private JavascriptService javascriptService;

    private ServletContext servletContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.javascriptService = applicationContext.getJavascriptService();
    }

    @Override
    public JavascriptService getObject() throws Exception {
        return this.javascriptService;
    }

    @Override
    public Class<?> getObjectType() {
        return JavascriptService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public static class JavascriptService {

        private final List<String> jobs = new ArrayList<>();

        private final DSLContext context;

        private final TaskScheduler scheduler;

        private final JdbcTemplate jdbcTemplate;

        public JavascriptService(DSLContext context, JdbcTemplate jdbcTemplate, TaskScheduler scheduler) {
            this.context = context;
            this.scheduler = scheduler;
            this.jdbcTemplate = jdbcTemplate;
        }

        public void schedule(String jobId) {
            if (jobs.contains(jobId)) {
                return;
            }
            JobTable jobTable = Tables.JOB.as("jobTable");
            JobRecord jobRecord = context.select(jobTable.fields()).from(jobTable).where(jobTable.JOB_ID.eq(jobId)).fetchOneInto(jobTable);
            if (jobRecord != null) {
                try {
                    this.scheduler.schedule(new JavascriptJob(context, jdbcTemplate, jobId), new JavascriptTrigger(context, jobId, jobRecord.getCron()));
                    jobs.add(jobId);
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }
}
