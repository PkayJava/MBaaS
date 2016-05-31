package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornForm;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface IFormFactory extends Serializable {

    <T> NashornForm<T> createForm(String id);

    <T> NashornForm<T> createForm(MarkupContainer container, String id);

    <T> NashornForm<T> createForm(String id, IModel<T> model);

    <T> NashornForm<T> createForm(MarkupContainer container, String id, IModel<T> model);

}
