package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.image.NashornImage;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/12/16.
 */
public interface IImageFactory extends Serializable {

    NashornImage createImage(String id);

    NashornImage createImage(MarkupContainer container, String id);

}
