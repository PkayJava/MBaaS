package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.model.entity.Tables;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.util.file.File;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 5/1/16.
 */
public class ApplicationFunction {

    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final NumberFormat ROUTINE = new DecimalFormat("000");

    public static void backup(JdbcTemplate jdbcTemplate, String applicationId) throws IOException {
        List<String> tables = new ArrayList<>();
        tables.add(Tables.APPLICATION.getName());
        tables.add(Tables.APPLICATION_ROLE.getName());
        tables.add(Tables.COLLECTION.getName());
        tables.add(Tables.COLLECTION_ROLE_PRIVACY.getName());
        tables.add(Tables.COLLECTION_USER_PRIVACY.getName());
        tables.add(Tables.ATTRIBUTE.getName());
        tables.add(Tables.CLIENT.getName());
        tables.add(Tables.QUERY.getName());
        tables.add(Tables.QUERY_PARAMETER.getName());
        tables.add(Tables.QUERY_USER_PRIVACY.getName());
        tables.add(Tables.QUERY_ROLE_PRIVACY.getName());
        tables.add(Tables.JOB.getName());
        tables.add(Tables.JAVASCRIPT.getName());
        tables.add(Tables.FILE.getName());
        tables.add(Tables.ASSET.getName());

        File working = new File(FileUtils.getTempDirectory(), "MBaaS_" + DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
        working.mkdirs();

        long routine = 0;
        for (String table : tables) {
            routine++;
            List<Map<String, Object>> documents = jdbcTemplate.queryForList("SELECT * FROM " + table + " WHERE application_id = ?", applicationId);
            File clientFile = new File(working, ROUTINE.format(routine) + "_" + table + ".sql");
            FileUtils.touch(clientFile);
            for (Map<String, Object> document : documents) {
                FileUtils.write(clientFile, insert(table, document) + "\n\r", true);
            }
        }

        for (String table : jdbcTemplate.queryForList("SELECT " + Tables.COLLECTION.NAME.getName() + " FROM " + Tables.COLLECTION.getName() + " WHERE " + Tables.COLLECTION.APPLICATION_ID.getName() + " = ?", String.class, applicationId)) {
            List<Map<String, Object>> documents = jdbcTemplate.queryForList("SELECT * FROM " + table);
            File clientFile = new File(working, ROUTINE.format(routine) + "_" + table + ".sql");
            FileUtils.touch(clientFile);
            for (Map<String, Object> document : documents) {
                FileUtils.write(clientFile, insert(table, document) + "\n\r", true);
            }
        }
        System.out.println(working.getAbsolutePath());
    }

    public static void restore() {

    }

    protected static String insert(String tableName, Map<String, Object> document) {
        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for (Map.Entry<String, Object> field : document.entrySet()) {
            if (field.getValue() != null) {
                fields.add(field.getKey());
                if (field.getValue() instanceof Date) {
                    values.add("'" + FORMAT.format((Date) field.getValue()) + "'");
                } else if (field.getValue() instanceof Boolean) {
                    values.add(String.valueOf((Boolean) field.getValue()));
                } else if (field.getValue() instanceof String) {
                    values.add("'" + escape((String) field.getValue()) + "'");
                } else if (field.getValue() instanceof Character) {
                    values.add("'" + escape(String.valueOf((Character) field.getValue())) + "'");
                } else if (field.getValue() instanceof Float
                        || field.getValue() instanceof Double
                        || field.getValue() instanceof BigDecimal) {
                    values.add(String.valueOf(((Number) field.getValue()).doubleValue()));
                } else if (field.getValue() instanceof Byte
                        || field.getValue() instanceof Short
                        || field.getValue() instanceof Integer
                        || field.getValue() instanceof Long
                        || field.getValue() instanceof BigInteger) {
                    values.add(String.valueOf(((Number) field.getValue()).intValue()));
                }
            }
        }
        return "INSERT INTO " + tableName + "(" + StringUtils.join(fields, ",") + ") " + "VALUES(" + StringUtils.join(values, ",") + ");";
    }

    protected static String escape(String text) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\r') {
                result.append('\\').append('r');
            } else if (ch == '\n') {
                result.append('\\').append('n');
            } else if (ch == '\'') {
                result.append('\\').append('\'');
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
