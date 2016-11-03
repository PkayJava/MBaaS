package com.angkorteam.mbaas.server.bean;

import org.apache.wicket.application.AbstractClassResolver;

/**
 * Created by socheat on 10/30/16.
 */
public class ClassResolver extends AbstractClassResolver {

    private GroovyClassLoader classLoader;

    public ClassResolver(GroovyClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

}
