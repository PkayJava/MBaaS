package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.configuration.Constants;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
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
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String itest = System.getProperty("itest");
        if (itest == null || "".equals(itest)) {
            String jdbcDriver = configuration.getString(Constants.APP_JDBC_DRIVER);
            String jdbcUrl = "jdbc:mysql://" + configuration.getString(Constants.APP_JDBC_HOSTNAME) + ":" + configuration.getString(Constants.APP_JDBC_PORT) + "/" + configuration.getString(Constants.APP_JDBC_DATABASE) + "?" + configuration.getString(Constants.APP_JDBC_EXTRA);
            String username = configuration.getString(Constants.APP_JDBC_USERNAME);
            String password = configuration.getString(Constants.APP_JDBC_PASSWORD);
            this.dataSource = getDataSource(jdbcDriver, jdbcUrl, username, password);
        } else {
            String jdbcDriver = configuration.getString(Constants.TEST_JDBC_DRIVER);
            String jdbcUrl = "jdbc:mysql://" + configuration.getString(Constants.TEST_JDBC_HOSTNAME) + ":" + configuration.getString(Constants.TEST_JDBC_PORT) + "/" + configuration.getString(Constants.TEST_JDBC_DATABASE) + "?" + configuration.getString(Constants.TEST_JDBC_EXTRA);
            String username = configuration.getString(Constants.TEST_JDBC_USERNAME);
            String password = configuration.getString(Constants.TEST_JDBC_PASSWORD);
            this.dataSource = getDataSource(jdbcDriver, jdbcUrl, username, password);
        }
    }

    protected BasicDataSource getDataSource(String jdbcDriver, String jdbcUrl, String username, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
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
