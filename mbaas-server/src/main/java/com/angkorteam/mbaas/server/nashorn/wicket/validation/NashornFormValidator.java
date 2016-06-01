package com.angkorteam.mbaas.server.nashorn.wicket.validation;

import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornFormValidator implements IFormValidator {

    private String id;

    private String script;

    private Map<String, Object> children;

    public NashornFormValidator() {
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            return (FormComponent<?>[]) invocable.invokeFunction(getId() + "__dependent_form_component", this.children);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    @Override
    public void validate(Form<?> form) {
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
            invocable.invokeFunction(getId() + "__validate", jdbcTemplate, this.children);
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

    public Map<String, Object> getChildren() {
        return children;
    }

    public void setChildren(Map<String, Object> children) {
        this.children = children;
    }
}
