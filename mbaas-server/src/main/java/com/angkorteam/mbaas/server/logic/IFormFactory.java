package com.angkorteam.mbaas.server.logic;

import com.angkorteam.framework.extension.wicket.html.form.Form;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface IFormFactory extends Serializable {

    public <T> Form<T> createForm(String id);

    public <T> Form<T> createForm(MarkupContainer container, String id);

    public <T> Form<T> createForm(String id, IModel<T> model);

    public <T> Form<T> createForm(MarkupContainer container, String id, IModel<T> model);

}
