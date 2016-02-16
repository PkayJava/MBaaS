package com.angkorteam.mbaas.factory;

import com.angkorteam.mbaas.ApplicationContext;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class FlywayFactoryBean implements FactoryBean<Flyway>, InitializingBean, ServletContextAware {

    private Flyway flyway;

    private ServletContext servletContext;

    @Override
    public Flyway getObject() throws Exception {
        return this.flyway;
    }

    @Override
    public Class<?> getObjectType() {
        return Flyway.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.flyway = applicationContext.getFlyway();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
