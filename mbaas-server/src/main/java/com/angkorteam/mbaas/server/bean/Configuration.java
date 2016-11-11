package com.angkorteam.mbaas.server.bean;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.w3c.dom.Element;

import java.io.File;
import java.net.URL;

/**
 * Created by socheat on 11/11/16.
 */
public class Configuration extends XMLPropertiesConfiguration {

    public static final String KEY = "mbaas.properties.xml";

    public static final String MAINTENANCE = "maintenance";

    public static final String WICKET = "wicket";

    public static final String TEMP_JDBC_DRIVER = "temp.jdbc.driver";
    public static final String TEMP_JDBC_HOSTNAME = "temp.jdbc.hostname";
    public static final String TEMP_JDBC_EXTRA = "temp.jdbc.extra";
    public static final String TEMP_JDBC_PORT = "temp.jdbc.port";
    public static final String TEMP_JDBC_USERNAME = "temp.jdbc.username";
    public static final String TEMP_JDBC_PASSWORD = "temp.jdbc.password";
    public static final String TEMP_JDBC_DATABASE = "temp.jdbc.database";

    public static final String APP_JDBC_DRIVER = "app.jdbc.driver";
    public static final String APP_JDBC_HOSTNAME = "app.jdbc.hostname";
    public static final String APP_JDBC_EXTRA = "app.jdbc.extra";
    public static final String APP_JDBC_PORT = "app.jdbc.port";
    public static final String APP_JDBC_USERNAME = "app.jdbc.username";
    public static final String APP_JDBC_PASSWORD = "app.jdbc.password";
    public static final String APP_JDBC_DATABASE = "app.jdbc.database";

    public static final String TEST_JDBC_DRIVER = "test.jdbc.driver";
    public static final String TEST_JDBC_HOSTNAME = "test.jdbc.hostname";
    public static final String TEST_JDBC_EXTRA = "test.jdbc.extra";
    public static final String TEST_JDBC_PORT = "test.jdbc.port";
    public static final String TEST_JDBC_USERNAME = "test.jdbc.username";
    public static final String TEST_JDBC_PASSWORD = "test.jdbc.password";
    public static final String TEST_JDBC_DATABASE = "test.jdbc.database";

    public static final String JDBC_COLUMN_OPTIMISTIC = "jdbc.column.optimistic";
    public static final String JDBC_COLUMN_DELETED = "jdbc.column.deleted";
    public static final String JDBC_COLUMN_OWNER_USER_ID = "jdbc.column.owner_user_id";
    public static final String JDBC_COLUMN_DATE_CREATED = "jdbc.column.date_created";

    public static final String ROLE_ADMINISTRATOR = "role.administrator";
    public static final String ROLE_ADMINISTRATOR_DESCRIPTION = "role.administrator.description";
    public static final String ROLE_SYSTEM = "role.system";
    public static final String ROLE_SYSTEM_DESCRIPTION = "role.system.description";
    public static final String ROLE_SERVICE = "role.service";
    public static final String ROLE_SERVICE_DESCRIPTION = "role.service.description";

    public static final String USER_ADMIN = "user.admin";
    public static final String USER_ADMIN_ROLE = "user.admin.role";
    public static final String USER_ADMIN_PASSWORD = "user.admin.password";

    public static final String USER_SYSTEM = "user.system";
    public static final String USER_SYSTEM_ROLE = "user.system.role";
    public static final String USER_SYSTEM_PASSWORD = "user.system.password";

    public static final String USER_SERVICE = "user.service";
    public static final String USER_SERVICE_ROLE = "user.service.role";
    public static final String USER_SERVICE_PASSWORD = "user.service.password";

    public static final String PATTERN_DATETIME = "pattern.datetime";
    public static final String PATTERN_TIME = "pattern.time";
    public static final String PATTERN_DATE = "pattern.date";
    public static final String PATTERN_FOLDER = "pattern.folder";
    public static final String PATTERN_ATTRIBUTE_NAME = "pattern.attribute.name";
    public static final String PATTERN_OAUTH_ROLE_NAME = "pattern.oauth.role.name";
    public static final String PATTERN_PATH = "pattern.path";
    public static final String PATTERN_QUERY_PARAMETER_NAME = "pattern.query.parameter.name";

    public static final String ENCRYPTION_PASSWORD = "encryption.password";
    public static final String ENCRYPTION_OUTPUT = "encryption.output";

    public static final String RESOURCE_REPO = "resource.repo";

    public static final String AUTHORIZATION_TIME_TO_LIVE = "authorization.time_to_live";
    public static final String ACCESS_TOKEN_TIME_TO_LIVE = "access_token.time_to_live";

    public static final String MAIL_SERVER = "mail.server";
    public static final String MAIL_PORT = "mail.port";
    public static final String MAIL_LOGIN = "mail.login";
    public static final String MAIL_PASSWORD = "mail.password";
    public static final String MAIL_PROTOCOL = "mail.protocol";
    public static final String MAIL_FROM = "mail.from";

    public static final String EXECUTOR_POOL_SIZE = "executor.pool_size";
    public static final String EXECUTOR_QUEUE_CAPACITY = "executor.queue_capacity";

    public static final String PUSH_SERVER_URL = "push.server.url";

    public Configuration() {
    }

    public Configuration(String fileName) throws ConfigurationException {
        super(fileName);
    }

    public Configuration(File file) throws ConfigurationException {
        super(file);
    }

    public Configuration(URL url) throws ConfigurationException {
        super(url);
    }

    public Configuration(Element element) throws ConfigurationException {
        super(element);
    }

}
