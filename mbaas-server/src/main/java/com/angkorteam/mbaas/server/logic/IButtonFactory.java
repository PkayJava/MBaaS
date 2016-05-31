package com.angkorteam.mbaas.server.logic;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface IButtonFactory extends Serializable {

    public Button createButton(String id);

    public Button createButton(String id, IModel<String> model);

    public Button createButton(MarkupContainer container, String id);

    public Button createButton(MarkupContainer container, String id, IModel<String> model);
}
