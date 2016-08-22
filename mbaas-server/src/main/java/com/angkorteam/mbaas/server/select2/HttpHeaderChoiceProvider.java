package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.google.gson.Gson;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 8/22/16.
 */
public class HttpHeaderChoiceProvider extends MultipleChoiceProvider<Map<String, Object>> {

    private String applicationCode;

    public HttpHeaderChoiceProvider(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    @Override
    public List<Map<String, Object>> toChoices(String[] ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        List<Map<String, Object>> choices = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.HTTP_HEADER + " WHERE " + Jdbc.HttpHeader.HTTP_HEADER_ID + " in (?)", ids);
        return choices;
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.HTTP_HEADER + " WHERE LOWER(" + Jdbc.HttpHeader.NAME + ") LIKE LOWER(?) LIMIT " + ((page - 1) * LIMIT) + "," + LIMIT, term + "%");
        for (Map<String, Object> record : records) {
            options.add(new Option((String) record.get(Jdbc.HttpHeader.HTTP_HEADER_ID), (String) record.get(Jdbc.HttpHeader.NAME)));
        }
        return options;
    }

    @Override
    public boolean hasMore(String term, int page) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM (SELECT * FROM " + Jdbc.HTTP_HEADER + " WHERE LOWER(" + Jdbc.HttpHeader.NAME + ") LIKE LOWER(?) LIMIT " + (page * LIMIT) + "," + LIMIT + ") pp", int.class, term + "%");
        return count > 0;
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }
}
