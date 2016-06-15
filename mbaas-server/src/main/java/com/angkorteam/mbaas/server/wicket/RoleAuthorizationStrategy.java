package com.angkorteam.mbaas.server.wicket;

import org.apache.wicket.authorization.strategies.CompoundAuthorizationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;

/**
 * Created by socheat on 6/15/16.
 */
public class RoleAuthorizationStrategy extends CompoundAuthorizationStrategy {

    public RoleAuthorizationStrategy(final IRoleCheckingStrategy roleCheckingStrategy) {
        add(new AnnotationsRoleAuthorizationStrategy(roleCheckingStrategy));
        add(new MetaDataRoleAuthorizationStrategy(roleCheckingStrategy));
    }

}
