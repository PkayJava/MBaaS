package com.angkorteam.mbaas.server.wicket;

import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.Table;

/**
 * Created by socheat on 4/10/16.
 */
public class MySQLTrigger {

//    public static void main(String args[]) throws Exception {
//        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
//        String jdbcUrl = "jdbc:mysql://" + configuration.getString(Constants.TEMP_JDBC_HOSTNAME) + ":" + configuration.getString(Constants.TEMP_JDBC_PORT) + "/" + configuration.getString(Constants.TEMP_JDBC_DATABASE) + "?" + configuration.getString(Constants.TEMP_JDBC_EXTRA);
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName(configuration.getString(Constants.TEMP_JDBC_DRIVER));
//        dataSource.setUrl(jdbcUrl);
//        dataSource.setUsername(configuration.getString(Constants.TEMP_JDBC_USERNAME));
//        dataSource.setPassword(configuration.getString(Constants.TEMP_JDBC_PASSWORD));
//        DbSupport support = DbSupportFactory.createDbSupport(dataSource.getConnection(), true);
//        for (Table table : support.getSchema(support.getCurrentSchemaName()).allTables()) {
//            System.out.println(table.getName());
//        }
//
//        StringBuilder ddl = new StringBuilder();
//        ddl.append("CREATE TRIGGER `event_name` BEFORE/AFTER INSERT/UPDATE/DELETE ON `database`.`table`");
//        ddl.append("FOR EACH ROW BEGIN");
//        ddl.append("-- trigger body");
//        ddl.append("-- this code is applied to every");
//        ddl.append("-- inserted/updated/deleted row");
//        ddl.append("END;");
//    }
}
