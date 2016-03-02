package com.angkorteam.mbaas.server.renderer;

import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by socheat on 3/3/16.
 */
public class RoleChoiceRenderer implements IChoiceRenderer<RolePojo> {

    @Override
    public Object getDisplayValue(RolePojo object) {
        return object.getName();
    }

    @Override
    public String getIdValue(RolePojo object, int index) {
        return object.getRoleId();
    }

    @Override
    public RolePojo getObject(String id, IModel<? extends List<? extends RolePojo>> choices) {
        for (RolePojo role : choices.getObject()) {
            if (role.getRoleId().equals(id)) {
                return role;
            }
        }
        return null;
    }
}
