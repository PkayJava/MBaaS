package com.angkorteam.mbaas.server.spring;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.MbaasRoleTable;
import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
import com.angkorteam.mbaas.model.entity.tables.NashornTable;
import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
import com.angkorteam.mbaas.model.entity.tables.records.MbaasRoleRecord;
import com.angkorteam.mbaas.model.entity.tables.records.MbaasUserRecord;
import com.angkorteam.mbaas.model.entity.tables.records.NashornRecord;
import com.angkorteam.mbaas.plain.enums.AuthenticationEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.enums.UserStatusEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.factory.ApplicationDataSourceFactoryBean;
import com.angkorteam.mbaas.server.factory.JavascriptServiceFactoryBean;
import com.angkorteam.mbaas.server.nashorn.JavaFilter;
import com.angkorteam.mbaas.server.service.PusherClient;
import com.angkorteam.mbaas.server.socket.ServerInitializer;
import com.google.gson.Gson;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import okhttp3.OkHttpClient;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.validation.ValidationError;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
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
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.json.GsonFactoryBean;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.script.ScriptEngineFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Khauv Socheat on 2/3/2016.
 */
public class ApplicationContext implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    public static final String KEY = ApplicationContext.class.getName();

    private BasicDataSource dataSource;

    private DbSupport dbSupport;

    private ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource;

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

    private EventLoopGroup bossGroup;

    private EventLoopGroup workGroup;

    private Gson gson;

    private ClassFilter classFilter;

    private ScriptEngineFactory scriptEngineFactory;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        this.gson = initGson();
        LOGGER.info("initializing mail sender");
        this.mailSender = initMailSender();
        LOGGER.info("initializing mbaas data source");
        this.dataSource = initDataSource();
        LOGGER.info("initializing data access object layer");
        this.configuration = initConfiguration(this.dataSource);
        this.dbSupport = initDbSupport(this.dataSource);
        this.jdbcTemplate = initJdbcTemplate(this.dataSource);
        LOGGER.info("initializing application data source");
        this.applicationDataSource = initApplicationDataSource();
        LOGGER.info("initializing database structure");
        this.flyway = initFlyway(this.dataSource);
        this.context = initDSLContext(this.configuration);
        this.httpClient = initHttpClient();
        this.pusherClient = initPusherClient(this.httpClient);
        this.scriptEngineFactory = initScriptEngineFactory();
        this.classFilter = initClassFilter(this.context);
        LOGGER.info("initializing string encryptors");
        this.stringEncryptor = initStringEncryptor();
        LOGGER.info("initializing default role");
        initMBaaSRole(this.context);
        LOGGER.info("initializing default user");
        initMBaaSUser(this.context, this.jdbcTemplate);
        LOGGER.info("initializing nashorn security");
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String resourceRepo = configuration.getString(Constants.RESOURCE_REPO);
        try {
            FileUtils.forceMkdir(new File(resourceRepo));
        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }
        LOGGER.info("initializing thread internal pool");
        this.executor = initExecutor();
        this.scheduler = initScheduler();
        this.javascriptService = initJavascriptService(this.scriptEngineFactory, this.classFilter, this.context, this.applicationDataSource, this.scheduler);

        LOGGER.info("initializing communication service");
        this.bossGroup = initBossGroup();
        this.workGroup = initWorkGroup();
        initCommunicationService(this.bossGroup, this.workGroup, this.context, this.jdbcTemplate, this.gson);

        LOGGER.info("initialized mbaas-server core module");
        servletContext.setAttribute(KEY, this);
    }

    protected ScriptEngineFactory initScriptEngineFactory() {
        NashornScriptEngineFactory scriptEngineFactory = new NashornScriptEngineFactory();
        return scriptEngineFactory;
    }

    protected ClassFilter initClassFilter(DSLContext context) {
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
        granted.add(LocalTime.class.getName());
        granted.add(LocalDate.class.getName());
        granted.add(LocalDateTime.class.getName());
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
        granted.add(ValidationError.class.getName());
        granted.add(Tables.class.getName());
        granted.add(DSL.class.getName());
        granted.add(Option.class.getName());
        granted.add(Jdbc.class.getName());
        granted.add(UUID.class.getName());
        granted.add(StringUtils.class.getName());
        granted.add(SimpleJdbcInsert.class.getName());
        granted.add(SimpleJdbcUpdate.class.getName());

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
        JavaFilter javaFilter = new JavaFilter(context);
        return javaFilter;
    }

    protected Gson initGson() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        GsonFactoryBean factoryBean = new GsonFactoryBean();
        factoryBean.setBase64EncodeByteArrays(false);
        factoryBean.setDisableHtmlEscaping(true);
        factoryBean.setPrettyPrinting(false);
        factoryBean.setSerializeNulls(false);
        factoryBean.setDateFormatPattern(configuration.getString(Constants.PATTERN_DATETIME));
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    protected DbSupport initDbSupport(DataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            DbSupport databaseSupport = DbSupportFactory.createDbSupport(connection, true);
            return databaseSupport;
        } catch (SQLException e) {
            throw new WicketRuntimeException(e);
        }
    }

    protected ApplicationDataSourceFactoryBean.ApplicationDataSource initApplicationDataSource() {
        ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource = new ApplicationDataSourceFactoryBean.ApplicationDataSource();
        return applicationDataSource;
    }

    protected void initCommunicationService(EventLoopGroup bossGroup, EventLoopGroup workGroup, DSLContext context, JdbcTemplate jdbcTemplate, Gson gson) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup);
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ServerInitializer(context, jdbcTemplate, gson));
        try {
            serverBootstrap.bind(5222).sync();
        } catch (InterruptedException e) {
            LOGGER.info(e.getMessage());
            throw new WicketRuntimeException(e);
        }
    }

    protected EventLoopGroup initBossGroup() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        return bossGroup;
    }

    protected EventLoopGroup initWorkGroup() {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        return workGroup;
    }

    protected JavascriptServiceFactoryBean.JavascriptService initJavascriptService(ScriptEngineFactory scriptEngineFactory, ClassFilter classFilter, DSLContext context, ApplicationDataSourceFactoryBean.ApplicationDataSource applicationDataSource, TaskScheduler scheduler) {
        JavascriptServiceFactoryBean.JavascriptService javascriptService = new JavascriptServiceFactoryBean.JavascriptService(context, applicationDataSource, scheduler, scriptEngineFactory, classFilter);
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        List<ApplicationRecord> applicationRecords = context.select(applicationTable.fields()).from(applicationTable).fetchInto(applicationTable);
        for (ApplicationRecord applicationRecord : applicationRecords) {
            String jdbcUrl = "jdbc:mysql://" + applicationRecord.getMysqlHostname() + ":" + applicationRecord.getMysqlPort() + "/" + applicationRecord.getMysqlDatabase() + "?" + applicationRecord.getMysqlExtra();
            JdbcTemplate jdbcTemplate = applicationDataSource.getJdbcTemplate(applicationRecord.getCode(), jdbcUrl, applicationRecord.getMysqlUsername(), applicationRecord.getMysqlPassword());
            List<String> jobIds = null;
            try {
                jobIds = jdbcTemplate.queryForList("SELECT " + Jdbc.Job.JOB_ID + " FROM " + Jdbc.JOB, String.class);
            } catch (DataAccessException e) {
            }
            if (jobIds != null && !jobIds.isEmpty()) {
                for (String jobId : jobIds) {
                    javascriptService.schedule(applicationRecord.getCode(), jobId);
                }
            }
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
        String pushAddress = configuration.getString(Constants.PUSH_SERVER_URL, "");
        if (!"".equals(pushAddress)) {
            String httpAddress = pushAddress.endsWith("/") ? pushAddress : pushAddress + "/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(httpAddress)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PusherClient pusherClient = retrofit.create(PusherClient.class);
            return pusherClient;
        } else {
            return null;
        }
    }

    protected void initMBaaSUser(DSLContext context, JdbcTemplate jdbcTemplate) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        MbaasUserTable mbaasUserTable = Tables.MBAAS_USER.as("mbaasUserTable");
        MbaasRoleTable mbaasRoleTable = Tables.MBAAS_ROLE.as("mbaasRoleTable");

        MbaasUserRecord adminRecord = context.select(mbaasUserTable.fields()).from(mbaasUserTable).where(mbaasUserTable.LOGIN.eq(configuration.getString(Constants.USER_MBAAS_ADMIN))).fetchOneInto(mbaasUserTable);
        if (adminRecord == null) {
            String uuid = UUID.randomUUID().toString();
            MbaasRoleRecord mbaasRoleRecord = context.select(mbaasRoleTable.fields()).from(mbaasRoleTable).where(mbaasRoleTable.NAME.eq(configuration.getString(configuration.getString(Constants.USER_MBAAS_ADMIN_ROLE)))).fetchOneInto(mbaasRoleTable);
            adminRecord = context.newRecord(mbaasUserTable);
            adminRecord.setMbaasUserId(uuid);
            adminRecord.setAccountNonExpired(true);
            adminRecord.setSystem(true);
            adminRecord.setAccountNonLocked(true);
            adminRecord.setCredentialsNonExpired(true);
            adminRecord.setStatus(UserStatusEnum.Active.getLiteral());
            adminRecord.setLogin(configuration.getString(Constants.USER_MBAAS_ADMIN));
            adminRecord.setFullName(configuration.getString(Constants.USER_MBAAS_ADMIN));
            adminRecord.setPassword(configuration.getString(Constants.USER_MBAAS_ADMIN_PASSWORD));
            adminRecord.setMbaasRoleId(mbaasRoleRecord.getMbaasRoleId());
            adminRecord.setAuthentication(AuthenticationEnum.None.getLiteral());
            adminRecord.store();
            context.update(mbaasUserTable).set(mbaasUserTable.PASSWORD, DSL.md5(configuration.getString(Constants.USER_MBAAS_ADMIN_PASSWORD))).where(mbaasUserTable.MBAAS_USER_ID.eq(uuid)).execute();
        }

        MbaasUserRecord systemRecord = context.select(mbaasUserTable.fields()).from(mbaasUserTable).where(mbaasUserTable.LOGIN.eq(configuration.getString(Constants.USER_MBAAS_SYSTEM))).fetchOneInto(mbaasUserTable);
        if (systemRecord == null) {
            String uuid = UUID.randomUUID().toString();
            MbaasRoleRecord mbaasRoleRecord = context.select(mbaasRoleTable.fields()).from(mbaasRoleTable).where(mbaasRoleTable.NAME.eq(configuration.getString(configuration.getString(Constants.USER_MBAAS_SYSTEM_ROLE)))).fetchOneInto(mbaasRoleTable);
            systemRecord = context.newRecord(mbaasUserTable);
            systemRecord.setMbaasUserId(uuid);
            systemRecord.setSystem(true);
            systemRecord.setAccountNonExpired(true);
            systemRecord.setAccountNonLocked(true);
            systemRecord.setCredentialsNonExpired(true);
            systemRecord.setStatus(UserStatusEnum.Active.getLiteral());
            systemRecord.setLogin(configuration.getString(Constants.USER_MBAAS_SYSTEM));
            systemRecord.setFullName(configuration.getString(Constants.USER_MBAAS_SYSTEM));
            systemRecord.setPassword(configuration.getString(Constants.USER_MBAAS_SYSTEM_PASSWORD));
            systemRecord.setMbaasRoleId(mbaasRoleRecord.getMbaasRoleId());
            systemRecord.setAuthentication(AuthenticationEnum.None.getLiteral());
            systemRecord.store();
            context.update(mbaasUserTable).set(mbaasUserTable.PASSWORD, DSL.md5(configuration.getString(Constants.USER_MBAAS_SYSTEM_PASSWORD))).where(mbaasUserTable.MBAAS_USER_ID.eq(uuid)).execute();
        }
    }

    protected void initMBaaSRole(DSLContext context) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        Map<String, String> roles = new HashMap<>();
        roles.put(configuration.getString(Constants.ROLE_MBAAS_ADMINISTRATOR), configuration.getString(Constants.ROLE_MBAAS_ADMINISTRATOR_DESCRIPTION));
        roles.put(configuration.getString(Constants.ROLE_MBAAS_SYSTEM), configuration.getString(Constants.ROLE_MBAAS_SYSTEM_DESCRIPTION));
        MbaasRoleTable mbaasRoleTable = Tables.MBAAS_ROLE.as("mbaasRoleTable");
        for (Map.Entry<String, String> role : roles.entrySet()) {
            MbaasRoleRecord mbaasRoleRecord = context.select(mbaasRoleTable.fields()).from(mbaasRoleTable).where(mbaasRoleTable.NAME.eq(role.getKey())).fetchOneInto(mbaasRoleTable);
            if (mbaasRoleRecord == null) {
                mbaasRoleRecord = context.newRecord(mbaasRoleTable);
                mbaasRoleRecord.setMbaasRoleId(UUID.randomUUID().toString());
                mbaasRoleRecord.setName(role.getKey());
                mbaasRoleRecord.setDescription(role.getValue());
                mbaasRoleRecord.setSystem(true);
                mbaasRoleRecord.store();
            }
        }
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
        try {
            flyway.migrate();
        } catch (FlywayException e) {
            LOGGER.info(e.getMessage());
            throw e;
        }
        return flyway;
    }

    protected BasicDataSource initDataSource() {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        BasicDataSource dataSource = new BasicDataSource();
        String itest = System.getProperty("itest");
        if (itest == null || "".equals(itest)) {
            String jdbcDriver = configuration.getString(Constants.APP_JDBC_DRIVER);
            String jdbcUrl = "jdbc:mysql://" + configuration.getString(Constants.APP_JDBC_HOSTNAME) + ":" + configuration.getString(Constants.APP_JDBC_PORT) + "/" + configuration.getString(Constants.APP_JDBC_DATABASE) + "?" + configuration.getString(Constants.APP_JDBC_EXTRA);
            dataSource.setDriverClassName(jdbcDriver);
            dataSource.setUsername(configuration.getString(Constants.APP_JDBC_USERNAME));
            dataSource.setPassword(configuration.getString(Constants.APP_JDBC_PASSWORD));
            dataSource.setUrl(jdbcUrl);
        } else {
            String jdbcDriver = configuration.getString(Constants.TEST_JDBC_DRIVER);
            String jdbcUrl = "jdbc:mysql://" + configuration.getString(Constants.TEST_JDBC_HOSTNAME) + ":" + configuration.getString(Constants.TEST_JDBC_PORT) + "/" + configuration.getString(Constants.TEST_JDBC_DATABASE) + "?" + configuration.getString(Constants.TEST_JDBC_EXTRA);
            dataSource.setDriverClassName(jdbcDriver);
            dataSource.setUsername(configuration.getString(Constants.TEST_JDBC_USERNAME));
            dataSource.setPassword(configuration.getString(Constants.TEST_JDBC_PASSWORD));
            dataSource.setUrl(jdbcUrl);
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
        if (this.bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }
        if (this.workGroup != null) {
            this.workGroup.shutdownGracefully();
        }
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
                LOGGER.info(e.getMessage());
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

    public final ClassFilter getClassFilter() {
        return this.classFilter;
    }

    public final ScriptEngineFactory getScriptEngineFactory() {
        return this.scriptEngineFactory;
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

    public final DbSupport getDbSupport() {
        return this.dbSupport;
    }

    public final JavascriptServiceFactoryBean.JavascriptService getJavascriptService() {
        return this.javascriptService;
    }

    public final ApplicationDataSourceFactoryBean.ApplicationDataSource getApplicationDataSource() {
        return this.applicationDataSource;
    }

    public final Gson getGson() {
        return this.gson;
    }
}
