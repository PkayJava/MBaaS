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
 * Created by socheat on 5/25/16.
 */
public class MasterPageChoiceProvider extends SingleChoiceProvider<Map<String, Object>> {

    private final String applicationCode;

    public MasterPageChoiceProvider(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.MASTER_PAGE + " WHERE LOWER(" + Jdbc.MasterPage.CODE + ")  LIKE LOWER(?) ORDER BY " + Jdbc.MasterPage.DATE_CREATED + " ASC LIMIT " + ((page - 1) * LIMIT) + "," + LIMIT, term + "%");
        for (Map<String, Object> record : records) {
            options.add(new Option((String) record.get(Jdbc.MasterPage.MASTER_PAGE_ID), (String) record.get(Jdbc.MasterPage.CODE)));
        }
        return options;
    }

    @Override
    public Map<String, Object> toChoice(String id) {
        if (com.google.common.base.Strings.isNullOrEmpty(id)) {
            return null;
        }
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        return jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MASTER_PAGE + " WHERE " + Jdbc.MasterPage.MASTER_PAGE_ID + " = ?", id);
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }

    @Override
    public boolean hasMore(String term, int page) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM (SELECT * FROM " + Jdbc.MASTER_PAGE + " WHERE LOWER(" + Jdbc.MasterPage.CODE + ") LIKE LOWER(?) ORDER BY " + Jdbc.MasterPage.DATE_CREATED + " ASC LIMIT " + (page * LIMIT) + "," + LIMIT + ") pp", int.class, term + "%");
        return count > 0;
    }
}
