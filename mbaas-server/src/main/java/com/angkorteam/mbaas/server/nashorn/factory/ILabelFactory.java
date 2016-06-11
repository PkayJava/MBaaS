package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.basic.NashornLabel;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface ILabelFactory extends Serializable {

    public NashornLabel createLabel(String id);

    public NashornLabel createLabel(MarkupContainer container, String id);

}
