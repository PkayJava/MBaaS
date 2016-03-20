package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * Created by socheat on 3/20/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/collection/user/privacy/management")
public class CollectionUserPrivacyManagementPage extends MasterPage {
}
