package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.util.file.File;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by socheat on 5/1/16.
 */
public class ApplicationFunction {

    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final NumberFormat ROUTINE = new DecimalFormat("000");

    public static java.io.File backup(JdbcTemplate jdbcTemplate, String applicationId) throws IOException {
        List<String> tables = new ArrayList<>();
        tables.add(Tables.APPLICATION.getName());
        tables.add(Tables.APPLICATION_ROLE.getName());
        tables.add(Tables.COLLECTION.getName());
        tables.add(Tables.COLLECTION_ROLE_PRIVACY.getName());
        tables.add(Tables.COLLECTION_USER_PRIVACY.getName());
        tables.add(Tables.ATTRIBUTE.getName());
        tables.add(Tables.DOCUMENT_ROLE_PRIVACY.getName());
        tables.add(Tables.DOCUMENT_USER_PRIVACY.getName());
        tables.add(Tables.MOBILE.getName());
        tables.add(Tables.CLIENT.getName());
        tables.add(Tables.QUERY.getName());
        tables.add(Tables.QUERY_PARAMETER.getName());
        tables.add(Tables.QUERY_USER_PRIVACY.getName());
        tables.add(Tables.QUERY_ROLE_PRIVACY.getName());
        tables.add(Tables.JOB.getName());
        tables.add(Tables.JAVASCRIPT.getName());
        tables.add(Tables.EAV_BOOLEAN.getName());
        tables.add(Tables.EAV_CHARACTER.getName());
        tables.add(Tables.EAV_DATE.getName());
        tables.add(Tables.EAV_DATE_TIME.getName());
        tables.add(Tables.EAV_DECIMAL.getName());
        tables.add(Tables.EAV_INTEGER.getName());
        tables.add(Tables.EAV_TEXT.getName());
        tables.add(Tables.EAV_TIME.getName());
        tables.add(Tables.EAV_VARCHAR.getName());

        File working = new File(FileUtils.getTempDirectory(), "MBaaS_" + DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
        working.mkdirs();

        long routine = 0;
        for (String table : tables) {
            routine++;
            List<Map<String, Object>> documents = jdbcTemplate.queryForList("SELECT * FROM " + table + " WHERE application_id = ?", applicationId);
            File tableFile = new File(working, ROUTINE.format(routine) + "_" + table + ".sql");
            FileUtils.touch(tableFile);
            for (Map<String, Object> document : documents) {
                FileUtils.write(tableFile, insert(table, document) + "\n\r", true);
            }
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String resourceRepo = configuration.getString(Constants.RESOURCE_REPO);

        {
            // file
            routine++;
            List<Map<String, Object>> documents = jdbcTemplate.queryForList("SELECT * FROM " + Tables.FILE.getName() + " WHERE application_id = ?", applicationId);
            File fileFile = new File(working, ROUTINE.format(routine) + "_" + Tables.FILE.getName() + ".sql");
            FileUtils.touch(fileFile);
            File repo = new File(resourceRepo, "file");
            FileOutputStream fos = new FileOutputStream(new File(working, "file.zip"));
            ZipOutputStream zos = new ZipOutputStream(fos);
            byte[] buffer = new byte[1024];
            for (Map<String, Object> document : documents) {
                String path = (String) document.get(Tables.FILE.PATH.getName());
                String name = (String) document.get(Tables.FILE.NAME.getName());
                FileUtils.write(fileFile, insert(Tables.FILE.getName(), document) + "\n\r", true);
                ZipEntry entry = new ZipEntry("file" + path + "/" + name);
                zos.putNextEntry(entry);
                FileInputStream in = new FileInputStream(new File(repo, path + "/" + name));
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                IOUtils.closeQuietly(in);
                zos.closeEntry();
            }
            zos.close();
        }

        {
            // asset
            routine++;
            List<Map<String, Object>> documents = jdbcTemplate.queryForList("SELECT * FROM " + Tables.ASSET.getName() + " WHERE application_id = ?", applicationId);
            File assetFile = new File(working, ROUTINE.format(routine) + "_" + Tables.ASSET.getName() + ".sql");
            FileUtils.touch(assetFile);
            File repo = new File(resourceRepo, "asset");
            FileOutputStream fos = new FileOutputStream(new File(working, "asset.zip"));
            ZipOutputStream zos = new ZipOutputStream(fos);
            byte[] buffer = new byte[1024];
            for (Map<String, Object> document : documents) {
                String path = (String) document.get(Tables.ASSET.PATH.getName());
                String name = (String) document.get(Tables.ASSET.NAME.getName());
                FileUtils.write(assetFile, insert(Tables.ASSET.getName(), document) + "\n\r", true);
                ZipEntry entry = new ZipEntry("asset" + path + "/" + name);
                zos.putNextEntry(entry);
                FileInputStream in = new FileInputStream(new File(repo, path + "/" + name));
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                IOUtils.closeQuietly(in);
                zos.closeEntry();
            }
            zos.close();
        }

        for (String table : jdbcTemplate.queryForList("SELECT " + Tables.COLLECTION.NAME.getName() + " FROM " + Tables.COLLECTION.getName() + " WHERE " + Tables.COLLECTION.APPLICATION_ID.getName() + " = ?", String.class, applicationId)) {
            List<Map<String, Object>> documents = jdbcTemplate.queryForList("SELECT * FROM " + table);
            File tableFile = new File(working, ROUTINE.format(routine) + "_" + table + ".sql");
            FileUtils.touch(tableFile);
            for (Map<String, Object> document : documents) {
                FileUtils.write(tableFile, insert(table, document) + "\n\r", true);
            }
        }

        {
            File mbaas = new File(FileUtils.getTempDirectory(), "MBaaS_" + DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()) + ".zip");
            FileOutputStream fos = new FileOutputStream(mbaas);
            ZipOutputStream zos = new ZipOutputStream(fos);
            byte[] buffer = new byte[1024];
            for (java.io.File file : working.listFiles()) {
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);
                FileInputStream in = new FileInputStream(file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                IOUtils.closeQuietly(in);
                zos.closeEntry();
            }
            zos.close();
            FileUtils.deleteDirectory(working);
            return mbaas;
        }
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
