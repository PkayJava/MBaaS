package com.angkorteam.mbaas;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.ColumnEnum;
import com.angkorteam.mbaas.plain.enums.IndexInfoEnum;
import com.angkorteam.mbaas.plain.enums.PrimaryKeyEnum;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.wicket.WicketRuntimeException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.Table;
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
import java.sql.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 2/3/2016.
 */
public class ApplicationContext implements ServletContextListener {


    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    public static final String KEY = ApplicationContext.class.getName();

    private BasicDataSource dataSource;

    private Configuration configuration;

    private DSLContext context;

    private StringEncryptor stringEncryptor;

    private Flyway flyway;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        this.dataSource = initDataSource();
        this.flyway = initFlyway(dataSource);
        this.configuration = initConfiguration(dataSource);
        this.context = initDSLContext(configuration);
        this.stringEncryptor = initStringEncryptor();
        initRole(context);
        initUser(context);
        initDDL(context, dataSource);
        servletContext.setAttribute(KEY, this);
    }

    protected void initUser(DSLContext context) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        User userTable = Tables.USER.as("userTable");
        Role roleTable = Tables.ROLE.as("roleTable");

        UserRecord adminRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(configuration.getString(Constants.USER_ADMIN))).fetchOneInto(userTable);
        if (adminRecord == null) {
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(configuration.getString(Constants.USER_ADMIN_ROLE)))).fetchOneInto(roleTable);
            adminRecord = context.newRecord(userTable);
            adminRecord.setDeleted(false);
            adminRecord.setAccountNonExpired(true);
            adminRecord.setAccountNonLocked(true);
            adminRecord.setCredentialsNonExpired(true);
            adminRecord.setDisabled(false);
            adminRecord.setLogin(configuration.getString(Constants.USER_ADMIN));
            adminRecord.setPassword(configuration.getString(Constants.USER_ADMIN_PASSWORD));
            adminRecord.setRoleId(roleRecord.getRoleId());
            adminRecord.store();
        }

        UserRecord mbaasRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(configuration.getString(Constants.USER_MBAAS))).fetchOneInto(userTable);
        if (mbaasRecord == null) {
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(configuration.getString(Constants.USER_MBAAS_ROLE)))).fetchOneInto(roleTable);
            mbaasRecord = context.newRecord(userTable);
            mbaasRecord.setDeleted(false);
            mbaasRecord.setAccountNonExpired(true);
            mbaasRecord.setAccountNonLocked(true);
            mbaasRecord.setCredentialsNonExpired(true);
            mbaasRecord.setDisabled(false);
            mbaasRecord.setLogin(configuration.getString(Constants.USER_MBAAS));
            mbaasRecord.setPassword(configuration.getString(Constants.USER_MBAAS_PASSWORD));
            mbaasRecord.setRoleId(roleRecord.getRoleId());
            mbaasRecord.store();
        }

        UserRecord internalAdminRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(configuration.getString(Constants.USER_INTERNAL_ADMIN))).fetchOneInto(userTable);
        if (internalAdminRecord == null) {
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(configuration.getString(Constants.USER_INTERNAL_ADMIN_ROLE)))).fetchOneInto(roleTable);
            internalAdminRecord = context.newRecord(userTable);
            internalAdminRecord.setDeleted(false);
            internalAdminRecord.setAccountNonExpired(true);
            internalAdminRecord.setAccountNonLocked(true);
            internalAdminRecord.setCredentialsNonExpired(true);
            internalAdminRecord.setDisabled(false);
            internalAdminRecord.setLogin(configuration.getString(Constants.USER_INTERNAL_ADMIN));
            internalAdminRecord.setPassword(configuration.getString(Constants.USER_INTERNAL_ADMIN_PASSWORD));
            internalAdminRecord.setRoleId(roleRecord.getRoleId());
            internalAdminRecord.store();
        }
    }

    protected void initRole(DSLContext context) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        Role roleTable = Tables.ROLE.as("roleTable");

        RoleRecord administratorRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_ADMINISTRATOR))).fetchOneInto(roleTable);
        if (administratorRecord == null) {
            administratorRecord = context.newRecord(roleTable);
            administratorRecord.setName(configuration.getString(Constants.ROLE_ADMINISTRATOR));
            administratorRecord.setDescription(configuration.getString(Constants.ROLE_ADMINISTRATOR_DESCRIPTION));
            administratorRecord.setDeleted(false);
            administratorRecord.store();
        }

        RoleRecord backofficeRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_BACKOFFICE))).fetchOneInto(roleTable);
        if (backofficeRecord == null) {
            backofficeRecord = context.newRecord(roleTable);
            backofficeRecord.setName(configuration.getString(Constants.ROLE_BACKOFFICE));
            backofficeRecord.setDescription(configuration.getString(Constants.ROLE_BACKOFFICE_DESCRIPTION));
            backofficeRecord.setDeleted(false);
            backofficeRecord.store();
        }

        RoleRecord registeredRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_REGISTERED))).fetchOneInto(roleTable);
        if (registeredRecord == null) {
            registeredRecord = context.newRecord(roleTable);
            registeredRecord.setName(configuration.getString(Constants.ROLE_REGISTERED));
            registeredRecord.setDescription(configuration.getString(Constants.ROLE_REGISTERED_DESCRIPTION));
            registeredRecord.setDeleted(false);
            registeredRecord.store();
        }

        RoleRecord anonymousRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_ANONYMOUS))).fetchOneInto(roleTable);
        if (anonymousRecord == null) {
            anonymousRecord = context.newRecord(roleTable);
            anonymousRecord.setName(configuration.getString(Constants.ROLE_ANONYMOUS));
            anonymousRecord.setDescription(configuration.getString(Constants.ROLE_ANONYMOUS_DESCRIPTION));
            anonymousRecord.setDeleted(false);
            anonymousRecord.store();
        }
    }

    protected void initDDL(DSLContext context, DataSource dataSource) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        com.angkorteam.mbaas.model.entity.tables.Table tableTable = Tables.TABLE.as("tableTable");
        Field fieldTable = Tables.FIELD.as("fieldTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Index indexTable = Tables.INDEX.as("indexTable");
        User userTable = Tables.USER.as("userTable");

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(configuration.getString(Constants.USER_ADMIN))).fetchOneInto(userTable);

        try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            DbSupport databaseSupport = DbSupportFactory.createDbSupport(connection, true);
            for (Table table : databaseSupport.getCurrentSchema().allTables()) {
                TableRecord tableRecord = context.select(tableTable.fields()).from(tableTable)
                        .where(tableTable.NAME.eq(table.getName()))
                        .fetchOneInto(tableTable);
                if (tableRecord == null) {
                    tableRecord = context.newRecord(tableTable);
                    tableRecord.setName(table.getName());
                    tableRecord.setSystem(true);
                    tableRecord.setOwnerUserId(userRecord.getUserId());
                    tableRecord.store();
                }
                List<FieldRecord> temporaryFieldRecords = context.select(fieldTable.fields()).from(fieldTable)
                        .where(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                        .and(fieldTable.VIRTUAL.eq(false))
                        .fetchInto(fieldTable);
                Map<String, FieldRecord> fieldRecords = new LinkedHashMap<>();
                for (FieldRecord fieldRecord : temporaryFieldRecords) {
                    fieldRecords.put(fieldRecord.getName(), fieldRecord);
                }
                {
                    ResultSet resultSet = databaseMetaData.getColumns(null, null, table.getName(), null);
                    while (resultSet.next()) {
                        String columnName = resultSet.getString(ColumnEnum.COLUMN_NAME.getLiteral());
                        if (!fieldRecords.containsKey(columnName)) {
                            FieldRecord fieldRecord = context.newRecord(fieldTable);
                            fieldRecord.setTableId(tableRecord.getTableId());
                            fieldRecord.setName(columnName);
                            fieldRecord.setNullable(resultSet.getBoolean(ColumnEnum.NULLABLE.getLiteral()));
                            fieldRecord.setAutoIncrement(resultSet.getBoolean(ColumnEnum.IS_AUTOINCREMENT.getLiteral()));
                            fieldRecord.setSystem(true);
                            fieldRecord.setVirtual(false);

                            int dataType = resultSet.getInt(ColumnEnum.DATA_TYPE.getLiteral());
                            String typeName = resultSet.getString(ColumnEnum.TYPE_NAME.getLiteral());

                            if (dataType == Types.LONGVARBINARY) {
                                fieldRecord.setExposed(false);
                            } else {
                                fieldRecord.setExposed(true);
                            }

                            if (dataType == Types.INTEGER) {
                                fieldRecord.setJavaType(Integer.class.getName());
                                fieldRecord.setSqlType(typeName);
                            } else if (dataType == Types.VARCHAR) {
                                fieldRecord.setJavaType(String.class.getName());
                                fieldRecord.setSqlType(typeName);
                            } else if (dataType == Types.TIMESTAMP) {
                                fieldRecord.setJavaType(Date.class.getName());
                                fieldRecord.setSqlType(typeName);
                            } else if (dataType == Types.LONGVARBINARY) {
                                fieldRecord.setJavaType(Byte.class.getName() + "[]");
                                fieldRecord.setSqlType(typeName);
                            } else if (dataType == Types.BIT) {
                                fieldRecord.setJavaType(Boolean.class.getName());
                                fieldRecord.setSqlType(typeName);
                            } else {
                                throw new WicketRuntimeException("field type unknown " + dataType + " => " + typeName);
                            }
                            fieldRecord.store();
                        } else {
                            fieldRecords.remove(columnName);
                        }
                    }
                }
                for (Map.Entry<String, FieldRecord> entry : fieldRecords.entrySet()) {
                    context.delete(fieldTable)
                            .where(fieldTable.FIELD_ID.eq(entry.getValue().getFieldId()))
                            .execute();
                }
                {
                    Map<Integer, FieldRecord> blobRecords = new LinkedHashMap<>();
                    for (FieldRecord blobRecord : context.select(fieldTable.fields()).from(fieldTable)
                            .where(fieldTable.SQL_TYPE.eq("BLOB"))
                            .and(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                            .and(fieldTable.VIRTUAL.eq(false))
                            .fetchInto(fieldTable)) {
                        blobRecords.put(blobRecord.getFieldId(), blobRecord);
                    }
                    if (blobRecords == null || blobRecords.isEmpty()) {
                        context.delete(fieldTable)
                                .where(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                                .and(fieldTable.VIRTUAL.eq(true))
                                .execute();
                    } else {
                        List<FieldRecord> virtualRecords = context.select(fieldTable.fields()).from(fieldTable)
                                .where(fieldTable.VIRTUAL.eq(true))
                                .and(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                                .fetchInto(fieldTable);
                        for (FieldRecord virtualRecord : virtualRecords) {
                            if (!blobRecords.containsKey(virtualRecord.getVirtualFieldId())) {
                                context.delete(fieldTable)
                                        .where(fieldTable.FIELD_ID.eq(virtualRecord.getFieldId()))
                                        .execute();
                            }
                        }
                    }
                }
                {
                    ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, table.getName());
                    while (resultSet.next()) {
                        String columnName = resultSet.getString(PrimaryKeyEnum.COLUMN_NAME.getLiteral());
                        FieldRecord fieldRecord = context.select(fieldTable.fields()).from(fieldTable)
                                .where(fieldTable.NAME.eq(columnName))
                                .and(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                                .and(fieldTable.VIRTUAL.eq(false))
                                .fetchOneInto(fieldTable);
                        PrimaryRecord primaryRecord = context.select(primaryTable.fields()).from(primaryTable)
                                .where(primaryTable.FIELD_ID.eq(fieldRecord.getFieldId()))
                                .and(primaryTable.TABLE_ID.eq(tableRecord.getTableId()))
                                .fetchOneInto(primaryTable);
                        if (primaryRecord == null) {
                            primaryRecord = context.newRecord(primaryTable);
                            primaryRecord.setFieldId(fieldRecord.getFieldId());
                            primaryRecord.setTableId(tableRecord.getTableId());
                            primaryRecord.store();
                        }
                    }
                }
                List<IndexRecord> temporaryIndexRecords = context.select(indexTable.fields()).from(indexTable)
                        .where(indexTable.TABLE_ID.eq(tableRecord.getTableId()))
                        .fetchInto(indexTable);
                Map<String, IndexRecord> indexRecords = new LinkedHashMap<>();
                for (IndexRecord indexRecord : temporaryIndexRecords) {
                    String key = tableRecord.getTableId() + indexRecord.getName() + indexRecord.getFieldId();
                    indexRecords.put(key, indexRecord);
                }
                {
                    ResultSet resultSet = databaseMetaData.getIndexInfo(null, null, table.getName(), false, false);
                    while (resultSet.next()) {
                        String indexName = resultSet.getString(IndexInfoEnum.INDEX_NAME.getLiteral());
                        if ("PRIMARY".equals(indexName)) {
                            continue;
                        }

                        String columnName = resultSet.getString(IndexInfoEnum.COLUMN_NAME.getLiteral());
                        FieldRecord fieldRecord = context.select(fieldTable.fields()).from(fieldTable)
                                .where(fieldTable.TABLE_ID.eq(tableRecord.getTableId()))
                                .and(fieldTable.NAME.eq(columnName))
                                .fetchOneInto(fieldTable);
                        String key = tableRecord.getTableId() + indexName + fieldRecord.getFieldId();
                        if (!indexRecords.containsKey(key)) {
                            IndexRecord indexRecord = context.newRecord(indexTable);
                            indexRecord.setTableId(tableRecord.getTableId());
                            indexRecord.setFieldId(fieldRecord.getFieldId());
                            indexRecord.setName(indexName);
                            indexRecord.setType(resultSet.getInt(IndexInfoEnum.TYPE.getLiteral()));
                            indexRecord.setUnique(!resultSet.getBoolean(IndexInfoEnum.NON_UNIQUE.getLiteral()));
                            indexRecord.store();
                        } else {
                            indexRecords.remove(key);
                        }
                    }
                }
                for (Map.Entry<String, IndexRecord> entry : indexRecords.entrySet()) {
                    context.delete(indexTable)
                            .where(indexTable.INDEX_ID.eq(entry.getValue().getIndexId()))
                            .execute();
                }
            }
        } catch (SQLException e) {
            throw new WicketRuntimeException(e);
        }
        context.update(tableTable).set(tableTable.LOCKED, false).execute();
    }

    public static ApplicationContext get(ServletContext servletContext) {
        return (ApplicationContext) servletContext.getAttribute(ApplicationContext.KEY);
    }

    protected Flyway initFlyway(DataSource dataSource) {
        if (dataSource == null) {
            return null;
        }
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
        return flyway;
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

    public final Flyway getFlyway() {
        return flyway;
    }

    public final DSLContext getDSLContext() {
        return context;
    }

    public final StringEncryptor getStringEncryptor() {
        return stringEncryptor;
    }
}
