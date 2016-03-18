package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.framework.extension.wicket.markup.html.link.Link;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.DesktopTable;
import com.angkorteam.mbaas.model.entity.tables.records.DesktopRecord;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by socheat on 3/10/16.
 */
public abstract class MasterPage extends AdminLTEPage {

    private Label pageHeaderLabel;
    private Label pageDescriptionLabel;

    public MasterPage() {
    }

    public MasterPage(IModel<?> model) {
        super(model);
    }

    public MasterPage(PageParameters parameters) {
        super(parameters);
    }

    public String getPageHeader() {
        return null;
    }

    public String getPageDescription() {
        return null;
    }

    @Override
    protected void onInitialize() {
        Session session = getSession();

        DSLContext context = getDSLContext();
        DesktopTable desktopTable = Tables.DESKTOP.as("desktopTable");

        DesktopRecord desktopRecord = context.select(desktopTable.fields()).from(desktopTable).where(desktopTable.SESSION_ID.eq(session.getId())).fetchOneInto(desktopTable);
        if (desktopRecord != null) {
            desktopRecord.setUserId(session.getUserId());
            desktopRecord.setDateSeen(new Date());
            desktopRecord.setClientIp(getSession().getClientInfo().getProperties().getRemoteAddress());
            desktopRecord.setUserAgent(getSession().getClientInfo().getUserAgent());
            desktopRecord.update();
        }

        super.onInitialize();

        this.pageHeaderLabel = new Label("pageHeaderLabel", new PropertyModel<>(this, "pageHeader"));
        add(this.pageHeaderLabel);
        this.pageDescriptionLabel = new Label("pageDescriptionLabel", new PropertyModel<>(this, "pageDescription"));
        add(this.pageDescriptionLabel);

        Link<Void> logoutLink = new Link<>("logoutLink");
        add(logoutLink);
        logoutLink.setOnClick(this::logoutLinkOnClick);
        logoutLink.setVisible(getSession().isSignedIn());
    }

    private void logoutLinkOnClick(Link link) {
        getSession().invalidateNow();
    }

    @Override
    public Session getSession() {
        return (Session) super.getSession();
    }

    public final DSLContext getDSLContext() {
        Application application = (Application) getApplication();
        return application.getDSLContext();
    }

    public final String getNavigatorLanguage() {
        return getSession().getClientInfo().getProperties().getNavigatorLanguage();
    }

    public final JdbcTemplate getJdbcTemplate() {
        Application application = (Application) getApplication();
        return application.getJdbcTemplate();
    }

    public ServletContext getServletContext() {
        Application application = (Application) getApplication();
        return application.getServletContext();
    }

    public String getHttpAddress() {
        ServletContext servletContext = getServletContext();
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        StringBuffer address = new StringBuffer();
        if (request.isSecure() && request.getServerPort() == 443) {
            address.append("https://").append(request.getServerName()).append(servletContext.getContextPath());
        } else if (!request.isSecure() && request.getServerPort() == 80) {
            address.append("http://").append(request.getServerName()).append(servletContext.getContextPath());
        } else {
            if (request.isSecure()) {
                address.append("https://");
            } else {
                address.append("http://");
            }
            address.append(request.getServerName()).append(":").append(request.getServerPort()).append(servletContext.getContextPath());
        }
        return address.toString();
    }
}
