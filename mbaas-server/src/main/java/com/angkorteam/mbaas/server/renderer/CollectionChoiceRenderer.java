package com.angkorteam.mbaas.server.renderer;

import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by socheat on 3/3/16.
 */
public class CollectionChoiceRenderer implements IChoiceRenderer<CollectionPojo> {

    @Override
    public Object getDisplayValue(CollectionPojo object) {
        return object.getName();
    }

    @Override
    public String getIdValue(CollectionPojo object, int index) {
        return object.getCollectionId();
    }

    @Override
    public CollectionPojo getObject(String id, IModel<? extends List<? extends CollectionPojo>> choices) {
        for (CollectionPojo collection : choices.getObject()) {
            if (collection.getCollectionId().equals(id)) {
                return collection;
            }
        }
        return null;
    }
}
