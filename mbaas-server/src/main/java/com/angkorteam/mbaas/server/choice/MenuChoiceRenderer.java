package com.angkorteam.mbaas.server.choice;

import com.angkorteam.mbaas.model.entity.tables.pojos.MenuPojo;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by socheat on 10/27/16.
 */
public class MenuChoiceRenderer implements IChoiceRenderer<MenuPojo> {

    @Override
    public Object getDisplayValue(MenuPojo object) {
        return object.getPath();
    }

    @Override
    public String getIdValue(MenuPojo object, int index) {
        return object.getMenuId();
    }

    @Override
    public MenuPojo getObject(String id, IModel<? extends List<? extends MenuPojo>> choices) {
        for (MenuPojo menu : choices.getObject()) {
            if (menu.getMenuId().equals(id)) {
                return menu;
            }
        }
        return null;
    }
}
