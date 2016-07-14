package com.angkorteam.mbaas.server.wicket.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Separate startup class for people that want to run the examples directly. Use parameter
 * -Dcom.sun.management.jmxremote to startup JMX (and e.g. connect with jconsole).
 */
public class ReadCommitted {

    private static final List<String> WORDS = new ArrayList<>();

    static {
        InputStream inputStream = null;
        try {
            inputStream = FileUtils.openInputStream(new File(Database.DICTIONARY));
            for (String word : IOUtils.readLines(inputStream, "UTF-8")) {
                WORDS.add(org.apache.commons.lang3.StringUtils.capitalize(word));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Main function, starts the jetty server.
     *
     * @param args
     */
    public static void main(String[] args) throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(Database.DRIVER);
        dataSource.setUsername(Database.USER);
        dataSource.setPassword(Database.PASSWORD);
        dataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        dataSource.setConnectionProperties(Database.PROPERTIES);
        dataSource.setUrl(Database.URL);
        clean(dataSource, "address", "assignment", "attendance", "calendar", "certification", "class", "course", "enrollment", "examination", "examination_master", "foundation_examination", "link", "person", "room", "subject");
        generatePerson(dataSource, 200);
    }

    public static void generatePerson(DataSource dataSource, int quantity) {
        List<Map<String, Object>> batch = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            Map<String, Object> fields = new HashMap<>();
            String name = randomName();
            fields.put("name", name);
            fields.put("picture", name);
            fields.put("noted", "Person");
            fields.put("type", 1);
            batch.add(fields);
        }
        insert(dataSource, "person", Arrays.asList("name", "picture", "noted", "type"), batch);
    }

    public static void insert(DataSource dataSource, String tableName, List<String> usingColumns, List<Map<String, Object>> fields) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource);
        jdbcInsert.withTableName(tableName);
        List<String> pp = new ArrayList<>();
        pp.addAll(usingColumns);
        pp.add(tableName + "_id");
        jdbcInsert.usingColumns(pp.toArray(new String[usingColumns.size()]));
        for (Map<String, Object> field : fields) {
            field.put(tableName + "_id", UUID.randomUUID().toString());
        }
        Map<String, Object>[] batch = fields.toArray(new Map[fields.size()]);
        jdbcInsert.executeBatch(batch);
    }

    public static String randomName() {
        List<String> results = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        while (buffer.length() <= 15) {
            String result = WORDS.get(RandomUtils.nextInt(0, WORDS.size()));
            if (!results.contains(result)) {
                results.add(result);
                buffer.append(result);
            }
        }
        return org.apache.commons.lang3.StringUtils.join(results, " ");
    }

    public static void clean(DataSource dataSource, String... tables) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        for (String table : tables) {
            jdbcTemplate.update("DELETE FROM " + table);
            System.out.println("CLEANED " + table);
        }
    }
}
