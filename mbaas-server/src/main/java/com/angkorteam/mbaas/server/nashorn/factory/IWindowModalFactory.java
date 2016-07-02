package com.angkorteam.mbaas.server.nashorn.factory;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

/**
 * Created by socheat on 6/30/16.
 */
public interface IWindowModalFactory {

    ModalWindow createModalWindow(String id, String blockCode);

    ModalWindow createModalWindow(MarkupContainer container, String id, String blockCode);

}
