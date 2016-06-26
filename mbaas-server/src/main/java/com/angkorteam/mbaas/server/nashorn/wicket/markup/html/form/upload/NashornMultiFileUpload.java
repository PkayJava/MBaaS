package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.upload;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.model.IModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 6/12/16.
 */
public class NashornMultiFileUpload extends MultiFileUploadField {

    private String script;

    public NashornMultiFileUpload(String id, IModel<Collection<FileUpload>> model) {
        super(id, model, 1, true);
    }

    public NashornMultiFileUpload(String id, IModel<Collection<FileUpload>> model, int max) {
        super(id, model, max, true);
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
