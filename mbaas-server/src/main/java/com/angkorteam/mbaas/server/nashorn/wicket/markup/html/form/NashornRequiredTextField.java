package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 6/2/16.
 */
public class NashornRequiredTextField<T> extends RequiredTextField<T> {

    private String script;

    public NashornRequiredTextField(String id, IModel<T> model, Class<T> type) {
        super(id, model, type);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void registerValidator(String event) {
        registerValidator(event, new HashMap<>());
    }

    public void registerValidator(String event, ScriptObjectMirror jsobject) {
        Map<String, Object> params = new HashMap<>();
        if (jsobject != null && !jsobject.isEmpty()) {
            for (Map.Entry<String, Object> param : jsobject.entrySet()) {
                params.put(param.getKey(), param.getValue());
            }
        }
        registerValidator(event, params);
    }

    public void registerValidator(String event, Map<String, Object> params) {
        NashornValidator validator = new NashornValidator(getId(), event, this.script, params);
        super.add(validator);
    }
}
