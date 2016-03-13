package com.angkorteam.mbaas.server.nashorn;

import jdk.nashorn.api.scripting.JSObject;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/13/16.
 */
public class Database {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public Database(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Map<String, Object> queryForMap(String sql) throws DataAccessException {
        if (!sql.trim().substring(0, "select".length()).toLowerCase().equals("select")) {
            throw new DataAccessResourceFailureException(sql);
        }
        return this.jdbcTemplate.queryForMap(sql);
    }

    public Map<String, Object> queryForMap(String sql, JSObject js) throws DataAccessException {
        if (js.isArray() || js.isStrictFunction() || js.isStrictFunction()) {
            throw new DataAccessResourceFailureException(sql);
        }
        Map<String, Object> paramMap = new HashMap<>();
        for (String key : js.keySet()) {
            paramMap.put(key, js.getMember(key));
        }

        if (!sql.trim().substring(0, "select".length()).toLowerCase().equals("select")) {
            throw new DataAccessResourceFailureException(sql);
        }
        return this.namedParameterJdbcTemplate.queryForMap(sql, paramMap);
    }

    public List<Map<String, Object>> queryForList(String sql) throws DataAccessException {
        if (!sql.trim().substring(0, "select".length()).toLowerCase().equals("select")) {
            throw new DataAccessResourceFailureException(sql);
        }
        return this.jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> queryForList(String sql, JSObject js) throws DataAccessException {
        if (js.isArray() || js.isStrictFunction() || js.isStrictFunction()) {
            throw new DataAccessResourceFailureException(sql);
        }
        Map<String, Object> paramMap = new HashMap<>();
        for (String key : js.keySet()) {
            paramMap.put(key, js.getMember(key));
        }
        if (!sql.trim().substring(0, "select".length()).toLowerCase().equals("select")) {
            throw new DataAccessResourceFailureException(sql);
        }
        return this.namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    public int update(String sql) throws DataAccessException {
        audit(sql);
        return this.jdbcTemplate.update(sql);
    }

    public int update(String sql, JSObject js) throws DataAccessException {
        if (js.isArray() || js.isStrictFunction() || js.isStrictFunction()) {
            throw new DataAccessResourceFailureException(sql);
        }
        Map<String, Object> paramMap = new HashMap<>();
        for (String key : js.keySet()) {
            paramMap.put(key, js.getMember(key));
        }
        audit(sql);
        return this.namedParameterJdbcTemplate.update(sql, paramMap);
    }

    private void audit(String sql) {
        String sqlTrimed = sql.trim();
        if (sqlTrimed.substring(0, "drop".length()).toLowerCase().equals("drop")) {
            throw new DataAccessResourceFailureException(sql);
        }
        if (sqlTrimed.substring(0, "create".length()).toLowerCase().equals("create")) {
            throw new DataAccessResourceFailureException(sql);
        }
        if (sqlTrimed.substring(0, "alter".length()).toLowerCase().equals("alter")) {
            throw new DataAccessResourceFailureException(sql);
        }
        if (sqlTrimed.substring(0, "call".length()).toLowerCase().equals("call")) {
            throw new DataAccessResourceFailureException(sql);
        }
        if (sqlTrimed.substring(0, "kill".length()).toLowerCase().equals("kill")) {
            throw new DataAccessResourceFailureException(sql);
        }
    }
}
