package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.tabs;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel.BlockPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.MapModel;

import java.util.Map;

/**
 * Created by socheat on 7/3/16.
 */
public class NashornTab extends AbstractTab {

    private String blockCode;

    private Map<String, Object> pageModel;

    private boolean state;

    private Map<String, Object> blockModel;

    public NashornTab(IModel<String> title, String blockCode, boolean stage, Map<String, Object> pageModel, Map<String, Object> blockModel) {
        super(title);
        this.blockCode = blockCode;
        this.state = stage;
        this.pageModel = pageModel;
        this.blockModel = blockModel;
    }

    @Override
    public WebMarkupContainer getPanel(String panelId) {
        return new BlockPanel(panelId, this.blockCode, this.state, this.pageModel, new MapModel<>(this.blockModel));
    }
}
