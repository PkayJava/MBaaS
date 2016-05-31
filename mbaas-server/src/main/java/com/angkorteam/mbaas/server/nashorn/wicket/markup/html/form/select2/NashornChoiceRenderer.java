package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.select2;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornChoiceRenderer implements IChoiceRenderer<Map<String, Object>> {

    private String id;

    private String text;

    public NashornChoiceRenderer(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Object getDisplayValue(Map<String, Object> object) {
        return object.get(this.text);
    }

    @Override
    public String getIdValue(Map<String, Object> object, int index) {
        return String.valueOf(object.get(this.id));
    }

    @Override
    public Map<String, Object> getObject(String id, IModel<? extends List<? extends Map<String, Object>>> choices) {
        if (id == null || "".equals(id)) {
            return null;
        }
        for (Map<String, Object> choice : choices.getObject()) {
            if (id.equals(getIdValue(choice, 0))) {
                return choice;
            }
        }
        return null;
    }
}
