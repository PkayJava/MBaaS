package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornForm;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface IFormFactory extends Serializable {

    <T> NashornForm<T> createForm(String id);

    <T> NashornForm<T> createForm(MarkupContainer container, String id);

}
