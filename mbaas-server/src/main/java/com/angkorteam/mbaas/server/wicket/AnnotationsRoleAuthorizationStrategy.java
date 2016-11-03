package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.PageRoleTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.CmsPage;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.cycle.RequestCycle;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 6/16/16.
 */
public class AnnotationsRoleAuthorizationStrategy extends org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy {

    public AnnotationsRoleAuthorizationStrategy(IRoleCheckingStrategy roleCheckingStrategy) {
        super(roleCheckingStrategy);
    }

    @Override
    public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {
        if (componentClass == CmsPage.class) {
            RequestCycle requestCycle = RequestCycle.get();
            String pageId = requestCycle.getRequest().getQueryParameters().getParameterValue("pageId").toString("");
            PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
            RoleTable roleTable = Tables.ROLE.as("roleTable");
            DSLContext context = Spring.getBean(DSLContext.class);
            List<String> roles = context.select(roleTable.NAME).from(roleTable).innerJoin(pageRoleTable).on(roleTable.ROLE_ID.eq(pageRoleTable.ROLE_ID)).where(pageRoleTable.PAGE_ID.eq(pageId)).fetchInto(String.class);
            Roles r = new Roles();
            if (roles != null && !roles.isEmpty()) {
                r.addAll(roles);
            }
            return hasAny(r);
        } else {
            return super.isInstantiationAuthorized(componentClass);
        }
    }
}
