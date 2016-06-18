package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.upload;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.model.IModel;

import java.util.Collection;

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

    public void registerValidator(String event) {
        NashornValidator validator = new NashornValidator(getId(), event, this.script);
        super.add(validator);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
