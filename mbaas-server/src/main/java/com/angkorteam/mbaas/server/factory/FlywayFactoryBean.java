package com.angkorteam.mbaas.server.factory;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.util.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class FlywayFactoryBean implements FactoryBean<Flyway>, InitializingBean, ServletContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayFactoryBean.class);

    private Flyway flyway;

    private ServletContext servletContext;

    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private DbSupport dbSupport;

    private String[] location;

    @Override
    public Flyway getObject() throws Exception {
        return this.flyway;
    }

    @Override
    public Class<?> getObjectType() {
        return Flyway.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (int i = 0; i < this.location.length; i++) {
            String value = this.location[i];
            if (!value.startsWith("classpath:")) {
                this.location[i] = Location.FILESYSTEM_PREFIX + servletContext.getRealPath(value);
            }
        }
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(this.dataSource);
        flyway.setLocations(this.location);
        FlywayException error = null;
        try {
            flyway.migrate();
        } catch (FlywayException e) {
            LOGGER.info(e.getMessage());
            error = e;
        }
        int max = 10;
        while (error != null && max <= 10) {
            max++;
            try {
                jdbcTemplate.execute("DROP DATABASE " + this.dbSupport.getCurrentSchemaName());
                jdbcTemplate.execute("CREATE DATABASE " + this.dbSupport.getCurrentSchemaName());
                flyway.migrate();
                error = null;
            } catch (FlywayException e) {
                LOGGER.info(e.getMessage());
                error = e;
            }
        }
        LOGGER.info("flyway db is migrated");
        this.flyway = flyway;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setDbSupport(DbSupport dbSupport) {
        this.dbSupport = dbSupport;
    }

    public void setLocation(String[] location) {
        this.location = location;
    }
}
