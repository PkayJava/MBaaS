package com.angkorteam.mbaas.factory;

import com.angkorteam.mbaas.ApplicationContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jooq.DSLContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class GsonFactory implements FactoryBean<Gson>, InitializingBean {

    private Gson gson;

    @Override
    public Gson getObject() throws Exception {
        return this.gson;
    }

    @Override
    public Class<?> getObjectType() {
        return Gson.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.gson = new GsonBuilder().create();
    }
}
