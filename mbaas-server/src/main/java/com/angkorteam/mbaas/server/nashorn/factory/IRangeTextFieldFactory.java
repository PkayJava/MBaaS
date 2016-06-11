package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornRangeTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/11/16.
 */
public interface IRangeTextFieldFactory extends Serializable {

    <T extends Number & Comparable<T>> NashornRangeTextField<T> createRangeTextField(String id, IModel<T> model, Class<T> type);

    <T extends Number & Comparable<T>> NashornRangeTextField<T> createRangeTextField(MarkupContainer container, String id, IModel<T> model, Class<T> type);

}
