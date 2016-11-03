package com.angkorteam.mbaas.server.factory;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class DSLContextFactoryBean implements FactoryBean<DSLContext>, InitializingBean, ServletContextAware {

    private DSLContext context;

    private ServletContext servletContext;

    private Configuration configuration;

    @Override
    public DSLContext getObject() throws Exception {
        return this.context;
    }

    @Override
    public Class<?> getObjectType() {
        return DSLContext.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.context = DSL.using(this.configuration);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
