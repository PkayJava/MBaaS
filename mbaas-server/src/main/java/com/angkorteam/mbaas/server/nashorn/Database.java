package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.QueryRecord;
import com.angkorteam.mbaas.plain.Identity;
import com.angkorteam.mbaas.plain.enums.QueryReturnTypeEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.plain.request.document.DocumentModifyRequest;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import jdk.nashorn.api.scripting.JSObject;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.dao.*;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.script.ScriptException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by socheat on 3/13/16.
 */
public class Database {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final MBaaS mbaas;
    private final DSLContext context;
    private final Identity identity;

    public Database(DSLContext context, Identity identity, JdbcTemplate jdbcTemplate, MBaaS mbaas) {
        this.jdbcTemplate = jdbcTemplate;
        this.identity = identity;
        this.context = context;
        this.mbaas = mbaas;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public String uuid() {
        return UUID.randomUUID().toString();
    }

    public String insert(String collection, JSObject document) throws ScriptException {
        String ownerUserId = identity.getUserId();
        if (ownerUserId == null || "".equals(ownerUserId)) {
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            String userAdmin = configuration.getString(Constants.USER_ADMIN);
            UserTable userTable = com.angkorteam.mbaas.model.entity.Tables.USER.as("userTable");
            ownerUserId = jdbcTemplate.queryForObject("SELECT " + userTable.USER_ID.getName() + " FROM " + userTable.getName() + " WHERE " + userTable.LOGIN.getName() + " = ?", String.class, userAdmin);
        }
        return insert(ownerUserId, collection, document);
    }

    public String insert(String ownerUserId, String collection, JSObject document) {
        Map<String, Object> params = new HashMap<>();
        if (document.isArray() || document.isStrictFunction() || document.isFunction()) {
            throw new DataIntegrityViolationException("could not insert into " + collection);
        }
        parse(params, document, collection);
        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setDocument(params);
        return DocumentFunction.insertDocument(context, jdbcTemplate, ownerUserId, collection, request);
    }

    private void parse(Map<String, Object> params, JSObject document, String collection) {
        for (String key : document.keySet()) {
            Object value = document.getMember(key);
            if (value instanceof Boolean
                    || value instanceof Byte
                    || value instanceof Short
                    || value instanceof Integer
                    || value instanceof Long
                    || value instanceof Float
                    || value instanceof Double
                    || value instanceof Character
                    || value instanceof String
                    || value instanceof Date) {
                params.put(key, value);
            } else {
                throw new DataIntegrityViolationException("could not insert into " + collection);
            }
        }
    }

    public void delete(String collection, String documentId) {
        DocumentFunction.deleteDocument(context, jdbcTemplate, collection, documentId);
    }

    public void modify(String collection, String documentId, JSObject document) {
        Map<String, Object> params = new HashMap<>();
        if (document.isArray() || document.isStrictFunction() || document.isFunction()) {
            throw new DataIntegrityViolationException("could not insert into " + collection);
        }
        parse(params, document, collection);
        DocumentModifyRequest request = new DocumentModifyRequest();
        request.setDocument(params);
        DocumentFunction.modifyDocument(context, jdbcTemplate, collection, documentId, request);
    }

    public Object executeQuery(String query) throws DataAccessException {
        QueryTable queryTable = com.angkorteam.mbaas.model.entity.Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.NAME.eq(query)).fetchOneInto(queryTable);

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
                if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Boolean.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Boolean.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Byte.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Byte.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Short.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Short.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Integer.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Integer.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Long.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Long.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Float.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Float.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Double.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Double.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Character.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Character.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.String.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), String.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Time.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Date.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Date.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Date.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.DateTime.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript(), Date.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Map.getLiteral())) {
                    return jdbcTemplate.queryForList(queryRecord.getScript());
                } else {
                    return jdbcTemplate.queryForList(queryRecord.getScript());
                }
            }
        } catch (EmptyResultDataAccessException e) {
        } catch (IncorrectResultSetColumnCountException | IncorrectResultSizeDataAccessException | BadSqlGrammarException e) {
            throw new DataAccessResourceFailureException(e.getMessage());
        }

        return null;
    }

    public Object executeQuery(String query, JSObject js) throws DataAccessException {
        if (js.isArray() || js.isStrictFunction() || js.isStrictFunction()) {
            throw new DataAccessResourceFailureException(query);
        }

        QueryTable queryTable = com.angkorteam.mbaas.model.entity.Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.NAME.eq(query)).fetchOneInto(queryTable);

        if (queryRecord == null || queryRecord.getScript() == null || "".equals(queryRecord.getScript()) || SecurityEnum.Denied.getLiteral().equals(queryRecord.getSecurity())) {
            throw new DataAccessResourceFailureException(query + " is not available");
        }

        Map<String, Object> params = new HashMap<>();
        for (String key : js.keySet()) {
            Object object = js.getMember(key);
            if (object instanceof JSObject) {
                if (((JSObject) object).isArray()) {
                    List<Object> values = new LinkedList<>();
                    for (Object value : ((JSObject) object).values()) {
                        values.add(value);
                    }
                    params.put(key, values);
                } else {
                    throw new DataAccessResourceFailureException(key + " type is not supported");
                }
            } else {
                params.put(key, object);
            }
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
                if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Boolean.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Boolean.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Byte.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Byte.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Short.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Short.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Integer.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Integer.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Long.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Long.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Float.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Float.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Double.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Double.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Character.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Character.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.String.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, String.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Time.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Date.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Date.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Date.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.DateTime.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params, Date.class);
                } else if (queryRecord.getReturnSubType().equals(QueryReturnTypeEnum.Map.getLiteral())) {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params);
                } else {
                    return namedParameterJdbcTemplate.queryForList(queryRecord.getScript(), params);
                }
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

    public boolean queryForBoolean(String sql) throws DataAccessException {
        return queryForObject(sql, boolean.class);
    }

    public boolean queryForBoolean(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, boolean.class);
    }

    public byte queryForByte(String sql) throws DataAccessException {
        return queryForObject(sql, byte.class);
    }

    public byte queryForByte(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, byte.class);
    }

    public short queryForShort(String sql) throws DataAccessException {
        return queryForObject(sql, short.class);
    }

    public short queryForShort(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, short.class);
    }

    public int queryForInteger(String sql) throws DataAccessException {
        return queryForObject(sql, int.class);
    }

    public int queryForInteger(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, int.class);
    }

    public long queryForLong(String sql) throws DataAccessException {
        return queryForObject(sql, long.class);
    }

    public long queryForLong(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, long.class);
    }

    public float queryForFloat(String sql) throws DataAccessException {
        return queryForObject(sql, float.class);
    }

    public float queryForFloat(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, float.class);
    }

    public double queryForDouble(String sql) throws DataAccessException {
        return queryForObject(sql, double.class);
    }

    public double queryForDouble(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, double.class);
    }

    public Date queryForDate(String sql) throws DataAccessException {
        return queryForObject(sql, Date.class);
    }

    public Date queryForDate(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, Date.class);
    }

    public String queryForString(String sql) throws DataAccessException {
        return queryForObject(sql, String.class);
    }

    public String queryForString(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, String.class);
    }

    public char queryForCharacter(String sql) throws DataAccessException {
        return queryForObject(sql, char.class);
    }

    public char queryForCharacter(String sql, JSObject js) throws DataAccessException {
        return queryForObject(sql, js, char.class);
    }

    private <T> T queryForObject(String sql, Class<T> clazz) throws DataAccessException {
        if (!sql.trim().substring(0, "select".length()).toLowerCase().equals("select")) {
            throw new DataAccessResourceFailureException(sql);
        }
        return this.jdbcTemplate.queryForObject(sql, clazz);
    }

    private <T> T queryForObject(String sql, JSObject js, Class<T> clazz) throws DataAccessException {
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
        return this.namedParameterJdbcTemplate.queryForObject(sql, paramMap, clazz);
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

    private <T> List<T> queryForListObject(String sql, Class<T> clazz) throws DataAccessException {
        if (!sql.trim().substring(0, "select".length()).toLowerCase().equals("select")) {
            throw new DataAccessResourceFailureException(sql);
        }
        return this.jdbcTemplate.queryForList(sql, clazz);
    }

    private <T> List<T> queryForListObject(String sql, JSObject js, Class<T> clazz) throws DataAccessException {
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
        return this.namedParameterJdbcTemplate.queryForList(sql, paramMap, clazz);
    }

    public List<Boolean> queryForListBoolean(String sql) throws DataAccessException {
        return this.queryForListObject(sql, Boolean.class);
    }

    public List<Boolean> queryForListBoolean(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, Boolean.class);
    }

    public List<Byte> queryForListByte(String sql) throws DataAccessException {
        return this.queryForListObject(sql, Byte.class);
    }

    public List<Byte> queryForListByte(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, Byte.class);
    }

    public List<Short> queryForListShort(String sql) throws DataAccessException {
        return this.queryForListObject(sql, Short.class);
    }

    public List<Short> queryForListShort(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, Short.class);
    }

    public List<Integer> queryForListInteger(String sql) throws DataAccessException {
        return this.queryForListObject(sql, Integer.class);
    }

    public List<Integer> queryForListInteger(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, Integer.class);
    }

    public List<Long> queryForListLong(String sql) throws DataAccessException {
        return this.queryForListObject(sql, Long.class);
    }

    public List<Long> queryForListLong(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, Long.class);
    }

    public List<Float> queryForListFloat(String sql) throws DataAccessException {
        return this.queryForListObject(sql, Float.class);
    }

    public List<Float> queryForListFloat(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, Float.class);
    }

    public List<Double> queryForListDouble(String sql) throws DataAccessException {
        return this.queryForListObject(sql, Double.class);
    }

    public List<Double> queryForListDouble(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, Double.class);
    }

    public List<String> queryForListString(String sql) throws DataAccessException {
        return this.queryForListObject(sql, String.class);
    }

    public List<String> queryForListString(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, String.class);
    }

    public List<Character> queryForListCharacter(String sql) throws DataAccessException {
        return this.queryForListObject(sql, Character.class);
    }

    public List<Character> queryForListCharacter(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, Character.class);
    }

    public List<Date> queryForListDate(String sql) throws DataAccessException {
        return this.queryForListObject(sql, Date.class);
    }

    public List<Date> queryForListDate(String sql, JSObject js) throws DataAccessException {
        return this.queryForListObject(sql, js, Date.class);
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
