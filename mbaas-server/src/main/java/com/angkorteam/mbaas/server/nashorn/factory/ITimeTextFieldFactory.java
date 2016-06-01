package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.form.TimeTextField;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornTimeTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/1/16.
 */
public interface ITimeTextFieldFactory extends Serializable {

    NashornTimeTextField createTimeTextField(String id, IModel<String> model);

    NashornTimeTextField createTimeTextField(MarkupContainer container, String id, IModel<String> model);

}
