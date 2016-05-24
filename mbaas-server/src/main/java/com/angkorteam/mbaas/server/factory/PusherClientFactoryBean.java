package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.service.PusherClient;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by socheat on 4/10/16.
 */
public class PusherClientFactoryBean implements FactoryBean<PusherClient>, InitializingBean, ServletContextAware {

    private PusherClient pusherClient;

    private ServletContext servletContext;

    @Override
    public PusherClient getObject() throws Exception {
        return this.pusherClient;
    }

    @Override
    public Class<?> getObjectType() {
        return PusherClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.pusherClient = applicationContext.getPusherClient();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

