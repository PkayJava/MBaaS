package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornNumberTextField;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;

/**
 * Created by socheat on 6/11/16.
 */
public interface INumberTextFieldFactory extends Serializable {

    <T extends Number & Comparable<T>> NashornNumberTextField<T> createNumberTextField(String id, Class<T> type);

    <T extends Number & Comparable<T>> NashornNumberTextField<T> createNumberTextField(MarkupContainer container, String id, Class<T> type);

}
