package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.model.entity.tables.records.QueryRecord;
import com.angkorteam.mbaas.plain.enums.QueryReturnTypeEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import jdk.nashorn.api.scripting.JSObject;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/13/16.
 */
public class Database {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final DSLContext context;

    public Database(DSLContext context, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.context = context;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Object queryFor(String query) throws DataAccessException {
        QueryTable queryTable = Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.PATH.eq(query)).fetchOneInto(queryTable);

        if (queryRecord == null || queryRecord.getScript() == null || "".equals(queryRecord.getScript()) || SecurityEnum.Denied.getLiteral().equals(queryRecord.getSecurity())) {
            throw new DataAccessResourceFailureException("query is not available");
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        try {
            if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Boolean.getLiteral())) {
                return jdbcTemplate.queryForObject(queryRecord.getScript(), Boolean.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Byte.getLiteral())) {
                return jdbcTemplate.queryForObject(queryRecord.getScript(), Byte.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Short.getLiteral())) {
                return jdbcTemplate.queryForObject(queryRecord.getScript(), Short.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Integer.getLiteral())) {
                return jdbcTemplate.queryForObject(queryRecord.getScript(), Integer.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Long.getLiteral())) {
                return jdbcTemplate.queryForObject(queryRecord.getScript(), Long.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Float.getLiteral())) {
                return jdbcTemplate.queryForObject(queryRecord.getScript(), Float.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Double.getLiteral())) {
                return jdbcTemplate.queryForObject(queryRecord.getScript(), Double.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Character.getLiteral())) {
                return jdbcTemplate.queryForObject(queryRecord.getScript(), Character.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.String.getLiteral())) {
                return jdbcTemplate.queryForObject(queryRecord.getScript(), String.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Time.getLiteral())) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
                Date value = jdbcTemplate.queryForObject(queryRecord.getScript(), Date.class);
                if (value != null) {
                    return dateFormat.format(value);
                }
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Date.getLiteral())) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
                Date value = jdbcTemplate.queryForObject(queryRecord.getScript(), Date.class);
                if (value != null) {
                    return dateFormat.format(value);
                }
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.DateTime.getLiteral())) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                Date value = jdbcTemplate.queryForObject(queryRecord.getScript(), Date.class);
                if (value != null) {
                    return dateFormat.format(value);
                }
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Map.getLiteral())) {
                return jdbcTemplate.queryForMap(queryRecord.getScript());

            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.List.getLiteral())) {
                return jdbcTemplate.queryForList(queryRecord.getScript());
            }
        } catch (EmptyResultDataAccessException e) {
        } catch (IncorrectResultSetColumnCountException | IncorrectResultSizeDataAccessException | BadSqlGrammarException e) {
            throw new DataAccessResourceFailureException(e.getMessage());
        }

        return null;
    }

    public Object queryFor(String query, JSObject js) throws DataAccessException {
        if (js.isArray() || js.isStrictFunction() || js.isStrictFunction()) {
            throw new DataAccessResourceFailureException(query);
        }

        QueryTable queryTable = Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.PATH.eq(query)).fetchOneInto(queryTable);

        if (queryRecord == null || queryRecord.getScript() == null || "".equals(queryRecord.getScript()) || SecurityEnum.Denied.getLiteral().equals(queryRecord.getSecurity())) {
            throw new DataAccessResourceFailureException(query + " is not available");
        }

        Map<String, Object> params = new HashMap<>();
        for (String key : js.keySet()) {
            params.put(key, js.getMember(key));
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        try {
            if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Boolean.getLiteral())) {
                return namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Boolean.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Byte.getLiteral())) {
                return namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Byte.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Short.getLiteral())) {
                return namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Short.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Integer.getLiteral())) {
                return namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Integer.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Long.getLiteral())) {
                return namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Long.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Float.getLiteral())) {
                return namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Float.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Double.getLiteral())) {
                return namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Double.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Character.getLiteral())) {
                return namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Character.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.String.getLiteral())) {
                return namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, String.class);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Time.getLiteral())) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
                Date value = namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Date.class);
                if (value != null) {
                    return dateFormat.format(value);
                }
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Date.getLiteral())) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
                Date value = namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Date.class);
                if (value != null) {
                    return dateFormat.format(value);
                }
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.DateTime.getLiteral())) {
                DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                Date value = namedParameterJdbcTemplate.queryForObject(queryRecord.getScript(), params, Date.class);
                if (value != null) {
                    return dateFormat.format(value);
                }
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.Map.getLiteral())) {
                return namedParameterJdbcTemplate.queryForMap(queryRecord.getScript(), params);
            } else if (queryRecord.getReturnType().equals(QueryReturnTypeEnum.List.getLiteral())) {
                return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params);
            }
        } catch (EmptyResultDataAccessException e) {
        } catch (IncorrectResultSetColumnCountException | IncorrectResultSizeDataAccessException | BadSqlGrammarException e) {
            throw new DataAccessResourceFailureException(e.getMessage());
        }
        return null;
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
