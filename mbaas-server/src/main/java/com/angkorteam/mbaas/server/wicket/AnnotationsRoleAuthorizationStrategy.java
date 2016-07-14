package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.page.PagePage;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.jdbc.core.JdbcTemplate;

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
        if (componentClass == PagePage.class) {
            RequestCycle requestCycle = RequestCycle.get();
            String applicationCode = ((Session) Session.get()).getApplicationCode();
            if (applicationCode == null || "".equals(applicationCode)) {
                return false;
            }
            String pageId = requestCycle.getRequest().getQueryParameters().getParameterValue("pageId").toString("");
            if (pageId == null || "".equals(pageId)) {
                pageId = ((Session) Session.get()).getHomePageId();
            }
            if (pageId == null || "".equals(pageId)) {
                return false;
            }
            JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(applicationCode);
            List<String> roles = jdbcTemplate.queryForList("SELECT " + Jdbc.Role.NAME + " FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.ROLE_ID + " IN (SELECT " + Jdbc.PageRole.ROLE_ID + " FROM " + Jdbc.PAGE_ROLE + " WHERE " + Jdbc.PageRole.PAGE_ID + " = ?" + ")", String.class, pageId);
            Roles r = new Roles();
//            if (roles != null && !roles.isEmpty()) {
//                XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
//                roles.remove(configuration.getString(Constants.ROLE_ANONYMOUS));
//            }
            if (roles != null && !roles.isEmpty()) {
                r.addAll(roles);
            }
            return hasAny(r);
        } else {
            return super.isInstantiationAuthorized(componentClass);
        }
    }
}
