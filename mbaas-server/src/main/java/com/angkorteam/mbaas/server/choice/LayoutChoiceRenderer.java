package com.angkorteam.mbaas.server.choice;

import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by socheat on 10/27/16.
 */
public class LayoutChoiceRenderer implements IChoiceRenderer<LayoutPojo> {

    @Override
    public Object getDisplayValue(LayoutPojo object) {
        return object.getTitle();
    }

    @Override
    public String getIdValue(LayoutPojo object, int index) {
        return object.getLayoutId();
    }

    @Override
    public LayoutPojo getObject(String id, IModel<? extends List<? extends LayoutPojo>> choices) {
        for (LayoutPojo layout : choices.getObject()) {
            if (layout.getLayoutId().equals(id)) {
                return layout;
            }
        }
        return null;
    }
}
