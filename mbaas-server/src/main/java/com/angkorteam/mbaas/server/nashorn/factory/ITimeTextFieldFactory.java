package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.form.TimeTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/1/16.
 */
public interface ITimeTextFieldFactory extends Serializable {

    TimeTextField createTimeTextField(String id, IModel<String> model);

    TimeTextField createTimeTextField(MarkupContainer container, String id, IModel<String> model);

}
