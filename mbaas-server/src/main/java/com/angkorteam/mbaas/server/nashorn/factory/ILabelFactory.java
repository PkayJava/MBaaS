package com.angkorteam.mbaas.server.nashorn.factory;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface ILabelFactory extends Serializable {

    public Label createLabel(String id);

    public Label createLabel(MarkupContainer container, String id);

}
