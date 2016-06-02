package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.UrlTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.ValidatorAdapter;
import org.apache.wicket.validation.validator.UrlValidator;

/**
 * Created by socheat on 6/2/16.
 */
public class NashornUrlTextField extends UrlTextField {

    public NashornUrlTextField(String id, IModel<String> model) {
        super(id, model);
    }

    public NashornUrlTextField(String id, IModel<String> model, UrlValidator urlValidator) {
        super(id, model, urlValidator);
    }

    @Override
    public Component add(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            if (behavior instanceof ValidatorAdapter) {
                if (((ValidatorAdapter) behavior).getValidator() instanceof NashornValidator) {
                    ((NashornValidator) ((ValidatorAdapter) behavior).getValidator()).setId(getId());
                }
            }
        }
        return super.add(behaviors);
    }
}
