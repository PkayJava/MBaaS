package com.angkorteam.mbaas.server.page;

import com.angkorteam.mbaas.server.wicket.MasterPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation({"administrator", "registered"})
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
