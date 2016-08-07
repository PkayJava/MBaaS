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
public class JsonChoiceProvider extends SingleChoiceProvider<Map<String, Object>> {

    private String applicationCode;

    public JsonChoiceProvider(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    @Override
    public Map<String, Object> toChoice(String id) {
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        return jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.JSON_ID + " = ?", id);
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        List<Map<String, Object>> jsonRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.JSON + " WHERE LOWER(" + Jdbc.Json.NAME + ") LIKE LOWER(?) LIMIT " + ((page - 1) * LIMIT) + "," + LIMIT, term + "%");
        for (Map<String, Object> jsonRecord : jsonRecords) {
            options.add(new Option((String) jsonRecord.get(Jdbc.Json.JSON_ID), (String) jsonRecord.get(Jdbc.Json.NAME)));
        }
        return options;
    }

    @Override
    public boolean hasMore(String term, int page) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM (SELECT * FROM " + Jdbc.JSON + " WHERE LOWER(" + Jdbc.Json.NAME + ") LIKE LOWER(?) LIMIT " + (page * LIMIT) + "," + LIMIT + ") pp", int.class, term + "%");
        return count > 0;
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }
}