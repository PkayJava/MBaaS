package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.bean.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class DataSourceFactoryBean implements FactoryBean<DataSource>, InitializingBean, ServletContextAware, DisposableBean {

    private BasicDataSource dataSource;

    private ServletContext servletContext;

    private Configuration configuration;

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
        String configurationFile = this.servletContext.getInitParameter("configuration");
        File file;
        if (!Strings.isNullOrEmpty(configurationFile)) {
            file = new File(configurationFile);
        } else {
            File home = new File(java.lang.System.getProperty("user.home"));
            file = new File(home, ".xml/" + Configuration.KEY);
        }
        try {
            this.configuration = new Configuration(file);
        } catch (ConfigurationException e) {
        }

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
}
