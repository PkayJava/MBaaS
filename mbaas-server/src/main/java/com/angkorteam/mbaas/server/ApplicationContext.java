package com.angkorteam.mbaas.server;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.Collection;
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
import java.util.*;
import java.util.Date;

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
        LOGGER.info("initializing database connection");
        this.dataSource = initDataSource();
        LOGGER.info("initializing database structure");
        this.flyway = initFlyway(dataSource);
        LOGGER.info("initializing data access object layer");
        this.configuration = initConfiguration(dataSource);
        this.context = initDSLContext(configuration);
        LOGGER.info("initializing string encryptor");
        this.stringEncryptor = initStringEncryptor();
        LOGGER.info("initializing default role");
        initRole(context);
        LOGGER.info("initializing default user");
        initUser(context);
        LOGGER.info("initializing system collections, attributes, indexes");
        initDDL(context, dataSource);
        LOGGER.info("initialized mbaas-server core module");
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
            adminRecord.setUserId(UUID.randomUUID().toString());
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
            mbaasRecord.setUserId(UUID.randomUUID().toString());
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
            internalAdminRecord.setUserId(UUID.randomUUID().toString());
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
            administratorRecord.setRoleId(UUID.randomUUID().toString());
            administratorRecord.setName(configuration.getString(Constants.ROLE_ADMINISTRATOR));
            administratorRecord.setDescription(configuration.getString(Constants.ROLE_ADMINISTRATOR_DESCRIPTION));
            administratorRecord.setDeleted(false);
            administratorRecord.store();
        }

        RoleRecord backofficeRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_BACKOFFICE))).fetchOneInto(roleTable);
        if (backofficeRecord == null) {
            backofficeRecord = context.newRecord(roleTable);
            backofficeRecord.setRoleId(UUID.randomUUID().toString());
            backofficeRecord.setName(configuration.getString(Constants.ROLE_BACKOFFICE));
            backofficeRecord.setDescription(configuration.getString(Constants.ROLE_BACKOFFICE_DESCRIPTION));
            backofficeRecord.setDeleted(false);
            backofficeRecord.store();
        }

        RoleRecord registeredRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_REGISTERED))).fetchOneInto(roleTable);
        if (registeredRecord == null) {
            registeredRecord = context.newRecord(roleTable);
            registeredRecord.setRoleId(UUID.randomUUID().toString());
            registeredRecord.setName(configuration.getString(Constants.ROLE_REGISTERED));
            registeredRecord.setDescription(configuration.getString(Constants.ROLE_REGISTERED_DESCRIPTION));
            registeredRecord.setDeleted(false);
            registeredRecord.store();
        }

        RoleRecord anonymousRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_ANONYMOUS))).fetchOneInto(roleTable);
        if (anonymousRecord == null) {
            anonymousRecord = context.newRecord(roleTable);
            anonymousRecord.setRoleId(UUID.randomUUID().toString());
            anonymousRecord.setName(configuration.getString(Constants.ROLE_ANONYMOUS));
            anonymousRecord.setDescription(configuration.getString(Constants.ROLE_ANONYMOUS_DESCRIPTION));
            anonymousRecord.setDeleted(false);
            anonymousRecord.store();
        }
    }

    protected void initDDL(DSLContext context, DataSource dataSource) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        Collection collectionTable = Tables.COLLECTION.as("collectionTable");
        Attribute attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        Primary primaryTable = Tables.PRIMARY.as("primaryTable");
        Index indexTable = Tables.INDEX.as("indexTable");
        User userTable = Tables.USER.as("userTable");

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(configuration.getString(Constants.USER_ADMIN))).fetchOneInto(userTable);

        Map<String, CollectionRecord> collectionRecords = new LinkedHashMap<>();
        for (CollectionRecord collectionRecord : context.select(collectionTable.fields()).from(collectionTable).fetchInto(collectionTable)) {
            collectionRecords.put(collectionRecord.getName(), collectionRecord);
        }

        try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            DbSupport databaseSupport = DbSupportFactory.createDbSupport(connection, true);
            for (Table table : databaseSupport.getCurrentSchema().allTables()) {
                CollectionRecord collectionRecord = collectionRecords.get(table.getName());
                if (collectionRecord == null) {
                    collectionRecord = context.newRecord(collectionTable);
                    collectionRecord.setCollectionId(UUID.randomUUID().toString());
                    collectionRecord.setName(table.getName());
                    collectionRecord.setSystem(true);
                    collectionRecord.setOwnerUserId(userRecord.getUserId());
                    collectionRecord.store();
                }
                List<AttributeRecord> temporaryAttributeRecords = context.select(attributeTable.fields()).from(attributeTable)
                        .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                        .and(attributeTable.VIRTUAL.eq(false))
                        .fetchInto(attributeTable);
                Map<String, AttributeRecord> attributeRecords = new LinkedHashMap<>();
                for (AttributeRecord attributeRecord : temporaryAttributeRecords) {
                    attributeRecords.put(attributeRecord.getName(), attributeRecord);
                }
                {
                    ResultSet resultSet = databaseMetaData.getColumns(null, null, table.getName(), null);
                    while (resultSet.next()) {
                        String columnName = resultSet.getString(ColumnEnum.COLUMN_NAME.getLiteral());
                        if (!attributeRecords.containsKey(columnName)) {
                            AttributeRecord attributeRecord = context.newRecord(attributeTable);
                            attributeRecord.setAttributeId(UUID.randomUUID().toString());
                            attributeRecord.setCollectionId(collectionRecord.getCollectionId());
                            attributeRecord.setName(columnName);
                            attributeRecord.setNullable(resultSet.getBoolean(ColumnEnum.NULLABLE.getLiteral()));
                            if (columnName.equals(collectionRecord.getName() + "_id")) {
                                attributeRecord.setAutoIncrement(true);
                            } else {
                                attributeRecord.setAutoIncrement(false);
                            }
                            attributeRecord.setSystem(true);
                            attributeRecord.setVirtual(false);

                            int dataType = resultSet.getInt(ColumnEnum.DATA_TYPE.getLiteral());
                            String typeName = resultSet.getString(ColumnEnum.TYPE_NAME.getLiteral());

                            if (dataType == Types.LONGVARBINARY) {
                                attributeRecord.setExposed(false);
                            } else {
                                attributeRecord.setExposed(true);
                            }

                            if (dataType == Types.INTEGER) {
                                attributeRecord.setJavaType(Integer.class.getName());
                                attributeRecord.setSqlType(typeName);
                            } else if (dataType == Types.VARCHAR) {
                                attributeRecord.setJavaType(String.class.getName());
                                attributeRecord.setSqlType(typeName);
                            } else if (dataType == Types.TIMESTAMP) {
                                attributeRecord.setJavaType(Date.class.getName());
                                attributeRecord.setSqlType(typeName);
                            } else if (dataType == Types.LONGVARBINARY) {
                                attributeRecord.setJavaType(Byte.class.getName() + "[]");
                                attributeRecord.setSqlType(typeName);
                            } else if (dataType == Types.BIT) {
                                attributeRecord.setJavaType(Boolean.class.getName());
                                attributeRecord.setSqlType(typeName);
                            } else {
                                throw new WicketRuntimeException("field type unknown " + dataType + " => " + typeName);
                            }
                            attributeRecord.store();
                        } else {
                            attributeRecords.remove(columnName);
                        }
                    }
                }
                for (Map.Entry<String, AttributeRecord> entry : attributeRecords.entrySet()) {
                    context.delete(attributeTable)
                            .where(attributeTable.ATTRIBUTE_ID.eq(entry.getValue().getAttributeId()))
                            .execute();
                }
                {
                    Map<String, AttributeRecord> blobRecords = new LinkedHashMap<>();
                    for (AttributeRecord blobRecord : context.select(attributeTable.fields()).from(attributeTable)
                            .where(attributeTable.SQL_TYPE.eq("BLOB"))
                            .and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                            .and(attributeTable.VIRTUAL.eq(false))
                            .fetchInto(attributeTable)) {
                        blobRecords.put(blobRecord.getAttributeId(), blobRecord);
                    }
                    if (blobRecords == null || blobRecords.isEmpty()) {
                        context.delete(attributeTable)
                                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                                .and(attributeTable.VIRTUAL.eq(true))
                                .execute();
                    } else {
                        List<AttributeRecord> virtualRecords = context.select(attributeTable.fields()).from(attributeTable)
                                .where(attributeTable.VIRTUAL.eq(true))
                                .and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                                .fetchInto(attributeTable);
                        for (AttributeRecord virtualRecord : virtualRecords) {
                            if (!blobRecords.containsKey(virtualRecord.getVirtualAttributeId())) {
                                context.delete(attributeTable)
                                        .where(attributeTable.ATTRIBUTE_ID.eq(virtualRecord.getAttributeId()))
                                        .execute();
                            }
                        }
                    }
                }
                {
                    ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, table.getName());
                    while (resultSet.next()) {
                        String columnName = resultSet.getString(PrimaryKeyEnum.COLUMN_NAME.getLiteral());
                        AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable)
                                .where(attributeTable.NAME.eq(columnName))
                                .and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                                .and(attributeTable.VIRTUAL.eq(false))
                                .fetchOneInto(attributeTable);
                        PrimaryRecord primaryRecord = context.select(primaryTable.fields()).from(primaryTable)
                                .where(primaryTable.ATTRIBUTE_ID.eq(attributeRecord.getAttributeId()))
                                .and(primaryTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                                .fetchOneInto(primaryTable);
                        if (primaryRecord == null) {
                            primaryRecord = context.newRecord(primaryTable);
                            primaryRecord.setAttributeId(attributeRecord.getAttributeId());
                            primaryRecord.setCollectionId(collectionRecord.getCollectionId());
                            primaryRecord.store();
                        }
                    }
                }
                List<IndexRecord> temporaryIndexRecords = context.select(indexTable.fields()).from(indexTable)
                        .where(indexTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                        .fetchInto(indexTable);
                Map<String, IndexRecord> indexRecords = new LinkedHashMap<>();
                for (IndexRecord indexRecord : temporaryIndexRecords) {
                    String key = collectionRecord.getCollectionId() + indexRecord.getName() + indexRecord.getAttributeId();
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
                        AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable)
                                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                                .and(attributeTable.NAME.eq(columnName))
                                .fetchOneInto(attributeTable);
                        String key = collectionRecord.getCollectionId() + indexName + attributeRecord.getAttributeId();
                        if (!indexRecords.containsKey(key)) {
                            IndexRecord indexRecord = context.newRecord(indexTable);
                            indexRecord.setIndexId(UUID.randomUUID().toString());
                            indexRecord.setCollectionId(collectionRecord.getCollectionId());
                            indexRecord.setAttributeId(attributeRecord.getAttributeId());
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
        context.update(collectionTable).set(collectionTable.LOCKED, false).execute();
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
