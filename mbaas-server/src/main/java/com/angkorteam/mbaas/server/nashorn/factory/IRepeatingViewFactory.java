package com.angkorteam.mbaas.server.nashorn.factory;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;

import java.io.Serializable;

/**
 * Created by socheat on 6/18/16.
 */
public interface IRepeatingViewFactory extends Serializable {

    RepeatingView createRepeatingView(String id);

    RepeatingView createRepeatingView(MarkupContainer container, String id);

}
