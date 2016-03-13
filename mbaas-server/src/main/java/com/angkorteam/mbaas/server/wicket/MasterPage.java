package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.WicketTable;
import com.angkorteam.mbaas.model.entity.tables.records.WicketRecord;
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
        WicketTable wicketTable = Tables.WICKET.as("wicketTable");

        WicketRecord wicketRecord = context.select(wicketTable.fields()).from(wicketTable).where(wicketTable.SESSION_ID.eq(session.getId())).fetchOneInto(wicketTable);
        if (wicketRecord != null) {
            wicketRecord.setUserId(session.getUserId());
            wicketRecord.setDateSeen(new Date());
            wicketRecord.setClientIp(getSession().getClientInfo().getProperties().getRemoteAddress());
            wicketRecord.setUserAgent(getSession().getClientInfo().getUserAgent());
            wicketRecord.update();
        }

        super.onInitialize();

        this.pageHeaderLabel = new Label("pageHeaderLabel", new PropertyModel<>(this, "pageHeader"));
        add(this.pageHeaderLabel);
        this.pageDescriptionLabel = new Label("pageDescriptionLabel", new PropertyModel<>(this, "pageDescription"));
        add(this.pageDescriptionLabel);
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
