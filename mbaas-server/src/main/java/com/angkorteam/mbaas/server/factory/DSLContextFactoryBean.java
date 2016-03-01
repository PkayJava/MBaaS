package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.ApplicationContext;
import org.jooq.DSLContext;
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
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.context = applicationContext.getDSLContext();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
