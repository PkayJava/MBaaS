package com.angkorteam.mbaas.server.logic;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Created by socheat on 5/30/16.
 */
public interface IWebMarkupContainerFactory extends Serializable {

    public WebMarkupContainer createWebMarkupContainer(final String id);

    public WebMarkupContainer createWebMarkupContainer(MarkupContainer container, String id);

    public WebMarkupContainer createWebMarkupContainer(String id, IModel<?> model);

    public WebMarkupContainer createWebMarkupContainer(MarkupContainer container, String id, IModel<?> model);

}
