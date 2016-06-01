package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornColorTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/1/16.
 */
public interface IColorTextFieldFactory extends Serializable {

    NashornColorTextField createColorTextField(String id, IModel<String> model);

    NashornColorTextField createColorTextField(MarkupContainer container, String id, IModel<String> model);

}
