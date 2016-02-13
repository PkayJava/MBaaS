package com.angkorteam.mbaas;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLPropertiesConfiguration;

import java.io.File;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class Constants {

    public static final String KEY = "mbaas.properties.xml";

    public static final String APP_JDBC_DRIVER = "app.jdbc.driver";
    public static final String APP_JDBC_URL = "app.jdbc.url";
    public static final String APP_JDBC_USERNAME = "app.jdbc.username";
    public static final String APP_JDBC_PASSWORD = "app.jdbc.password";
    public static final String APP_JDBC_DATABASE = "app.jdbc.database";

    public static final String TEST_JDBC_DRIVER = "test.jdbc.driver";
    public static final String TEST_JDBC_URL = "test.jdbc.url";
    public static final String TEST_JDBC_USERNAME = "test.jdbc.username";
    public static final String TEST_JDBC_PASSWORD = "test.jdbc.password";
    public static final String TEST_JDBC_DATABASE = "test.jdbc.database";

    public static final String JDBC_COLUMN_EXTRA = "jdbc.column.extra";
    public static final String JDBC_COLUMN_OPTIMISTIC = "jdbc.column.optimistic";
    public static final String JDBC_COLUMN_DELETED = "jdbc.column.deleted";

    public static final String ENCRYPTION_PASSWORD = "encryption.password";
    public static final String ENCRYPTION_OUTPUT = "encryption.output";

    public static final String APP_VERSION = "app.version";

    private static XMLPropertiesConfiguration configuration;
    private static long lastModified = -1;

    public static XMLPropertiesConfiguration getXmlPropertiesConfiguration() {
        File home = new File(System.getProperty("user.home"));
        try {
            File file = new File(home, ".xml/" + Constants.KEY);
            if (configuration == null) {
                configuration = new XMLPropertiesConfiguration(file);
            } else {
                if (lastModified != file.lastModified()) {
                    configuration = new XMLPropertiesConfiguration(file);
                }
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return configuration;
    }
}
