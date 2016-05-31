package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornButton;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface IButtonFactory extends Serializable {

    NashornButton createButton(String id);

    NashornButton createButton(String id, IModel<String> model);

    NashornButton createButton(MarkupContainer container, String id);

    NashornButton createButton(MarkupContainer container, String id, IModel<String> model);
}
