package com.angkorteam.mbaas;

import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by Khauv Socheat on 2/3/2016.
 */
public class ApplicationContext implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    public static final String KEY = ApplicationContext.class.getName();

    private BasicDataSource dataSource;

    private Configuration configuration;

    private DSLContext dslContext;

    private StringEncryptor stringEncryptor;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        this.dataSource = initDataSource();
        initFlyway(dataSource);
        this.configuration = initConfiguration(dataSource);
        this.dslContext = initDSLContext(configuration);
        this.stringEncryptor = initStringEncryptor();
        servletContext.setAttribute(KEY, this);
    }

    public static ApplicationContext get(ServletContext servletContext) {
        return (ApplicationContext) servletContext.getAttribute(ApplicationContext.KEY);
    }

    protected void initFlyway(DataSource dataSource) {
        if (dataSource == null) {
            return;
        }
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }

    protected BasicDataSource initDataSource() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        BasicDataSource dataSource = new BasicDataSource();
        String itest = System.getProperty("itest");
        if (itest == null || "".equals(itest)) {
            dataSource.setDriverClassName(configuration.getString(Constants.APP_JDBC_DRIVER));
            dataSource.setUsername(configuration.getString(Constants.APP_JDBC_USERNAME));
            dataSource.setPassword(configuration.getString(Constants.APP_JDBC_PASSWORD));
            dataSource.setUrl(configuration.getString(Constants.APP_JDBC_URL));
        } else {
            dataSource.setDriverClassName(configuration.getString(Constants.TEST_JDBC_DRIVER));
            dataSource.setUsername(configuration.getString(Constants.TEST_JDBC_USERNAME));
            dataSource.setPassword(configuration.getString(Constants.TEST_JDBC_PASSWORD));
            dataSource.setUrl(configuration.getString(Constants.TEST_JDBC_URL));
        }
        return dataSource;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (this.dataSource != null) {
            try {
                this.dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public final DataSource getDataSource() {
        return dataSource;
    }

    protected DSLContext initDSLContext(Configuration configuration) {
        return DSL.using(configuration);
    }

    protected Configuration initConfiguration(BasicDataSource dataSource) {
        if (dataSource == null) {
            return null;
        }
        XMLPropertiesConfiguration xml = Constants.getXmlPropertiesConfiguration();
        MappedSchema mappedSchema = new MappedSchema();
        mappedSchema.withInput("mbaas_temp");
        String itest = System.getProperty("itest");
        if (itest == null || "".equals(itest)) {
            mappedSchema.withOutput(xml.getString(Constants.APP_JDBC_DATABASE));
        } else {
            mappedSchema.withOutput(xml.getString(Constants.TEST_JDBC_DATABASE));
        }
        RenderMapping renderMapping = new RenderMapping();
        renderMapping.withSchemata(mappedSchema);
        Settings settings = new Settings();
        settings.withRenderMapping(renderMapping);
        settings.withExecuteWithOptimisticLocking(true);
        settings.setUpdatablePrimaryKeys(false);

        DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.setSettings(settings);
        configuration.setDataSource(this.dataSource);

        if ("com.mysql.jdbc.Driver".equals(dataSource.getDriverClassName())) {
            configuration.set(SQLDialect.MYSQL);
        } else if ("org.hsqldb.jdbcDriver".equals(dataSource.getDriverClassName())) {
            configuration.set(SQLDialect.HSQLDB);
        } else if ("org.mariadb.jdbc.Driver".equals(dataSource.getDriverClassName())) {
            configuration.set(SQLDialect.MARIADB);
        }
        return configuration;
    }

    protected StringEncryptor initStringEncryptor() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(configuration.getString(Constants.ENCRYPTION_PASSWORD));
        encryptor.setStringOutputType(configuration.getString(Constants.ENCRYPTION_OUTPUT));
        return encryptor;
    }

    public final DSLContext getDSLContext() {
        return dslContext;
    }

    public final StringEncryptor getStringEncryptor() {
        return stringEncryptor;
    }
}
