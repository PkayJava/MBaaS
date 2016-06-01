package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.form.ColorTextField;
import com.angkorteam.framework.extension.wicket.markup.html.form.DateTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by socheat on 6/1/16.
 */
public interface IColorTextFieldFactory extends Serializable {

    ColorTextField createColorTextField(String id, IModel<String> model);

    ColorTextField createColorTextField(MarkupContainer container, String id, IModel<String> model);

}
