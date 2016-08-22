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
public class MenuChoiceProvider extends SingleChoiceProvider<Map<String, Object>> {

    private final String applicationCode;

    public MenuChoiceProvider(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.PARENT_MENU_ID + " IS NOT NULL AND LOWER(" + Jdbc.Menu.TITLE + ") LIKE LOWER(?) ORDER BY " + Jdbc.Page.DATE_CREATED + " ASC LIMIT " + ((page - 1) * LIMIT) + "," + LIMIT, term + "%");
        for (Map<String, Object> record : records) {
            options.add(new Option((String) record.get(Jdbc.Menu.MENU_ID), (String) record.get(Jdbc.Menu.TITLE)));
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
        return jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.MENU_ID + " = ?", id);
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }

    @Override
    public boolean hasMore(String term, int page) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM (SELECT * FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.PARENT_MENU_ID + " IS NOT NULL AND LOWER(" + Jdbc.Menu.TITLE + ") LIKE LOWER(?) ORDER BY " + Jdbc.Page.DATE_CREATED + " ASC LIMIT " + (page * LIMIT) + "," + LIMIT + ") pp", int.class, term + "%");
        return count > 0;
    }
}
