package com.angkorteam.mbaas.server.page.flow;

import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;

/**
 * Created by socheat on 5/28/16.
 */
@Mount("/flow")
public class FlowPage extends MasterPage {

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    public String getVariation() {
        String pageId = getRequest().getQueryParameters().getParameterValue("pageId").toString("");
        return pageId;
    }

}
