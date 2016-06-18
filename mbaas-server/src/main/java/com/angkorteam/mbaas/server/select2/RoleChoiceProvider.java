package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 5/25/16.
 */
public class RoleChoiceProvider extends MultipleChoiceProvider<Map<String, Object>> {

    private final String applicationCode;

    public RoleChoiceProvider(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        List<Map<String, Object>> roleRecords = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ROLE + " WHERE LOWER(" + Jdbc.Role.NAME + ") LIKE LOWER(?) LIMIT " + ((page - 1) * LIMIT) + "," + LIMIT, term + "%");
        for (Map<String, Object> roleRecord : roleRecords) {
            options.add(new Option((String) roleRecord.get(Jdbc.Role.ROLE_ID), (String) roleRecord.get(Jdbc.Role.NAME)));
        }
        return options;
    }

    @Override
    public List<Map<String, Object>> toChoices(String[] ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        return jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.ROLE_ID + " IN (" + StringUtils.repeat("?", ", ", ids.length) + ")", (Object[]) ids);
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }

    @Override
    public boolean hasMore(String term, int page) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM (SELECT * FROM " + Jdbc.ROLE + " WHERE LOWER(" + Jdbc.Role.NAME + ") LIKE LOWER(?) LIMIT " + (page * LIMIT) + "," + LIMIT + ") pp", int.class, term + "%");
        return count > 0;
    }
}
