//package com.angkorteam.mbaas.server.function;
//
//import com.angkorteam.mbaas.server.Jdbc;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * Created by socheat on 7/11/16.
// */
//public class RestoreFunction {
//
//    public static void backup(Gson gson, JdbcTemplate jdbcTemplate, String tableName, String uuid) {
//        Map<String, Object> record = jdbcTemplate.queryForMap("SELECT * FROM " + tableName + " WHERE " + tableName + "_id = ?", uuid);
//        String json = gson.toJson(record);
//        jdbcTemplate.update("INSERT INTO " + Jdbc.RESTORE + "(" + Jdbc.Restore.RESTORE_ID + ", " + Jdbc.Restore.DATE_CREATED + ", " + Jdbc.Restore.TABLE_NAME + ", " + Jdbc.Restore.FIELDS + ") values(?,?,?,?)", UUID.randomUUID().toString(), new Date(), tableName, json);
//    }
//
//    public static void restore(Gson gson, JdbcTemplate jdbcTemplate, String restoreId) {
//        Map<String, Object> restore = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.RESTORE + " WHERE " + Jdbc.Restore.RESTORE_ID + " = ?", restoreId);
//        String json = (String) restore.get(Jdbc.Restore.FIELDS);
//        String tableName = (String) restore.get(Jdbc.Restore.TABLE_NAME);
//        Map<String, Object> fields = gson.fromJson(json, new TypeToken<Map<String, String>>() {
//        }.getType());
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
//        jdbcInsert.withTableName(tableName);
//        jdbcInsert.setColumnNames(new ArrayList<>(fields.keySet()));
//        jdbcInsert.execute(fields);
//        jdbcTemplate.update("DELETE FROM " + Jdbc.RESTORE + " WHERE " + Jdbc.Restore.RESTORE_ID + " = ?", restoreId);
//    }
//}
