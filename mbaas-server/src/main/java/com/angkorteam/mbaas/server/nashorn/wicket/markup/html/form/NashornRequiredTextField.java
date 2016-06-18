package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.ValidatorAdapter;

/**
 * Created by socheat on 6/2/16.
 */
public class NashornRequiredTextField<T> extends RequiredTextField<T> {

    private String script;

    public NashornRequiredTextField(String id, IModel<T> model, Class<T> type) {
        super(id, model, type);
    }

    @Override
    public Component add(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            if (behavior instanceof ValidatorAdapter) {
                if (((ValidatorAdapter) behavior).getValidator() instanceof NashornValidator) {
                    throw new WicketRuntimeException("use registerValidator");
                }
            }
        }
        return super.add(behaviors);
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
