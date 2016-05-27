package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.server.spring.ApplicationContext;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by socheat on 5/21/16.
 */
public class ApplicationDataSourceFactoryBean implements FactoryBean<ApplicationDataSourceFactoryBean.ApplicationDataSource>, InitializingBean, ServletContextAware {

    private ApplicationDataSource applicationDataSource;

    private ServletContext servletContext;

    @Override
    public ApplicationDataSource getObject() throws Exception {
        return this.applicationDataSource;
    }

    @Override
    public Class<?> getObjectType() {
        return ApplicationDataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.applicationDataSource = applicationContext.getApplicationDataSource();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public static class ApplicationDataSource {

        private final Map<String, BasicDataSource> dataSources;
        private final Map<String, JdbcTemplate> jdbcTemplates;
        private final Map<String, DSLContext> contexts;
        private final Map<String, Schema> dbSchemas;

        public ApplicationDataSource() {
            this.dataSources = new WeakHashMap<>();
            this.jdbcTemplates = new WeakHashMap<>();
            this.contexts = new WeakHashMap<>();
            this.dbSchemas = new WeakHashMap<>();
        }

        public BasicDataSource getDataSource(String applicationCode, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
            BasicDataSource dataSource = this.dataSources.get(applicationCode);
            if (dataSource == null) {
                dataSource = new BasicDataSource();
                XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
                String jdbcDriver = configuration.getString(Constants.APP_JDBC_DRIVER);
                dataSource.setDriverClassName(jdbcDriver);
                dataSource.setUsername(jdbcUsername);
                dataSource.setPassword(jdbcPassword);
                dataSource.setUrl(jdbcUrl);
                this.dataSources.put(applicationCode, dataSource);
            }
            return dataSource;
        }

        public JdbcTemplate getJdbcTemplate(String applicationCode, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
            DataSource dataSource = getDataSource(applicationCode, jdbcUrl, jdbcUsername, jdbcPassword);
            if (dataSource == null) {
                return null;
            }
            JdbcTemplate jdbcTemplate = this.jdbcTemplates.get(applicationCode);
            if (jdbcTemplate == null) {
                jdbcTemplate = new JdbcTemplate(dataSource);
                this.jdbcTemplates.put(applicationCode, jdbcTemplate);
            }
            return jdbcTemplate;
        }

        public Schema getDbSchema(String applicationCode, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
            DataSource dataSource = getDataSource(applicationCode, jdbcUrl, jdbcUsername, jdbcPassword);
            if (dataSource == null) {
                return null;
            }
            Schema schema = this.dbSchemas.get(applicationCode);
            if (schema == null) {
                try {
                    DbSupport dbSupport = DbSupportFactory.createDbSupport(dataSource.getConnection(), true);
                    schema = dbSupport.getSchema(applicationCode);
                    this.dbSchemas.put(applicationCode, schema);
                } catch (SQLException e) {
                }
            }
            return schema;
        }

        public DSLContext getDSLContext(String applicationCode, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
            BasicDataSource dataSource = getDataSource(applicationCode, jdbcUrl, jdbcUsername, jdbcPassword);
            if (dataSource == null) {
                return null;
            }
            DSLContext context = this.contexts.get(applicationCode);
            if (context == null) {
                XMLPropertiesConfiguration xml = Constants.getXmlPropertiesConfiguration();
                MappedSchema mappedSchema = new MappedSchema();
                mappedSchema.withInput(xml.getString(Constants.TEMP_JDBC_DATABASE));
                mappedSchema.withOutput(applicationCode);
                RenderMapping renderMapping = new RenderMapping();
                renderMapping.withSchemata(mappedSchema);
                Settings settings = new Settings();
                settings.withRenderMapping(renderMapping);
                settings.withExecuteWithOptimisticLocking(true);
                settings.setUpdatablePrimaryKeys(false);

                DefaultConfiguration configuration = new DefaultConfiguration();
                configuration.setSettings(settings);
                configuration.setDataSource(dataSource);

                if ("com.mysql.cj.jdbc.Driver".equals(dataSource.getDriverClassName())) {
                    configuration.set(SQLDialect.MYSQL);
                } else if ("com.mysql.jdbc.Driver".equals(dataSource.getDriverClassName())) {
                    configuration.set(SQLDialect.MYSQL);
                } else if ("org.hsqldb.jdbcDriver".equals(dataSource.getDriverClassName())) {
                    configuration.set(SQLDialect.HSQLDB);
                } else if ("org.mariadb.jdbc.Driver".equals(dataSource.getDriverClassName())) {
                    configuration.set(SQLDialect.MARIADB);
                }
                context = DSL.using(configuration);
                this.contexts.put(applicationCode, context);
            }
            return context;
        }
    }
}
