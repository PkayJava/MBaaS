package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.configuration.Constants;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.Table;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

/**
 * Created by socheat on 4/10/16.
 */
public class JooqDb {

    public static void main(String args[]) throws Exception {
        XMLPropertiesConfiguration xml = Constants.getXmlPropertiesConfiguration();
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(xml.getString(Constants.TEMP_JDBC_DRIVER));
        dataSource.setUrl(xml.getString(Constants.TEMP_JDBC_URL));
        dataSource.setUsername(xml.getString(Constants.TEMP_JDBC_USERNAME));
        dataSource.setPassword(xml.getString(Constants.TEMP_JDBC_PASSWORD));

        RenderMapping renderMapping = new RenderMapping();

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

        DSLContext context = DSL.using(configuration);
    }
}
