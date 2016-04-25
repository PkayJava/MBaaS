package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.*;
import com.angkorteam.mbaas.server.factory.JavascriptServiceFactoryBean;
import com.angkorteam.mbaas.server.service.PusherClient;
import okhttp3.OkHttpClient;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.FileUtils;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;

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

    private JdbcTemplate jdbcTemplate;

    private MailSender mailSender;

    private PusherClient pusherClient;

    private OkHttpClient httpClient;

    private ThreadPoolTaskExecutor executor;

    private ThreadPoolTaskScheduler scheduler;

    private JavascriptServiceFactoryBean.JavascriptService javascriptService;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        LOGGER.info("initializing mail sender");
        this.mailSender = initMailSender();
        LOGGER.info("initializing database connection");
        this.dataSource = initDataSource();
        LOGGER.info("initializing database structure");
        this.flyway = initFlyway(dataSource);
        LOGGER.info("initializing data access object layer");
        this.configuration = initConfiguration(dataSource);
        this.jdbcTemplate = initJdbcTemplate(dataSource);
        this.context = initDSLContext(configuration);
        this.httpClient = initHttpClient();
        this.pusherClient = initPusherClient(this.httpClient);
        LOGGER.info("initializing string encryptors");
        this.stringEncryptor = initStringEncryptor();
        LOGGER.info("initializing default role");
        initRole(context);
        LOGGER.info("initializing default user");
        initUser(context, jdbcTemplate);
        LOGGER.info("initializing system collections, attributes, indexes");
        initDDL(context, dataSource);
        LOGGER.info("initializing nashorn security");
        initNashorn(context);
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String resourceRepo = configuration.getString(Constants.RESOURCE_REPO);
        try {
            FileUtils.forceMkdir(new File(resourceRepo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("thread internal pool");
        this.executor = initExecutor();
        this.scheduler = initScheduler();
        this.javascriptService = initJavascriptService(context, jdbcTemplate, scheduler);

        LOGGER.info("initialized mbaas-server core module");
        servletContext.setAttribute(KEY, this);
    }

    protected JavascriptServiceFactoryBean.JavascriptService initJavascriptService(DSLContext context, JdbcTemplate jdbcTemplate, TaskScheduler scheduler) {
        JavascriptServiceFactoryBean.JavascriptService javascriptService = new JavascriptServiceFactoryBean.JavascriptService(context, jdbcTemplate, scheduler);
        JobTable jobTable = Tables.JOB.as("jobTable");
        List<JobRecord> jobRecords = context.select(jobTable.fields()).from(jobTable).fetchInto(jobTable);
        for (JobRecord jobRecord : jobRecords) {
            javascriptService.schedule(jobRecord.getJobId());
        }
        return javascriptService;
    }

    protected ThreadPoolTaskExecutor initExecutor() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(configuration.getInt(Constants.EXECUTOR_POOL_SIZE));
        executor.setMaxPoolSize(configuration.getInt(Constants.EXECUTOR_POOL_SIZE));
        executor.setQueueCapacity(configuration.getInt(Constants.EXECUTOR_QUEUE_CAPACITY));
        executor.setDaemon(true);
        executor.setBeanName("Executor");
        executor.afterPropertiesSet();
        return executor;
    }

    protected ThreadPoolTaskScheduler initScheduler() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setBeanName("Scheduler");
        scheduler.setDaemon(true);
        scheduler.setPoolSize(configuration.getInt(Constants.EXECUTOR_POOL_SIZE));
        scheduler.afterPropertiesSet();
        return scheduler;
    }

    protected OkHttpClient initHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(5, TimeUnit.SECONDS);
        builder.followRedirects(false);
        builder.followSslRedirects(false);
        builder.retryOnConnectionFailure(true);
        return builder.build();
    }

    protected PusherClient initPusherClient(OkHttpClient httpClient) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String pushAddress = configuration.getString(Constants.PUSH_SERVER_URL);
        String httpAddress = pushAddress.endsWith("/") ? pushAddress : pushAddress + "/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(httpAddress)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PusherClient pusherClient = retrofit.create(PusherClient.class);
        return pusherClient;
    }

    protected void initNashorn(DSLContext context) {
        List<String> granted = new ArrayList<>();

        granted.add(Boolean.class.getName());
        granted.add(Byte.class.getName());
        granted.add(Short.class.getName());
        granted.add(Integer.class.getName());
        granted.add(Long.class.getName());
        granted.add(Float.class.getName());
        granted.add(Double.class.getName());
        granted.add(Character.class.getName());
        granted.add(String.class.getName());
        granted.add(Date.class.getName());
        granted.add(BigDecimal.class.getName());
        granted.add(BigInteger.class.getName());
        granted.add(Arrays.class.getName());
        granted.add(Collections.class.getName());
        granted.add(LinkedHashMap.class.getName());
        granted.add(LinkedHashSet.class.getName());
        granted.add(Hashtable.class.getName());
        granted.add(Vector.class.getName());
        granted.add(LinkedList.class.getName());
        granted.add(ArrayList.class.getName());
        granted.add(HashMap.class.getName());
        granted.add(ArrayBlockingQueue.class.getName());
        granted.add(SynchronousQueue.class.getName());
        granted.add(LinkedBlockingDeque.class.getName());
        granted.add(DelayQueue.class.getName());
        granted.add(LinkedTransferQueue.class.getName());
        granted.add(ArrayDeque.class.getName());
        granted.add(ConcurrentLinkedDeque.class.getName());
        granted.add(Stack.class.getName());
        granted.add(Tables.class.getName());
        granted.add(DSL.class.getName());

        NashornTable nashornTable = Tables.NASHORN.as("nashornTable");

        for (String name : granted) {
            int count = context.selectCount().from(nashornTable).where(nashornTable.NASHORN_ID.eq(name)).fetchOneInto(int.class);
            if (count == 0) {
                NashornRecord nashornRecord = context.newRecord(nashornTable);
                nashornRecord.setNashornId(name);
                nashornRecord.setDateCreated(new Date());
                nashornRecord.setSecurity(SecurityEnum.Granted.getLiteral());
                nashornRecord.store();
            }
        }
    }

    protected void initUser(DSLContext context, JdbcTemplate jdbcTemplate) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        UserTable userTable = Tables.USER.as("userTable");
        RoleTable roleTable = Tables.ROLE.as("roleTable");

        UserRecord adminRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(configuration.getString(Constants.USER_ADMIN))).fetchOneInto(userTable);
        if (adminRecord == null) {
            String uuid = UUID.randomUUID().toString();
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(configuration.getString(Constants.USER_ADMIN_ROLE)))).fetchOneInto(roleTable);
            adminRecord = context.newRecord(userTable);
            adminRecord.setUserId(uuid);
            adminRecord.setAccountNonExpired(true);
            adminRecord.setSystem(true);
            adminRecord.setAccountNonLocked(true);
            adminRecord.setCredentialsNonExpired(true);
            adminRecord.setStatus(UserStatusEnum.Active.getLiteral());
            adminRecord.setLogin(configuration.getString(Constants.USER_ADMIN));
            adminRecord.setPassword(configuration.getString(Constants.USER_ADMIN_PASSWORD));
            adminRecord.setRoleId(roleRecord.getRoleId());
            adminRecord.setAuthentication(AuthenticationEnum.None.getLiteral());
            adminRecord.store();
            context.update(userTable).set(userTable.PASSWORD, DSL.md5(configuration.getString(Constants.USER_ADMIN_PASSWORD))).where(userTable.USER_ID.eq(uuid)).execute();
        }

        UserRecord mbaasRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(configuration.getString(Constants.USER_MBAAS))).fetchOneInto(userTable);
        if (mbaasRecord == null) {
            String uuid = UUID.randomUUID().toString();
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(configuration.getString(Constants.USER_MBAAS_ROLE)))).fetchOneInto(roleTable);
            mbaasRecord = context.newRecord(userTable);
            mbaasRecord.setUserId(uuid);
            mbaasRecord.setSystem(true);
            mbaasRecord.setAccountNonExpired(true);
            mbaasRecord.setAccountNonLocked(true);
            mbaasRecord.setCredentialsNonExpired(true);
            mbaasRecord.setStatus(UserStatusEnum.Active.getLiteral());
            mbaasRecord.setLogin(configuration.getString(Constants.USER_MBAAS));
            mbaasRecord.setPassword(configuration.getString(Constants.USER_MBAAS_PASSWORD));
            mbaasRecord.setRoleId(roleRecord.getRoleId());
            mbaasRecord.setAuthentication(AuthenticationEnum.None.getLiteral());
            mbaasRecord.store();
            context.update(userTable).set(userTable.PASSWORD, DSL.md5(configuration.getString(Constants.USER_MBAAS_PASSWORD))).where(userTable.USER_ID.eq(uuid)).execute();
        }

        UserRecord internalAdminRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(configuration.getString(Constants.USER_INTERNAL_ADMIN))).fetchOneInto(userTable);
        if (internalAdminRecord == null) {
            String uuid = UUID.randomUUID().toString();
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(configuration.getString(Constants.USER_INTERNAL_ADMIN_ROLE)))).fetchOneInto(roleTable);
            internalAdminRecord = context.newRecord(userTable);
            internalAdminRecord.setUserId(uuid);
            internalAdminRecord.setSystem(true);
            internalAdminRecord.setAccountNonExpired(true);
            internalAdminRecord.setAccountNonLocked(true);
            internalAdminRecord.setCredentialsNonExpired(true);
            internalAdminRecord.setStatus(UserStatusEnum.Active.getLiteral());
            internalAdminRecord.setLogin(configuration.getString(Constants.USER_INTERNAL_ADMIN));
            internalAdminRecord.setPassword(configuration.getString(Constants.USER_INTERNAL_ADMIN_PASSWORD));
            internalAdminRecord.setRoleId(roleRecord.getRoleId());
            internalAdminRecord.setAuthentication(AuthenticationEnum.None.getLiteral());
            internalAdminRecord.store();
            context.update(userTable).set(userTable.PASSWORD, DSL.md5(configuration.getString(Constants.USER_INTERNAL_ADMIN_PASSWORD))).where(userTable.USER_ID.eq(uuid)).execute();
        }
    }

    protected void initRole(DSLContext context) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        Map<String, String> roles = new HashMap<>();
        roles.put(configuration.getString(Constants.ROLE_ADMINISTRATOR), configuration.getString(Constants.ROLE_ADMINISTRATOR_DESCRIPTION));
        roles.put(configuration.getString(Constants.ROLE_BACKOFFICE), configuration.getString(Constants.ROLE_BACKOFFICE_DESCRIPTION));
        roles.put(configuration.getString(Constants.ROLE_REGISTERED), configuration.getString(Constants.ROLE_REGISTERED_DESCRIPTION));
        roles.put(configuration.getString(Constants.ROLE_ANONYMOUS), configuration.getString(Constants.ROLE_ANONYMOUS_DESCRIPTION));
        roles.put(configuration.getString(Constants.ROLE_OAUTH2_AUTHORIZATION), configuration.getString(Constants.ROLE_OAUTH2_AUTHORIZATION_DESCRIPTION));
        roles.put(configuration.getString(Constants.ROLE_OAUTH2_CLIENT), configuration.getString(Constants.ROLE_OAUTH2_CLIENT_DESCRIPTION));
        roles.put(configuration.getString(Constants.ROLE_OAUTH2_IMPLICIT), configuration.getString(Constants.ROLE_OAUTH2_IMPLICIT_DESCRIPTION));
        roles.put(configuration.getString(Constants.ROLE_OAUTH2_PASSWORD), configuration.getString(Constants.ROLE_OAUTH2_PASSWORD_DESCRIPTION));

        RoleTable roleTable = Tables.ROLE.as("roleTable");
        for (Map.Entry<String, String> role : roles.entrySet()) {
            RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(role.getKey())).fetchOneInto(roleTable);
            if (roleRecord == null) {
                roleRecord = context.newRecord(roleTable);
                roleRecord.setRoleId(UUID.randomUUID().toString());
                roleRecord.setName(role.getKey());
                roleRecord.setDescription(role.getValue());
                roleRecord.setSystem(true);
                roleRecord.store();
            }
        }
    }

    protected void initDDL(DSLContext context, DataSource dataSource) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        UserTable userTable = Tables.USER.as("userTable");

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(configuration.getString(Constants.USER_ADMIN))).fetchOneInto(userTable);

        Map<String, CollectionRecord> collectionRecords = new LinkedHashMap<>();
        for (CollectionRecord collectionRecord : context.select(collectionTable.fields()).from(collectionTable).fetchInto(collectionTable)) {
            collectionRecords.put(collectionRecord.getName(), collectionRecord);
        }

        try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            DbSupport databaseSupport = DbSupportFactory.createDbSupport(connection, true);
            for (Table table : databaseSupport.getSchema(databaseSupport.getCurrentSchemaName()).allTables()) {
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
                        .fetchInto(attributeTable);
                Map<String, AttributeRecord> attributeRecords = new LinkedHashMap<>();
                for (AttributeRecord attributeRecord : temporaryAttributeRecords) {
                    int extra = attributeRecord.getExtra();
                    if (AttributeExtraEnum.PRIMARY == (extra & AttributeExtraEnum.PRIMARY)) {
                        extra = extra - AttributeExtraEnum.PRIMARY;
                    }
                    if (AttributeExtraEnum.INDEX == (extra & AttributeExtraEnum.INDEX)) {
                        extra = extra - AttributeExtraEnum.INDEX;
                    }
                    attributeRecord.setExtra(extra);
                    attributeRecord.update();
                    attributeRecords.put(attributeRecord.getName(), attributeRecord);
                }
                List<String> physicalName = new ArrayList<>();
                ResultSet columns = databaseMetaData.getColumns(null, null, table.getName(), null);
                while (columns.next()) {
                    String columnName = columns.getString(ColumnEnum.COLUMN_NAME.getLiteral());
                    if (!attributeRecords.containsKey(columnName)) {
                        int extra = 0;
                        AttributeRecord attributeRecord = context.newRecord(attributeTable);
                        attributeRecord.setAttributeId(UUID.randomUUID().toString());
                        attributeRecord.setCollectionId(collectionRecord.getCollectionId());
                        attributeRecord.setName(columnName);
                        if (columns.getBoolean(ColumnEnum.NULLABLE.getLiteral())) {
                            extra = extra | AttributeExtraEnum.NULLABLE;
                        }
                        if (columnName.equals(collectionRecord.getName() + "_id")) {
                            extra = extra | AttributeExtraEnum.AUTO_INCREMENT;
                            attributeRecord.setVisibility(VisibilityEnum.Shown.getLiteral());
                        } else {
                            attributeRecord.setVisibility(VisibilityEnum.Hided.getLiteral());
                        }
                        extra = extra | AttributeExtraEnum.EXPOSED;
                        attributeRecord.setSystem(true);
                        attributeRecord.setEav(false);
                        attributeRecord.setExtra(extra);

                        int dataType = columns.getInt(ColumnEnum.DATA_TYPE.getLiteral());
                        int columnSize = columns.getInt(ColumnEnum.COLUMN_SIZE.getLiteral());
                        String typeName = columns.getString(ColumnEnum.TYPE_NAME.getLiteral());

                        String errorMessage = "collection " + table.getName() + ", attribute " + columnName + " dataType " + dataType + ",  typeName " + typeName + ", columnSize " + columnSize;

                        boolean handled = false;

                        if (dataType == Types.BIT) {
                            if ("BIT".equals(typeName)) {
                                if (columnSize == 1) {
                                    attributeRecord.setAttributeType(AttributeTypeEnum.Boolean.getLiteral());
                                    handled = true;
                                } else {
                                    attributeRecord.setAttributeType(AttributeTypeEnum.Integer.getLiteral());
                                    handled = true;
                                }
                            } else if ("TINYINT".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Integer.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.TINYINT) {
                            if ("TINYINT".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Integer.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.SMALLINT) {
                            if ("SMALLINT".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Integer.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.BOOLEAN) {
                            attributeRecord.setAttributeType(AttributeTypeEnum.Boolean.getLiteral());
                        } else if (dataType == Types.INTEGER) {
                            if ("INT".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Integer.getLiteral());
                                handled = true;
                            } else if ("MEDIUMINT".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Integer.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.BIGINT) {
                            if ("BIGINT".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Integer.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.REAL) {
                            if ("FLOAT".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Float.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.DOUBLE) {
                            if ("DOUBLE".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Double.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.DECIMAL) {
                            if ("DECIMAL".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Double.getLiteral());
                                handled = true;
                            }
                        } else if ((dataType == Types.CHAR && "CHAR".equals(typeName)) || (dataType == Types.VARCHAR && "VARCHAR".equals(typeName))) {
                            if (columnSize > 255) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Text.getLiteral());
                                handled = true;
                            } else if (columnSize > 1) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.String.getLiteral());
                                handled = true;
                            } else {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Character.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.TIMESTAMP) {
                            if ("DATETIME".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.DateTime.getLiteral());
                                handled = true;
                            } else if ("TIMESTAMP".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.DateTime.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.DATE) {
                            if ("DATE".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Date.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.TIME) {
                            if ("TIME".equals(typeName)) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Time.getLiteral());
                                handled = true;
                            }
                        } else if (dataType == Types.LONGVARCHAR) {
                            if (typeName.equals("TEXT")) {
                                attributeRecord.setAttributeType(AttributeTypeEnum.Text.getLiteral());
                                handled = true;
                            }
                        }
                        if (!handled) {
                            throw new WicketRuntimeException(errorMessage);
                        }
                        attributeRecord.store();
                    } else {
                        if (attributeRecords.get(columnName).getEav()) {
                            throw new WicketRuntimeException(table.getName() + " attribute " + columnName + " is already exists but it is EAV attribute");
                        }
                    }
                    physicalName.add(columnName);
                }
                for (Map.Entry<String, AttributeRecord> entry : attributeRecords.entrySet()) {
                    if (!physicalName.contains(entry.getKey()) && !entry.getValue().getEav()) {
                        context.delete(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(entry.getValue().getAttributeId())).execute();
                    }
                }
                {
                    ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, table.getName());
                    while (resultSet.next()) {
                        String columnName = resultSet.getString(PrimaryKeyEnum.COLUMN_NAME.getLiteral());
                        AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable)
                                .where(attributeTable.NAME.eq(columnName))
                                .and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                                .fetchOneInto(attributeTable);
                        int extra = attributeRecord.getExtra() | AttributeExtraEnum.PRIMARY;
                        attributeRecord.setExtra(extra);
                        attributeRecord.update();
                    }
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
                        int extra = attributeRecord.getExtra();
                        attributeRecord.setExtra(extra | AttributeExtraEnum.INDEX);
                        attributeRecord.update();
                    }
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

    protected MailSender initMailSender() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(configuration.getString(Constants.MAIL_SERVER));
        mailSender.setUsername(configuration.getString(Constants.MAIL_LOGIN));
        mailSender.setPassword(configuration.getString(Constants.MAIL_PASSWORD));
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setPort(configuration.getInt(Constants.MAIL_PORT));
        mailSender.setProtocol(configuration.getString(Constants.MAIL_PROTOCOL));
        return mailSender;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (this.executor != null) {
            this.executor.shutdown();
        }
        if (this.scheduler != null) {
            this.scheduler.shutdown();
        }
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

    protected JdbcTemplate initJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    protected Configuration initConfiguration(BasicDataSource dataSource) {
        if (dataSource == null) {
            return null;
        }
        XMLPropertiesConfiguration xml = Constants.getXmlPropertiesConfiguration();
        MappedSchema mappedSchema = new MappedSchema();
        mappedSchema.withInput(xml.getString(Constants.TEMP_JDBC_DATABASE));
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

    public final MailSender getMailSender() {
        return this.mailSender;
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

    public final JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public final PusherClient getPusherClient() {
        return pusherClient;
    }

    public final TaskExecutor getExecutor() {
        return executor;
    }

    public final TaskScheduler getScheduler() {
        return scheduler;
    }

    public final JavascriptServiceFactoryBean.JavascriptService getJavascriptService() {
        return this.javascriptService;
    }
}
