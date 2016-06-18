package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

/**
 * Created by socheat on 6/18/16.
 */
public class CheckBoxPanel extends Panel implements IMarkupResourceStreamProvider {

    public CheckBoxPanel(String id) {
        super(id);
        add(new CheckBox("checkbox"));
    }

    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return new StringResourceStream("<wicket:panel><input wicket:id=\"checkbox\" type=\"checkbox\"/></wicket:panel>");
    }
}
