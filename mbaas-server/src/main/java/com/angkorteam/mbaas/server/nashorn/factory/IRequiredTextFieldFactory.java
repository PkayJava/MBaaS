package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornRequiredTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/11/16.
 */
public interface IRequiredTextFieldFactory extends Serializable {

    <T> NashornRequiredTextField<T> createRequiredTextField(String id, IModel<T> model, Class<T> type);

    <T> NashornRequiredTextField<T> createRequiredTextField(MarkupContainer container, String id, IModel<T> model, Class<T> type);

}
