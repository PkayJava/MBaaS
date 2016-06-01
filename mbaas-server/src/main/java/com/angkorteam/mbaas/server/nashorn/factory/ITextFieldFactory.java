package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface ITextFieldFactory extends Serializable {

    public <T> NashornTextField<T> createTextField(String id, IModel<T> model);

    public <T> NashornTextField<T> createTextField(MarkupContainer container, String id, IModel<T> model);

    public <T> NashornTextField<T> createTextField(String id, IModel<T> model, Class<T> type);

    public <T> NashornTextField<T> createTextField(MarkupContainer container, String id, IModel<T> model, Class<T> type);

}
