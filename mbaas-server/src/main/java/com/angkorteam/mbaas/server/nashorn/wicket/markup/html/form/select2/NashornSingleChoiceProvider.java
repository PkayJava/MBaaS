package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.SingleChoiceProvider;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.google.gson.Gson;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 5/31/16.
 */
public class NashornSingleChoiceProvider extends SingleChoiceProvider<Map<String, Object>> {

    private final String applicationCode;

    private String id;

    private String script;

    public NashornSingleChoiceProvider(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    @Override
    public Map<String, Object> toChoice(String id) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);

        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            return (Map<String, Object>) invocable.invokeFunction(this.id + "__toChoice", jdbcTemplate, id);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public List<Option> query(String term, int page) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);

        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            return (List<Option>) invocable.invokeFunction(this.id + "__query", jdbcTemplate, term, page);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean hasMore(String term, int page) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);

        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            return (Boolean) invocable.invokeFunction(this.id + "__hasMore", jdbcTemplate, term, page);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }

}
