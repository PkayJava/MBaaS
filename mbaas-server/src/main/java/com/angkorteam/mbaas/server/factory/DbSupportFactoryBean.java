package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.spring.ApplicationContext;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by socheat on 5/21/16.
 */
public class DbSupportFactoryBean implements FactoryBean<DbSupport>, InitializingBean, ServletContextAware {

    private ServletContext servletContext;

    private DbSupport dbSupport;

    public DbSupportFactoryBean() {
    }

    @Override
    public DbSupport getObject() throws Exception {
        return this.dbSupport;
    }

    @Override
    public Class<?> getObjectType() {
        return DbSupport.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.dbSupport = applicationContext.getDbSupport();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
