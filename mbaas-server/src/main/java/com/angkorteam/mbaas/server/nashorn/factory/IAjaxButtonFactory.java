package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.ajax.markup.html.form.NashornAjaxButton;
import org.apache.wicket.MarkupContainer;

/**
 * Created by socheat on 6/30/16.
 */
public interface IAjaxButtonFactory {

    NashornAjaxButton createAjaxButton(String id);

    NashornAjaxButton createAjaxButton(MarkupContainer container, String id);
    
}
