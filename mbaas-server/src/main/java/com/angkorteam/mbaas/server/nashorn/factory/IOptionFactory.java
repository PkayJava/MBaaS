package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;

import java.io.Serializable;

/**
 * Created by socheat on 6/11/16.
 */
public interface IOptionFactory extends Serializable {

    Option createOption(String id, String text);

}
