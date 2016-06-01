package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.framework.extension.wicket.markup.html.form.DateTextField;
import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.ValidatorAdapter;

import java.util.Date;

/**
 * Created by socheat on 6/2/16.
 */
public class NashornDateTextField extends DateTextField {

    public NashornDateTextField(String id, IModel<Date> model) {
        super(id, model);
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
