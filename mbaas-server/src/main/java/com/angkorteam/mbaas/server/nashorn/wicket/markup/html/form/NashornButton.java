package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.model.IModel;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;

/**
 * Created by socheat on 5/31/16.
 */
public class NashornButton extends org.apache.wicket.markup.html.form.Button {

    private Map<String, Object> userModel;

    private String script;

    public NashornButton(String id) {
        super(id);
    }

    public NashornButton(String id, IModel<String> model) {
        super(id, model);
    }


    public Map<String, Object> getUserModel() {
        return userModel;
    }

    public void setUserModel(Map<String, Object> userModel) {
        this.userModel = userModel;
    }

    @Override
    public final void onSubmit() {
        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            invocable.invokeFunction(getId() + "__onSubmit", this, this.userModel);
        } catch (ScriptException e) {
        } catch (NoSuchMethodException e) {
        }
    }

    @Override
    public final void onError() {
        ScriptEngine scriptEngine = ApplicationUtils.getApplication().getScriptEngine();
        if (this.script != null || !"".equals(this.script)) {
            try {
                scriptEngine.eval(this.script);
            } catch (ScriptException e) {
            }
        }
        Invocable invocable = (Invocable) scriptEngine;
        try {
            invocable.invokeFunction(getId() + "__onError", this, this.userModel);
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
}
