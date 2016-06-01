package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;

/**
 * Created by socheat on 5/31/16.
 */
public class NashornForm<T> extends org.apache.wicket.markup.html.form.Form<T> {

    private String script;
    private Map<String, Object> userModel;

    public NashornForm(String id) {
        super(id);
    }

    public NashornForm(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected final void onSubmit() {
        Session session = (Session) Session.get();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());

        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            invocable.invokeFunction(getId() + "__on_submit", RequestCycle.get(), jdbcTemplate, this, this.userModel);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        }
    }

    @Override
    protected final void onError() {
        Session session = (Session) Session.get();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());

        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            invocable.invokeFunction(getId() + "__on_error", RequestCycle.get(), jdbcTemplate, this, this.userModel);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        }
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Map<String, Object> getUserModel() {
        return userModel;
    }

    public void setUserModel(Map<String, Object> userModel) {
        this.userModel = userModel;
    }
}
