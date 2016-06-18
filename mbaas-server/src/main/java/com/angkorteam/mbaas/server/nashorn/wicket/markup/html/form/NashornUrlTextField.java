package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.UrlTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.ValidatorAdapter;
import org.apache.wicket.validation.validator.UrlValidator;

/**
 * Created by socheat on 6/2/16.
 */
public class NashornUrlTextField extends UrlTextField {

    private String script;

    public NashornUrlTextField(String id, IModel<String> model) {
        super(id, model);
    }

    public NashornUrlTextField(String id, IModel<String> model, UrlValidator urlValidator) {
        super(id, model, urlValidator);
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

    @Override
    protected String[] getInputTypes() {
        return new String[]{"url", "text"};
    }
}
