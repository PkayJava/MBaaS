package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.ValidatorAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornDropDownChoice extends org.apache.wicket.markup.html.form.DropDownChoice<Map<String, Object>> {

    public NashornDropDownChoice(String id, IModel<Map<String, Object>> model, IModel<? extends List<? extends Map<String, Object>>> choices, IChoiceRenderer<? super Map<String, Object>> renderer) {
        super(id, model, choices, renderer);
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
