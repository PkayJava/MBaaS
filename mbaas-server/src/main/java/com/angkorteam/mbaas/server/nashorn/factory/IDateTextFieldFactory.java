package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornDateTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by socheat on 6/1/16.
 */
public interface IDateTextFieldFactory extends Serializable {

    NashornDateTextField createDateTextField(String id, IModel<Date> model);

    NashornDateTextField createDateTextField(MarkupContainer container, String id, IModel<Date> model);

}
