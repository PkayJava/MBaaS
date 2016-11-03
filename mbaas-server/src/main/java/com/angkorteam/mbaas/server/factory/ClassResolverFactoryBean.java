package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.bean.ClassResolver;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by socheat on 10/30/16.
 */
public class ClassResolverFactoryBean implements FactoryBean<ClassResolver>, InitializingBean {

    private ClassResolver classResolver;

    private GroovyClassLoader classLoader;

    public ClassResolverFactoryBean() {
    }

    @Override
    public ClassResolver getObject() throws Exception {
        return this.classResolver;
    }

    @Override
    public Class<?> getObjectType() {
        return ClassResolver.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.classResolver = new ClassResolver(this.classLoader);
    }

    public void setClassLoader(GroovyClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
