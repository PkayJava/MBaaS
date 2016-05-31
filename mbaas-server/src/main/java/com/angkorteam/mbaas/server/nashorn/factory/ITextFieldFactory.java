package com.angkorteam.mbaas.server.nashorn.factory;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface ITextFieldFactory extends Serializable {

    public <T> TextField<T> createTextField(String id);

    public <T> TextField<T> createTextField(MarkupContainer container, String id);

    public <T> TextField<T> createTextField(String id, Class<T> type);

    public <T> TextField<T> createTextField(MarkupContainer container, String id, Class<T> type);

    public <T> TextField<T> createTextField(String id, IModel<T> model);

    public <T> TextField<T> createTextField(MarkupContainer container, String id, IModel<T> model);

    public <T> TextField<T> createTextField(String id, IModel<T> model, Class<T> type);

    public <T> TextField<T> createTextField(MarkupContainer container, String id, IModel<T> model, Class<T> type);

}
