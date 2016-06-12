package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.image;

import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 6/12/16.
 */
public class NashornImage extends ExternalImage {

    public NashornImage(String id, IModel<Serializable> model) {
        super(id, model);
    }

}
