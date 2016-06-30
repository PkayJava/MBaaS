package com.angkorteam.mbaas.server.wicket.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Separate startup class for people that want to run the examples directly. Use parameter
 * -Dcom.sun.management.jmxremote to startup JMX (and e.g. connect with jconsole).
 */
public class Source {

    private static final List<String> WORDS = new ArrayList<>();

    private static final List<String> PHONE_NUMBER_PREFIX = new ArrayList<>();

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
        generateFoundationExamination(dataSource, 300);
        submitFoundationExaminationResult(dataSource, 0.7f);
        goEnrollment(dataSource, 0.4f);
    }

    public static void goEnrollment(DataSource dataSource, float percent) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> personIds = queryIdentity(dataSource, "person");
        int total = (int) (personIds.size() * percent);
        while (!personIds.isEmpty()) {
            String personId = personIds.remove(RandomUtils.nextInt(0, personIds.size()));
            if (personIds.size() > total) {
                jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'CLOSED', reason = 'No Time', placement_test_score = ? WHERE foundation_examination_id = ?", RandomUtils.nextDouble(30d, 40d), personId);
            } else {
                jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'CLOSED', reason = 'Enrolled', placement_test_score = ? WHERE foundation_examination_id = ?", RandomUtils.nextDouble(60d, 100d), personId);
            }
        }
    }

    public static void submitFoundationExaminationResult(DataSource dataSource, float goodPercent) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> foundationExaminationIds = queryIdentity(dataSource, "foundation_examination");
        int total = (int) (foundationExaminationIds.size() * goodPercent);
        while (!foundationExaminationIds.isEmpty()) {
            String foundationExaminationId = foundationExaminationIds.remove(RandomUtils.nextInt(0, foundationExaminationIds.size()));
            if (foundationExaminationIds.size() > total) {
                jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'OPENED', placement_test_score = ? WHERE foundation_examination_id = ?", RandomUtils.nextDouble(30d, 40d), foundationExaminationId);
            } else {
                jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'OPENED', placement_test_score = ? WHERE foundation_examination_id = ?", RandomUtils.nextDouble(60d, 100d), foundationExaminationId);
            }
        }
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 1' WHERE placement_test_score BETWEEN ? AND ?", 0, 10);
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 2' WHERE placement_test_score BETWEEN ? AND ?", 10, 20);
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 3' WHERE placement_test_score BETWEEN ? AND ?", 20, 30);
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 4' WHERE placement_test_score BETWEEN ? AND ?", 30, 40);
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 5' WHERE placement_test_score BETWEEN ? AND ?", 40, 50);
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 6' WHERE placement_test_score BETWEEN ? AND ?", 50, 60);
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 7' WHERE placement_test_score BETWEEN ? AND ?", 60, 70);
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 8' WHERE placement_test_score BETWEEN ? AND ?", 70, 80);
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 9' WHERE placement_test_score BETWEEN ? AND ?", 80, 90);
        jdbcTemplate.update("UPDATE foundation_examination SET opportunity = 'Level 10' WHERE placement_test_score BETWEEN ? AND ?", 90, 100);
    }

    public static void generateFoundationExamination(DataSource dataSource, int quantity) {
        List<String> personIds = queryIdentity(dataSource, "person");
        List<Map<String, Object>> records = new ArrayList<>(quantity);
        int remain = quantity;
        while (!personIds.isEmpty()) {
            remain = remain - 1;
            Map<String, Object> record = new HashMap<>();
            LocalDate date = LocalDate.of(RandomUtils.nextInt(2000, LocalDate.now().getYear() + 1), RandomUtils.nextInt(1, 13), RandomUtils.nextInt(1, 29));
            record.put("date_token", date);
            record.put("opportunity", "N/A");
            record.put("person_id", personIds.remove(RandomUtils.nextInt(0, personIds.size())));
            record.put("reason", "N/A");
            record.put("date_expired", date.plusWeeks(1));
            records.add(record);
        }
        personIds = queryIdentity(dataSource, "person");
        while (remain > 0) {
            remain = remain - 1;
            Map<String, Object> record = new HashMap<>();
            LocalDate date = LocalDate.of(RandomUtils.nextInt(2000, LocalDate.now().getYear() + 1), RandomUtils.nextInt(1, 13), RandomUtils.nextInt(1, 29));
            record.put("date_token", date);
            record.put("opportunity", "N/A");
            record.put("person_id", personIds.get(RandomUtils.nextInt(0, personIds.size())));
            record.put("reason", "N/A");
            record.put("date_expired", date.plusWeeks(1));
            records.add(record);
        }
        insert(dataSource, "foundation_examination", Arrays.asList("date_token", "opportunity", "person_id", "reason", "date_expired"), records);
    }

    public static List<String> queryIdentity(DataSource dataSource, String tableName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.queryForList("SELECT " + tableName + "_id FROM " + tableName, String.class);
    }

    public static List<String> queryIdentity(DataSource dataSource, String tableName, Map<String, Object> wheres) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<String> names = new LinkedList<>();
        List<Object> values = new LinkedList<>();
        for (Map.Entry<String, Object> where : wheres.entrySet()) {
            names.add(where.getKey() + " = ?");
            values.add(where.getValue());
        }
        return jdbcTemplate.queryForList("SELECT " + tableName + "_id FROM " + tableName + " WHERE " + StringUtils.join(names, " AND "), String.class, values.toArray());
    }

    public static void generatePerson(DataSource dataSource, int quantity) {
        List<Map<String, Object>> records = new ArrayList<>(quantity);
        for (int i = 1; i <= quantity; i++) {
            Map<String, Object> record = new HashMap<>();
            String name = randomName();
            record.put("name", name);
            record.put("picture", name);
            record.put("noted", "Person");
            record.put("type", 1);
            records.add(record);
        }
        insert(dataSource, "person", Arrays.asList("name", "picture", "noted", "type"), records);
    }

    @SuppressWarnings("unchecked")
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
        Map<String, Object>[] batchs = fields.toArray(new Map[fields.size()]);
        jdbcInsert.executeBatch(batchs);
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

    public static String randomPhoneNumber() {
        return "";
    }

    public static void clean(DataSource dataSource, String... tables) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        for (String table : tables) {
            jdbcTemplate.update("DELETE FROM " + table);
            System.out.println("CLEANED " + table);
        }
    }
}
