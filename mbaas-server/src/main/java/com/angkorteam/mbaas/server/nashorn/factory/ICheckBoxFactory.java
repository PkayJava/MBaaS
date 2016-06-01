package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornCheckBox;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/1/16.
 * <input type="checkbox">
 */
public interface ICheckBoxFactory extends Serializable {

    NashornCheckBox createCheckBox(String id, IModel<Boolean> model);

    NashornCheckBox createCheckBox(MarkupContainer container, String id, IModel<Boolean> model);

}
