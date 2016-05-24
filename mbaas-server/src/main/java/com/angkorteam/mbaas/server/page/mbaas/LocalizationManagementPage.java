package com.angkorteam.mbaas.server.page.mbaas;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.LocalizationTable;
import com.angkorteam.mbaas.server.provider.LocalizationProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MBaaSPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/13/16.
 */
@AuthorizeInstantiation("mbaas.system")
@Mount("/mbaas/localization/management")
public class LocalizationManagementPage extends MBaaSPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Localization Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        LocalizationProvider provider = new LocalizationProvider();
        provider.selectField(Boolean.class, "localizationId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("key", this), "key", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("page", this), "page", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("language", this), "language", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("label", this), "label", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", LocalizationManagementPage.class, getPageParameters());
        add(refreshLink);
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        return "";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            String localizationId = (String) object.get("localizationId");
            PageParameters parameters = new PageParameters();
            parameters.add("localizationId", localizationId);
            setResponsePage(LocalizationModifyPage.class, parameters);
            return;
        }
        if ("Delete".equals(link)) {
            String localizationId = (String) object.get("localizationId");
            DSLContext context = getDSLContext();
            LocalizationTable localizationTable = Tables.LOCALIZATION.as("localizationTable");
            context.delete(localizationTable).where(localizationTable.LOCALIZATION_ID.eq(localizationId)).execute();
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }

}
