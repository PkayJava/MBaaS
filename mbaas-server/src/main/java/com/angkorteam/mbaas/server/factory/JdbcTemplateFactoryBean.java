package com.angkorteam.mbaas.server.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class JdbcTemplateFactoryBean implements FactoryBean<JdbcTemplate>, InitializingBean {

    private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    @Override
    public JdbcTemplate getObject() throws Exception {
        return this.jdbcTemplate;
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
