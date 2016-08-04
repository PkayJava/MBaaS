package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * Created by socheat on 8/3/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/rest/management")
public class RestManagementPage extends MasterPage {
}
