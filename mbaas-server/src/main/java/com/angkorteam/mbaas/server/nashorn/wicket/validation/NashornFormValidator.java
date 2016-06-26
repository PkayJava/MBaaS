package com.angkorteam.mbaas.server.nashorn.wicket.validation;

import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornFormValidator implements IFormValidator {

    private String id;

    private String event;

    private String script;

    private FormComponent<?>[] components;

    private Map<String, FormComponent<?>> map;

    private Map<String, Object> params;

    public NashornFormValidator(String id, String event, String script, FormComponent<?>[] components) {
        this(id, event, script, components, new HashMap<>());
    }

    public NashornFormValidator(String id, String event, String script, FormComponent<?>[] components, Map<String, Object> params) {
        this.id = id;
        this.event = event;
        this.script = script;
        this.components = components;
        this.map = new HashMap<>();
        for (FormComponent<?> component : components) {
            this.map.put(component.getId(), component);
        }
        this.params = params;
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.components;
    }

    @Override
    public void validate(Form<?> form) {
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
            invocable.invokeFunction(this.id + "__" + this.event, jdbcTemplate, this.map, this.params == null ? new HashMap<>() : this.params);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new WicketRuntimeException("function " + this.id + "__" + this.event + "(jdbcTemplate, components, params){} is missing");
        }
    }
}
