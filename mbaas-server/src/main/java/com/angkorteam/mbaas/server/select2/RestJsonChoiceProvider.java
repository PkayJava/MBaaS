package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.SingleChoiceProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.google.gson.Gson;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 8/7/16.
 */
public class RestJsonChoiceProvider extends SingleChoiceProvider<Map<String, Object>> {

    private String applicationCode;

    private String contentType;

    public RestJsonChoiceProvider(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public RestJsonChoiceProvider(String applicationCode, String contentType) {
        this.applicationCode = applicationCode;
        this.contentType = contentType;
    }

    @Override
    public Map<String, Object> toChoice(String id) {
        if (com.google.common.base.Strings.isNullOrEmpty(id)) {
            return null;
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        return jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", id);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        if (this.contentType == null || "".equals(this.contentType)) {
            return options;
        }
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.CONTENT_TYPE + " = ? AND LOWER(" + Jdbc.Json.NAME + ") LIKE LOWER(?) LIMIT " + ((page - 1) * LIMIT) + "," + LIMIT, this.contentType, term + "%");
        for (Map<String, Object> record : records) {
            options.add(new Option((String) record.get(Jdbc.Json.JSON_ID), (String) record.get(Jdbc.Json.NAME)));
        }
        return options;
    }

    @Override
    public boolean hasMore(String term, int page) {
        if (this.contentType == null || "".equals(this.contentType)) {
            return false;
        }
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM (SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.CONTENT_TYPE + " = ? AND LOWER(" + Jdbc.Json.NAME + ") LIKE LOWER(?) LIMIT " + (page * LIMIT) + "," + LIMIT + ") pp", int.class, this.contentType, term + "%");
        return count > 0;
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }
}