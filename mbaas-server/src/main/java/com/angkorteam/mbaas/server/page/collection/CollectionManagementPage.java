package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.plain.request.collection.CollectionDeleteRequest;
import com.angkorteam.mbaas.server.function.CollectionFunction;
import com.angkorteam.mbaas.server.page.attribute.AttributeManagementPage;
import com.angkorteam.mbaas.server.page.document.DocumentManagementPage;
import com.angkorteam.mbaas.server.provider.CollectionProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/collection/management")
public class CollectionManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Collection Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CollectionProvider provider = new CollectionProvider();
        provider.selectField(String.class, "collectionId");
        provider.selectField(Boolean.class, "system");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup("document", this), "document", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("owner", this), "owner", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Role Privacy", "User Privacy", "Attribute", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<Void>("refreshLink", CollectionManagementPage.class, getPageParameters());
        add(refreshLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("name".equals(link)) {
            String collectionId = (String) object.get("collectionId");
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", collectionId);
            setResponsePage(DocumentManagementPage.class, parameters);
        }
        if ("Delete".equals(link)) {
            DSLContext context = getDSLContext();
            JdbcTemplate jdbcTemplate = getJdbcTemplate();
            CollectionDeleteRequest requestBody = new CollectionDeleteRequest();
            requestBody.setCollectionName((String) object.get("name"));
            CollectionFunction.deleteCollection(context, jdbcTemplate, requestBody);
            setResponsePage(CollectionManagementPage.class);
        }
        if ("Attribute".equals(link)) {
            String collectionId = (String) object.get("collectionId");
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", collectionId);
            setResponsePage(AttributeManagementPage.class, parameters);
        }
        if ("Role Privacy".equals(link)) {
            String collectionId = (String) object.get("collectionId");
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", collectionId);
            setResponsePage(CollectionRolePrivacyManagementPage.class, parameters);
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        Boolean system = (Boolean) object.get("system");
        if ("name".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        if ("Attribute".equals(link)) {
            return true;
        }
        if ("Role Privacy".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("name".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        if ("Attribute".equals(link)) {
            return true;
        }
        if ("Role Privacy".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Attribute".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Role Privacy".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}
