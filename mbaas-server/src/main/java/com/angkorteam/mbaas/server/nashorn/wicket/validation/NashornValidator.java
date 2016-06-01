package com.angkorteam.mbaas.server.nashorn.wicket.validation;

import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornValidator<T> implements IValidator<T> {

    private String id;

    private String script;

    public NashornValidator() {
    }

    @Override
    public void validate(IValidatable<T> validatable) {
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
            invocable.invokeFunction(this.id + "__validate", jdbcTemplate, validatable.getValue());
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
