//package com.angkorteam.mbaas.server.select2;
//
//import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
//import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import com.google.gson.Gson;
//import org.apache.wicket.model.IModel;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by socheat on 8/22/16.
// */
//public class HttpQueryChoiceProvider extends MultipleChoiceProvider<Map<String, Object>> {
//
//    private String applicationCode;
//
//    public HttpQueryChoiceProvider(String applicationCode) {
//        this.applicationCode = applicationCode;
//    }
//
//    @Override
//    public List<Map<String, Object>> toChoices(List<String> ids) {
//        if (ids == null || ids.isEmpty()) {
//            return null;
//        }
//        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
//        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate);
//        Map<String, Object> params = new HashMap<>();
//        params.put(Jdbc.HttpQuery.HTTP_QUERY_ID, ids);
//        List<Map<String, Object>> choices = named.queryForList("SELECT * FROM " + Jdbc.HTTP_QUERY + " WHERE " + Jdbc.HttpQuery.HTTP_QUERY_ID + " in (:" + Jdbc.HttpQuery.HTTP_QUERY_ID + ")", params);
//        return choices;
//    }
//
//    @Override
//    public List<Option> query(String term, int page) {
//        List<Option> options = new ArrayList<>();
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.HTTP_QUERY + " WHERE LOWER(" + Jdbc.HttpQuery.NAME + ") LIKE LOWER(?) LIMIT " + ((page - 1) * LIMIT) + "," + LIMIT, term + "%");
//        for (Map<String, Object> record : records) {
//            options.add(new Option((String) record.get(Jdbc.HttpQuery.HTTP_QUERY_ID), (String) record.get(Jdbc.HttpQuery.NAME)));
//        }
//        return options;
//    }
//
//    @Override
//    public boolean hasMore(String term, int page) {
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM (SELECT * FROM " + Jdbc.HTTP_QUERY + " WHERE LOWER(" + Jdbc.HttpQuery.NAME + ") LIKE LOWER(?) LIMIT " + (page * LIMIT) + "," + LIMIT + ") pp", int.class, term + "%");
//        return count > 0;
//    }
//
//    @Override
//    public Gson getGson() {
//        return ApplicationUtils.getApplication().getGson();
//    }
//
//    @Override
//    public int size() {
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//        return jdbcTemplate.queryForObject("SELECT count(*) FROM " + Jdbc.HTTP_QUERY, int.class);
//    }
//
//    @Override
//    public Map<String, Object> get(int index) {
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//        return jdbcTemplate.queryForMap("SELECT * from " + Jdbc.HTTP_QUERY + " LIMIT " + index + ",1");
//    }
//
//    @Override
//    public Object getDisplayValue(Map<String, Object> object) {
//        return object.get(Jdbc.HttpQuery.NAME);
//    }
//
//    @Override
//    public String getIdValue(Map<String, Object> object, int index) {
//        return (String) object.get(Jdbc.HttpQuery.HTTP_QUERY_ID);
//    }
//
//    @Override
//    public Map<String, Object> getObject(String id, IModel<? extends List<? extends Map<String, Object>>> choices) {
//        Application application = ApplicationUtils.getApplication();
//        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//        try {
//            return jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.HTTP_QUERY + " WHERE " + Jdbc.HttpQuery.HTTP_QUERY_ID + " = ?", id);
//        } catch (EmptyResultDataAccessException e) {
//            return null;
//        }
//    }
//}
