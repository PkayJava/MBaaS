package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.WicketTable;
import com.angkorteam.mbaas.model.entity.tables.records.WicketRecord;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.Date;

/**
 * Created by socheat on 3/1/16.
 */
public class Page extends WebPage {

    public Page() {
    }

    public Page(IModel<?> model) {
        super(model);
    }

    public Page(PageParameters parameters) {
        super(parameters);
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
            wicketRecord.update();
        }

        super.onInitialize();
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

}
