package com.angkorteam.mbaas.server.page;

import org.apache.wicket.markup.html.border.Border;

/**
 * Created by socheat on 3/1/16.
 */
public class DashboardPage extends MBaaSPage {

    @Override
    public String getPageUUID() {
        return DashboardPage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);
    }
}
