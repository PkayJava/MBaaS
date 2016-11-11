package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.bean.Configuration;
import com.angkorteam.mbaas.server.bean.System;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class DataSourceFactoryBean implements FactoryBean<DataSource>, InitializingBean, ServletContextAware, DisposableBean {

    private BasicDataSource dataSource;

    private ServletContext servletContext;

    private System system;

    @Override
    public DataSource getObject() throws Exception {
        return this.dataSource;
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Configuration configuration = this.system.getConfiguration();
        String itest = java.lang.System.getProperty("itest");
        if (itest == null || "".equals(itest)) {
            String jdbcDriver = configuration.getString(Configuration.APP_JDBC_DRIVER);
            String jdbcUrl = "jdbc:mysql://" + configuration.getString(Configuration.APP_JDBC_HOSTNAME) + ":" + configuration.getString(Configuration.APP_JDBC_PORT) + "/" + configuration.getString(Configuration.APP_JDBC_DATABASE) + "?" + configuration.getString(Configuration.APP_JDBC_EXTRA);
            String username = configuration.getString(Configuration.APP_JDBC_USERNAME);
            String password = configuration.getString(Configuration.APP_JDBC_PASSWORD);
            this.dataSource = getDataSource(jdbcDriver, jdbcUrl, username, password);
        } else {
            String jdbcDriver = configuration.getString(Configuration.TEST_JDBC_DRIVER);
            String jdbcUrl = "jdbc:mysql://" + configuration.getString(Configuration.TEST_JDBC_HOSTNAME) + ":" + configuration.getString(Configuration.TEST_JDBC_PORT) + "/" + configuration.getString(Configuration.TEST_JDBC_DATABASE) + "?" + configuration.getString(Configuration.TEST_JDBC_EXTRA);
            String username = configuration.getString(Configuration.TEST_JDBC_USERNAME);
            String password = configuration.getString(Configuration.TEST_JDBC_PASSWORD);
            this.dataSource = getDataSource(jdbcDriver, jdbcUrl, username, password);
        }
    }

    protected BasicDataSource getDataSource(String jdbcDriver, String jdbcUrl, String username, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaxIdle(5);
        dataSource.setMinIdle(1);
        dataSource.setMaxWaitMillis(5000);
        dataSource.setMaxTotal(40);
        dataSource.setInitialSize(10);
        dataSource.setUrl(jdbcUrl);
        return dataSource;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void destroy() throws Exception {
        this.dataSource.close();
    }

    public void setSystem(System system) {
        this.system = system;
    }
}
