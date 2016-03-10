package com.angkorteam.mbaas.server.page;

import com.angkorteam.mbaas.server.wicket.MasterPage;

/**
 * Created by socheat on 3/1/16.
 */
public class DashboardPage extends MasterPage {

    @Override
    public String getPageHeader() {
        return "Dashboard";
    }

    @Override
    public String getPageDescription() {
        return "Summary Report";
    }

}
