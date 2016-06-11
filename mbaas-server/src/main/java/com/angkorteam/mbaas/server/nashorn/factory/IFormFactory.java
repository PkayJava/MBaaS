package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter.NashornFilterForm;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornForm;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.NashornTableProvider;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 5/30/16.
 */
public interface IFormFactory extends Serializable {

    <T> NashornForm<T> createForm(String id);

    <T> NashornForm<T> createForm(MarkupContainer container, String id);

}
