package com.angkorteam.mbaas.server.factory;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.script.ScriptEngine;
import javax.servlet.ServletContext;

/**
 * Created by socheat on 5/31/16.
 */
public class ScriptEngineFactoryBean implements FactoryBean<ScriptEngine>, InitializingBean, ServletContextAware {

    private ScriptEngine scriptEngine;

    private ServletContext servletContext;

    private ClassFilter classFilter;

    public ScriptEngineFactoryBean() {
    }

    @Override
    public ScriptEngine getObject() throws Exception {
        return this.scriptEngine;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NashornScriptEngineFactory scriptEngineFactory = new NashornScriptEngineFactory();
        this.scriptEngine = scriptEngineFactory.getScriptEngine(this.classFilter);
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.classFilter = classFilter;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}