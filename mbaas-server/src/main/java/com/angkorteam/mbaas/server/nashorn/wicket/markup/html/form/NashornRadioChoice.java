package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornRadioChoice extends RadioChoice<Map<String, Object>> {

    private String script;

    public NashornRadioChoice(String id, IModel<Map<String, Object>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        super(id, model, choices, renderer);
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
