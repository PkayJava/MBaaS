package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornPasswordTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/2/16.
 */
public interface IPasswordTextFieldFactory extends Serializable {

    NashornPasswordTextField createPasswordTextField(String id);

    NashornPasswordTextField createPasswordTextField(MarkupContainer container, String id);

}
