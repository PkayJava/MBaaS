package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by socheatkhauv on 10/26/16.
 */
public class GroovyClassLoaderFactoryBean implements FactoryBean<GroovyClassLoader>, InitializingBean {

    private GroovyClassLoader loader;

    @Override
    public GroovyClassLoader getObject() throws Exception {
        return this.loader;
    }

    @Override
    public Class<?> getObjectType() {
        return GroovyClassLoader.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.loader = new GroovyClassLoader();

    }
}
