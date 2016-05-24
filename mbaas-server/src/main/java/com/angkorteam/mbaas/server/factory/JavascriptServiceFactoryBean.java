package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import com.angkorteam.mbaas.server.spring.JavascriptJob;
import com.angkorteam.mbaas.server.spring.JavascriptTrigger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        private final TaskScheduler scheduler;

        private final ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource;

        private final DSLContext context;

        public JavascriptService(DSLContext context, ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource, TaskScheduler scheduler) {
            this.scheduler = scheduler;
            this.context = context;
            this.applicationDataSource = applicationDataSource;
        }

        public void schedule(String applicationCode, String jobId) {
            if (jobs.contains(applicationCode + "=>" + jobId)) {
                return;
            }
            JdbcTemplate jdbcTemplate = this.applicationDataSource.getJdbcTemplate(applicationCode);
            Map<String, Object> jobRecord = null;
            jobRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JOB + " WHERE " + Jdbc.Job.JOB_ID + " = ?", jobId);
            if (jobRecord != null) {
                try {
                    this.scheduler.schedule(new JavascriptJob(this.context, this.applicationDataSource, applicationCode, jobId), new JavascriptTrigger(this.applicationDataSource, applicationCode, jobId, (String) jobRecord.get("cron")));
                    jobs.add(jobId);
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }
}
