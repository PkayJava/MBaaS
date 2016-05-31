package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.spring.ApplicationContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.script.ScriptEngineFactory;
import javax.servlet.ServletContext;

/**
 * Created by socheat on 5/31/16.
 */
public class ScriptEngineFactoryBean implements FactoryBean<ScriptEngineFactory>, InitializingBean, ServletContextAware {

    private ScriptEngineFactory scriptEngineFactory;

    private ServletContext servletContext;

    public ScriptEngineFactoryBean() {
    }

    @Override
    public ScriptEngineFactory getObject() throws Exception {
        return this.scriptEngineFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptEngineFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.scriptEngineFactory = applicationContext.getScriptEngineFactory();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}