package com.angkorteam.mbaas.server.factory;

import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by socheat on 10/23/16.
 */
public class DbSupportFactoryBean implements FactoryBean<DbSupport>, InitializingBean {

    private DbSupport dbSupport;

    private DataSource dataSource;

    @Override
    public DbSupport getObject() throws Exception {
        return this.dbSupport;
    }

    @Override
    public Class<?> getObjectType() {
        return DbSupport.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Connection connection = this.dataSource.getConnection();
        this.dbSupport = DbSupportFactory.createDbSupport(connection, true);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
