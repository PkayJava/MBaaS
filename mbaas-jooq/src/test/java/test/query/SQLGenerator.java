package test.query;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by socheat on 10/23/16.
 */
public class SQLGenerator {

    private static AtomicLong collectionId = new AtomicLong(0);

    private static AtomicLong attributeId = new AtomicLong(0);

    private static AtomicLong instanceAttributeId = new AtomicLong(0);

    private static AtomicLong primaryAttributeId = new AtomicLong(0);

    private static AtomicLong indexAttributeId = new AtomicLong(0);

    private static AtomicLong primary_attribute_id = new AtomicLong(0);

    private static AtomicLong index_attribute_id = new AtomicLong(0);

    private static AtomicLong orderId = new AtomicLong(0);

    private static AtomicLong instance_attribute_id = new AtomicLong(0);

    public static void main(String[] args) throws IOException {

        int until = 6000;

        File folder = new File("/home/socheat/Documents/git/PkayJava/MBaaS/mbaas-jooq/src/main/resources/db/migration");
        Map<Integer, File> files = new TreeMap();
        for (File file : folder.listFiles()) {
            String name = file.getName();
            Integer lastIndex = name.indexOf("__");
            Integer id = Integer.valueOf(file.getName().substring(1, lastIndex));
            files.put(id, file);

        }
        List<Table> tables = new ArrayList<>();
        for (Map.Entry<Integer, File> entry : files.entrySet()) {
            Integer version = entry.getKey();
            if (version <= until) {
                File file = entry.getValue();
                System.out.println(file.getName());
                Table table = parseTable(file);
                tables.add(table);
            }
        }
        printSQL(tables);
    }

    public static void printSQL(List<Table> tables) throws IOException {
        File file = new File("/home/socheat/Documents/git/PkayJava/MBaaS/mbaas-jooq/src/main/resources/db/migration/V7000__schema_metadata.sql");
        file.delete();
        FileUtils.touch(file);
        // insert into collection table
        FileUtils.writeStringToFile(file, "# collection table", "UTF-8", true);
        FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        for (Table table : tables) {
            String script = String.format("INSERT INTO collection(collection_id, name, locked, system, mutable) VALUES(%s, '%s', %s, %s, %s);",
                    table.getId(), table.getName(), "FALSE", "TRUE", String.valueOf(table.isMutable()).toUpperCase());
            FileUtils.writeStringToFile(file, script, "UTF-8", true);
            FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        }
        FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        // insert into attribute table
        FileUtils.writeStringToFile(file, "# attribute table", "UTF-8", true);
        FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        for (Table table : tables) {
            String collectionId = table.getId();
            for (Field field : table.getFields().values()) {
                String script = String.format("INSERT INTO attribute (attribute_id, collection_id, name, type, length, `precision`, allow_null, eav, system) VALUES (%s, %s, '%s', '%s', %s, %s, %s, %s, %s);",
                        field.getId(), collectionId, field.getName(), field.getType(), field.getLength() == null ? "NULL" : field.getLength(), field.getPrecision() == null ? "NULL" : field.getPrecision(), String.valueOf(field.isNullable()).toUpperCase(), "FALSE", "TRUE");
                FileUtils.writeStringToFile(file, script, "UTF-8", true);
                FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
            }
        }
        FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        // insert into instance_attribute table
        FileUtils.writeStringToFile(file, "# instance_attribute", "UTF-8", true);
        FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        for (Table table : tables) {
            String collectionId = table.getId();
            for (Field field : table.getInstanceField()) {
                String script = String.format("INSERT INTO instance_attribute (instance_attribute_id, collection_id, attribute_id, `order`, system) VALUES (%s, %s, %s, %s, %s);",
                        instance_attribute_id.incrementAndGet(), collectionId, field.getId(), orderId.incrementAndGet(), "true");
                FileUtils.writeStringToFile(file, script, "UTF-8", true);
                FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
            }
        }
        FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        // insert into primary_attribute table
        FileUtils.writeStringToFile(file, "# primary_attribute", "UTF-8", true);
        FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        for (Table table : tables) {
            String collectionId = table.getId();
            for (Field field : table.getPrimaryField()) {
                String script = String.format("INSERT INTO primary_attribute (primary_attribute_id, collection_id, attribute_id, system) VALUES (%s, %s, %s, %s);",
                        primary_attribute_id.incrementAndGet(), collectionId, field.getId(), "true");
                FileUtils.writeStringToFile(file, script, "UTF-8", true);
                FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
            }
        }
        FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        // insert into index_attribute table
        FileUtils.writeStringToFile(file, "# index_attribute table", "UTF-8", true);
        FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
        for (Table table : tables) {
            String collectionId = table.getId();
            for (Map.Entry<String, List<Field>> entry : table.getIndexField().entrySet()) {
                for (Field field : entry.getValue()) {
                    String script = String.format("INSERT INTO index_attribute (index_attribute_id, collection_id, attribute_id, name, type, system) VALUES (%s, %s, %s, '%s', '%s', %s);",
                            index_attribute_id.incrementAndGet(), collectionId, field.getId(), entry.getKey(), "KEY", "true");
                    FileUtils.writeStringToFile(file, script, "UTF-8", true);
                    FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
                }
            }
            for (Map.Entry<String, List<Field>> entry : table.getUniqueField().entrySet()) {
                for (Field field : entry.getValue()) {
                    String script = String.format("INSERT INTO index_attribute (index_attribute_id, collection_id, attribute_id, name, type, system) VALUES (%s, %s, %s, '%s', '%s', %s);",
                            index_attribute_id.incrementAndGet(), collectionId, field.getId(), entry.getKey(), "UNIQUE KEY", "true");
                    FileUtils.writeStringToFile(file, script, "UTF-8", true);
                    FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
                }
            }
            for (Map.Entry<String, List<Field>> entry : table.getFulltextField().entrySet()) {
                for (Field field : entry.getValue()) {
                    String script = String.format("INSERT INTO index_attribute (index_attribute_id, collection_id, attribute_id, name, type, system) VALUES (%s, %s, %s, '%s', '%s', %s);",
                            index_attribute_id.incrementAndGet(), collectionId, field.getId(), entry.getKey(), "FULLTEXT KEY", "true");
                    FileUtils.writeStringToFile(file, script, "UTF-8", true);
                    FileUtils.writeStringToFile(file, "\n", "UTF-8", true);
                }
            }
        }
    }

    public static Table parseTable(File file) throws IOException {
        Table table = new Table();
        table.setId(String.valueOf(collectionId.incrementAndGet()));
        String ddl = FileUtils.readFileToString(file, "UTF-8");
        int firstSpace = ddl.indexOf(' ');
        int secondSpace = ddl.indexOf(' ', firstSpace + 1);
        int thirdSpace = ddl.indexOf(' ', secondSpace + 1);
        String tableName = ddl.substring(secondSpace + 1, thirdSpace).replace("`", "");
        table.setName(tableName);
        table.setMutable(ddl.contains("#MUTABLE"));
        int firstBrase = ddl.indexOf("(");
        int lastBrase = ddl.lastIndexOf(")");
        String fields = ddl.substring(firstBrase + 1, lastBrase);

        Map<String, Field> fieldMap = new HashMap<>();
        List<String> primary = new ArrayList<>();
        List<String> instance = new ArrayList<>();
        Map<String, List<String>> index = new HashMap<>();
        Map<String, List<String>> fulltext = new HashMap<>();
        Map<String, List<String>> unique = new HashMap<>();
        for (String line : StringUtils.split(fields, '\n')) {
            if (line.equals("\r") || line.equals("\n")) {
                continue;
            }
            StringBuffer bf = new StringBuffer();
            for (char ch : line.toCharArray()) {
                if (ch == '\r' || ch == '\n') {
                    bf.append(" ");
                } else {
                    bf.append(ch);
                }
            }
            String temp = bf.toString();
            while (temp.contains("  ")) {
                temp = temp.replaceAll("  ", " ");
            }
            temp = temp.trim();
            if (temp.startsWith("KEY ") || temp.startsWith("UNIQUE KEY ") || temp.startsWith("FULLTEXT KEY ")) {

                int s = temp.indexOf('`') + 1;
                int e = temp.indexOf('`', s);
                String key = temp.substring(s, e).trim();
                s = temp.indexOf('(', e) + 1;
                e = temp.indexOf(')', s);
                String names = temp.substring(s, e);
                String[] ppp = names.split(",");
                List<String> values = new ArrayList<>();
                for (String pp : ppp) {
                    values.add(pp.trim().replaceAll("`", ""));
                }
                if (temp.startsWith("KEY ")) {
                    index.put(key, values);
                } else if (temp.startsWith("UNIQUE KEY ")) {
                    unique.put(key, values);
                } else if (temp.startsWith("FULLTEXT KEY ")) {
                    fulltext.put(key, values);
                }
            } else if (temp.startsWith("PRIMARY KEY ")) {
                int s = temp.indexOf('(') + 1;
                int e = temp.indexOf(')');
                temp = temp.substring(s, e);
                String[] ppp = temp.split(",");
                List<String> values = new ArrayList<>();
                for (String pp : ppp) {
                    values.add(pp.trim().replaceAll("`", ""));
                }
                primary.addAll(values);
            } else {
                firstSpace = temp.indexOf(' ');
                String fieldName = temp.substring(0, firstSpace).replaceAll("`", "");
                if (temp.contains("#INSTANCE")) {
                    instance.add(fieldName);
                }
                String dbType = temp.substring(firstSpace + 1);
                String type = null;
                Boolean allowNull = !dbType.contains("NOT NULL");
                Long length = null;
                Long precision = null;
                if (dbType.startsWith("VARCHAR(")) {
                    type = "String";
                    int p = dbType.indexOf("(");
                    int pp = dbType.indexOf(")");
                    length = Long.valueOf(dbType.substring(p + 1, pp));
                } else if (dbType.startsWith("BIT(")) {
                    type = "Boolean";
                } else if (dbType.startsWith("INT(")) {
                    type = "Long";
                    int p = dbType.indexOf("(");
                    int pp = dbType.indexOf(")");
                    length = Long.valueOf(dbType.substring(p + 1, pp));
                } else if (dbType.startsWith("DECIMAL(")) {
                    type = "Double";
                    int p = dbType.indexOf("(");
                    int pp = dbType.indexOf(")");
                    String pppppp = dbType.substring(p + 1, pp);
                    String ppp[] = pppppp.split(",");
                    length = Long.valueOf(ppp[0].trim());
                    precision = Long.valueOf(ppp[1].trim());
                } else if (dbType.startsWith("TEXT")) {
                    type = "Text";
                } else if (dbType.startsWith("DATETIME") || dbType.startsWith("TIMESTAMP")) {
                    type = "DateTime";
                } else if (dbType.startsWith("TIME")) {
                    type = "Time";
                } else if (dbType.startsWith("DATE")) {
                    type = "Date";
                } else {
                    throw new IllegalArgumentException(temp);
                }
                Field field = new Field();
                field.setId(String.valueOf(attributeId.incrementAndGet()));
                field.setNullable(allowNull);
                field.setName(fieldName);
                field.setType(type);
                field.setLength(length);
                field.setPrecision(precision);
                fieldMap.put(fieldName, field);
            }
        }
        table.getFields().putAll(fieldMap);
        for (String field : primary) {
            table.getPrimaryField().add(table.getFields().get(field));
        }
        for (String field : instance) {
            table.getInstanceField().add(table.getFields().get(field));
        }
        for (Map.Entry<String, List<String>> entry : index.entrySet()) {
            String name = entry.getKey();
            List<Field> values = new ArrayList<>();
            for (String field : entry.getValue()) {
                values.add(table.getFields().get(field));
            }
            table.getIndexField().put(name, values);
        }
        for (Map.Entry<String, List<String>> entry : unique.entrySet()) {
            String name = entry.getKey();
            List<Field> values = new ArrayList<>();
            for (String field : entry.getValue()) {
                values.add(table.getFields().get(field));
            }
            table.getUniqueField().put(name, values);
        }
        for (Map.Entry<String, List<String>> entry : fulltext.entrySet()) {
            String name = entry.getKey();
            List<Field> values = new ArrayList<>();
            for (String field : entry.getValue()) {
                values.add(table.getFields().get(field));
            }
            table.getFulltextField().put(name, values);
        }
        return table;
    }

    public static class Table {
        private String id;
        private String name;
        private boolean mutable;
        private Map<String, Field> fields = new HashMap<>();
        private List<Field> primaryField = new ArrayList<>();
        private List<Field> instanceField = new ArrayList<>();
        private Map<String, List<Field>> indexField = new HashMap<>();
        private Map<String, List<Field>> fulltextField = new HashMap<>();
        private Map<String, List<Field>> uniqueField = new HashMap<>();

        public boolean isMutable() {
            return mutable;
        }

        public void setMutable(boolean mutable) {
            this.mutable = mutable;
        }

        public String getId() {
            return id;
        }

        public Map<String, List<Field>> getFulltextField() {
            return fulltextField;
        }

        public void setFulltextField(Map<String, List<Field>> fulltextField) {
            this.fulltextField = fulltextField;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Field> getFields() {
            return fields;
        }

        public void setFields(Map<String, Field> fields) {
            this.fields = fields;
        }

        public List<Field> getPrimaryField() {
            return primaryField;
        }

        public void setPrimaryField(List<Field> primaryField) {
            this.primaryField = primaryField;
        }

        public Map<String, List<Field>> getIndexField() {
            return indexField;
        }

        public void setIndexField(Map<String, List<Field>> indexField) {
            this.indexField = indexField;
        }

        public List<Field> getInstanceField() {
            return instanceField;
        }

        public void setInstanceField(List<Field> instanceField) {
            this.instanceField = instanceField;
        }

        public Map<String, List<Field>> getUniqueField() {
            return uniqueField;
        }

        public void setUniqueField(Map<String, List<Field>> uniqueField) {
            this.uniqueField = uniqueField;
        }
    }

    public static class Field {
        private String id;
        private String name;
        private String type;
        private boolean nullable;
        private Long length;
        private Long precision;

        public Long getPrecision() {
            return precision;
        }

        public void setPrecision(Long precision) {
            this.precision = precision;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }

        public Long getLength() {
            return length;
        }

        public void setLength(Long length) {
            this.length = length;
        }
    }

    public static class Relation {

        private String name;
        private Table table;
        private Field field;
        private String type;
        private Table joinTable;
        private Field joinField;
        private Table manyTable;
        private Field manyJoinField;
        private Field manyReverseField;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Table getTable() {
            return table;
        }

        public void setTable(Table table) {
            this.table = table;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Table getJoinTable() {
            return joinTable;
        }

        public void setJoinTable(Table joinTable) {
            this.joinTable = joinTable;
        }

        public Field getJoinField() {
            return joinField;
        }

        public void setJoinField(Field joinField) {
            this.joinField = joinField;
        }

        public Table getManyTable() {
            return manyTable;
        }

        public void setManyTable(Table manyTable) {
            this.manyTable = manyTable;
        }

        public Field getManyJoinField() {
            return manyJoinField;
        }

        public void setManyJoinField(Field manyJoinField) {
            this.manyJoinField = manyJoinField;
        }

        public Field getManyReverseField() {
            return manyReverseField;
        }

        public void setManyReverseField(Field manyReverseField) {
            this.manyReverseField = manyReverseField;
        }
    }

}
