package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.framework.extension.wicket.markup.html.form.ColorTextField;
import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.model.IModel;

/**
 * Created by socheat on 6/2/16.
 */
public class NashornColorTextField extends ColorTextField {

    private String script;

    public NashornColorTextField(String id, IModel<String> model) {
        super(id, model);
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
