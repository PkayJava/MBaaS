package com.angkorteam.mbaas.server.renderer;

import com.angkorteam.mbaas.model.entity.tables.pojos.UserPojo;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by socheat on 3/3/16.
 */
public class UserChoiceRenderer implements IChoiceRenderer<UserPojo> {

    @Override
    public Object getDisplayValue(UserPojo object) {
        return object.getLogin();
    }

    @Override
    public String getIdValue(UserPojo object, int index) {
        return object.getUserId();
    }

    @Override
    public UserPojo getObject(String id, IModel<? extends List<? extends UserPojo>> choices) {
        for (UserPojo user : choices.getObject()) {
            if (user.getUserId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}
