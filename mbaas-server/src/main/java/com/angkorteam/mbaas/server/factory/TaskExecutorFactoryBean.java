package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.spring.ApplicationContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by socheat on 4/25/16.
 */
public class TaskExecutorFactoryBean implements FactoryBean<TaskExecutor>, InitializingBean, ServletContextAware {

    private ServletContext servletContext;

    private TaskExecutor executor;

    @Override
    public TaskExecutor getObject() throws Exception {
        return this.executor;
    }

    @Override
    public Class<?> getObjectType() {
        return TaskExecutor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.executor = applicationContext.getExecutor();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
