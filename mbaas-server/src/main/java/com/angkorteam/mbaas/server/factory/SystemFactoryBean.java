package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.bean.System;
import org.jooq.DSLContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by socheat on 10/23/16.
 */
public class SystemFactoryBean implements FactoryBean<System>, InitializingBean, ServletContextAware {

    private System system;

    private ServletContext servletContext;

    private DSLContext context;

    private JdbcTemplate jdbcTemplate;

    @Override
    public System getObject() throws Exception {
        return this.system;
    }

    @Override
    public Class<?> getObjectType() {
        return System.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.system = new System(this.context, this.jdbcTemplate);
    }

    public void setContext(DSLContext context) {
        this.context = context;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
