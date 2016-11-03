package com.angkorteam.mbaas.server.choice;

import com.angkorteam.mbaas.model.entity.tables.pojos.SectionPojo;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by socheat on 10/27/16.
 */
public class SectionChoiceRenderer implements IChoiceRenderer<SectionPojo> {

    @Override
    public Object getDisplayValue(SectionPojo object) {
        return object.getTitle();
    }

    @Override
    public String getIdValue(SectionPojo object, int index) {
        return object.getSectionId();
    }

    @Override
    public SectionPojo getObject(String id, IModel<? extends List<? extends SectionPojo>> choices) {
        for (SectionPojo section : choices.getObject()) {
            if (section.getSectionId().equals(id)) {
                return section;
            }
        }
        return null;
    }
}
