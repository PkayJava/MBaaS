package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.form.DateTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by socheat on 6/1/16.
 */
public interface IDateTextFieldFactory extends Serializable {

    DateTextField createDateTextField(String id, IModel<Date> model);

    DateTextField createDateTextField(MarkupContainer container, String id, IModel<Date> model);

}
