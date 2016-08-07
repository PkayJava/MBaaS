package com.angkorteam.mbaas.server.renderer;

import com.angkorteam.mbaas.server.Jdbc;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/3/16.
 */
public class ClientChoiceRenderer implements IChoiceRenderer<Map<String, Object>> {

    @Override
    public Object getDisplayValue(Map<String, Object> object) {
        return object.get(Jdbc.Client.NAME);
    }

    @Override
    public String getIdValue(Map<String, Object> object, int index) {
        return (String) object.get(Jdbc.Client.CLIENT_ID);
    }

    @Override
    public Map<String, Object> getObject(String id, IModel<? extends List<? extends Map<String, Object>>> choices) {
        for (Map<String, Object> choice : choices.getObject()) {
            if (choice.get(Jdbc.Client.CLIENT_ID).equals(id)) {
                return choice;
            }
        }
        return null;
    }
}
