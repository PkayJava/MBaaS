package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.ValidatorAdapter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornListMultipleChoice extends ListMultipleChoice<Map<String, Object>> {

    public NashornListMultipleChoice(String id, IModel<Collection<Map<String, Object>>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        super(id, model, choices, renderer);
    }

    public NashornListMultipleChoice(String id, IModel<Collection<Map<String, Object>>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows) {
        super(id, model, choices, renderer);
        setMaxRows(maxRows);
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
