package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.SingleChoiceProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.google.gson.Gson;
import org.apache.wicket.model.IModel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 7/5/16.
 */
public class PageChoiceProvider extends SingleChoiceProvider<Map<String, Object>> {

    private String applicationCode;

    public PageChoiceProvider(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    @Override
    public Map<String, Object> toChoice(String id) {
        if (com.google.common.base.Strings.isNullOrEmpty(id)) {
            return null;
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        return jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", id);
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.PAGE + " WHERE LOWER(" + Jdbc.Page.CODE + ") LIKE LOWER(?) LIMIT " + ((page - 1) * LIMIT) + "," + LIMIT, term + "%");
        for (Map<String, Object> record : records) {
            options.add(new Option((String) record.get(Jdbc.Page.PAGE_ID), (String) record.get(Jdbc.Page.CODE)));
        }
        return options;
    }

    @Override
    public boolean hasMore(String term, int page) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM (SELECT * FROM " + Jdbc.PAGE + " WHERE LOWER(" + Jdbc.Page.CODE + ") LIKE LOWER(?) LIMIT " + (page * LIMIT) + "," + LIMIT + ") pp", int.class, term + "%");
        return count > 0;
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }

    @Override
    public int size() {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        return jdbcTemplate.queryForObject("SELECT count(*) FROM " + Jdbc.PAGE, int.class);
    }

    @Override
    public Map<String, Object> get(int index) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
        return jdbcTemplate.queryForMap("SELECT * from " + Jdbc.PAGE + " LIMIT " + index + ",1");
    }

    @Override
    public Object getDisplayValue(Map<String, Object> object) {
        return object.get(Jdbc.Page.CODE);
    }

    @Override
    public String getIdValue(Map<String, Object> object, int index) {
        return (String) object.get(Jdbc.Page.PAGE_ID);
    }

    @Override
    public Map<String, Object> getObject(String id, IModel<? extends List<? extends Map<String, Object>>> choices) {
        for (Map<String, Object> choice : choices.getObject()) {
            if (choice.get(Jdbc.Page.PAGE_ID).equals(id)) {
                return choice;
            }
        }
        return null;
    }
}
