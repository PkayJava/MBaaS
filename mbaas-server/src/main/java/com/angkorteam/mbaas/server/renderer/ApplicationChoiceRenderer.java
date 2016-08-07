package com.angkorteam.mbaas.server.renderer;

import com.angkorteam.mbaas.model.entity.tables.pojos.ApplicationPojo;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by socheat on 3/3/16.
 */
public class ApplicationChoiceRenderer implements IChoiceRenderer<ApplicationPojo> {

    @Override
    public Object getDisplayValue(ApplicationPojo object) {
        return object.getName();
    }

    @Override
    public String getIdValue(ApplicationPojo object, int index) {
        return object.getApplicationId();
    }

    @Override
    public ApplicationPojo getObject(String id, IModel<? extends List<? extends ApplicationPojo>> choices) {
        for (ApplicationPojo choice : choices.getObject()) {
            if (choice.getApplicationId().equals(id)) {
                return choice;
            }
        }
        return null;
    }
}
