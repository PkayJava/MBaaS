package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornHiddenField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/11/16.
 */
public interface IHiddenFieldFactory extends Serializable {

    <T> NashornHiddenField<T> createHiddenField(String id, IModel<T> model, Class<T> type);

    <T> NashornHiddenField<T> createHiddenField(MarkupContainer container, String id, IModel<T> model, Class<T> type);

}
