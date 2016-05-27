package com.angkorteam.mbaas.server.page.logic;

import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * Created by socheat on 5/26/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/logic/management")
public class LogicManagementPage extends MasterPage {

    @Override
    protected void onInitialize() {
        super.onInitialize();

    }
}
