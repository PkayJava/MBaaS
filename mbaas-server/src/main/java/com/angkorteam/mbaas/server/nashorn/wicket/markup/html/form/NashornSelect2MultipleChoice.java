package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornSelect2MultipleChoice extends Select2MultipleChoice<Map<String, Object>> {

    private String script;

    public NashornSelect2MultipleChoice(String id, IModel<List<Map<String, Object>>> object, MultipleChoiceProvider<Map<String, Object>> provider, IChoiceRenderer<Map<String, Object>> renderer) {
        super(id, object, provider, renderer);
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
