package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.IModel;

/**
 * Created by socheat on 6/2/16.
 */
public class NashornNumberTextField<T extends Number & Comparable<T>> extends NumberTextField<T> {

    private String script;

    public NashornNumberTextField(String id, IModel<T> model, Class<T> type) {
        super(id, model, type);
    }

    public void registerValidator(String event) {
        NashornValidator validator = new NashornValidator(getId(), event, this.script);
        super.add(validator);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    protected String[] getInputTypes() {
        return new String[]{"number", "text"};
    }
}
