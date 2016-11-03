package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import java.util.List;

/**
 * Created by socheat on 4/10/16.
 */
public class JooqDb {

    public static void main(String args[]) throws Exception {
        XMLPropertiesConfiguration xml = Constants.getXmlPropertiesConfiguration();
        String jdbcUrl = "jdbc:mysql://" + xml.getString(Constants.APP_JDBC_HOSTNAME) + ":" + xml.getString(Constants.APP_JDBC_PORT) + "/" + xml.getString(Constants.APP_JDBC_DATABASE) + "?" + xml.getString(Constants.APP_JDBC_EXTRA);
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(xml.getString(Constants.APP_JDBC_DRIVER));
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(xml.getString(Constants.APP_JDBC_USERNAME));
        dataSource.setPassword(xml.getString(Constants.APP_JDBC_PASSWORD));

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
        Table<?> aaaaaTable = DSL.table("aaaaa");
        Field<String> aaaaaDocumentId = DSL.field(aaaaaTable.getName() + "." + "aaaaa_id", String.class);

        Table<?> eavVarcharTable = DSL.table("eav_varchar");
        Field<String> eavVarcharDocumentId = DSL.field(eavVarcharTable.getName() + "." + "document_id", String.class);
        Field<String> eavVarcharAttributeId = DSL.field(eavVarcharTable.getName() + "." + "attribute_id", String.class);
        Field<String> eavVarcharEavValue = DSL.field(eavVarcharTable.getName() + "." + "eav_value", String.class);

        List<String[]> pp = context
                .select(aaaaaDocumentId)
                .select(eavVarcharEavValue)
                .from(aaaaaTable)
                .leftJoin(eavVarcharTable).on(aaaaaDocumentId.eq(eavVarcharDocumentId)).and(eavVarcharAttributeId.eq("84c3cbd1-0ed7-4909-8048-1fa9c366c84d"))
                .fetchInto(String[].class);
        for (String[] p : pp) {
            System.out.println(StringUtils.join(p, ","));
        }
    }
}
