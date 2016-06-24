package com.angkorteam.mbaas.server.nashorn.wicket.validation;

import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.UUID;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornValidator<T> implements IValidator<T> {

    private String id;

    private String event;

    private String script;

    public NashornValidator(String id, String event, String script) {
        this.id = id;
        this.event = event;
        this.script = script;
    }

    @Override
    public void validate(IValidatable<T> validatable) {
        Session session = (Session) Session.get();
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(session.getApplicationCode());

        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null && !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
                throw new WicketRuntimeException(e);
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            invocable.invokeFunction(this.id + "__" + this.event, jdbcTemplate, validatable);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new WicketRuntimeException("function " + this.id + "__" + this.event + "(jdbcTemplate, validatable){} is missing");
        }
    }
}
