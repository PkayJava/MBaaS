package com.angkorteam.mbaas.server.nashorn.factory;

import jdk.nashorn.api.scripting.JSObject;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;

import java.io.Serializable;

/**
 * Created by socheat on 7/3/16.
 */
public interface IAjaxTabbedPanelFactory extends Serializable {

    AjaxTabbedPanel<? extends ITab> createAjaxTabbedPanel(String id, JSObject columns);

    AjaxTabbedPanel<? extends ITab> createAjaxTabbedPanel(MarkupContainer container, String id, JSObject columns);

}
