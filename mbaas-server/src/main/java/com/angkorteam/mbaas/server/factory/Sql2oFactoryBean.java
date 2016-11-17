package com.angkorteam.mbaas.server.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.sql2o.Sql2o;

import javax.sql.DataSource;

/**
 * Created by socheat on 11/17/16.
 */
public class Sql2oFactoryBean implements FactoryBean<Sql2o>, InitializingBean {

    private Sql2o sql2o;

    private DataSource dataSource;

    @Override
    public Sql2o getObject() throws Exception {
        return this.sql2o;
    }

    @Override
    public Class<?> getObjectType() {
        return Sql2o.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.sql2o = new Sql2o(this.dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
