package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import com.angkorteam.mbaas.server.function.UserAttributeFunction;
import com.angkorteam.mbaas.server.provider.UserAttributeProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.jooq.types.Interval;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/8/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/user/attribute/management")
public class UserAttributeManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String collectionId;

    @Override
    public String getPageHeader() {
        return "User Attribute Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        this.collectionId = context.select(collectionTable.COLLECTION_ID).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(String.class);

        UserAttributeProvider provider = new UserAttributeProvider(getSession().getUserId());
        provider.selectField(Boolean.class, "system");
        provider.selectField(String.class, "attributeId");
        provider.selectField(String.class, "userPrivacyId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();

        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("attributeType", this), "attributeType", provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup("extra", this), "extra", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("scope", this), "scope", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Permission", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<Void>("refreshLink", UserAttributeManagementPage.class, getPageParameters());
        add(refreshLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
            CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

            String attributeId = (String) object.get("attributeId");
            DSLContext context = getDSLContext();

            AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(attributeId)).fetchOneInto(attributeTable);
            CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);

            JdbcTemplate jdbcTemplate = getJdbcTemplate();
            CollectionAttributeDeleteRequest requestBody = new CollectionAttributeDeleteRequest();
            requestBody.setAttributeName(attributeRecord.getName());
            requestBody.setCollectionName(collectionRecord.getName());
            UserAttributeFunction.deleteAttribute(context, jdbcTemplate, requestBody);
        }
        if ("Permission".equals(link)) {
            String userPrivacyId = (String) object.get("userPrivacyId");
            String attributeId = (String) object.get("attributeId");
            if (userPrivacyId != null && !"".equals(userPrivacyId)) {
                PageParameters parameters = new PageParameters();
                parameters.add("userPrivacyId", userPrivacyId);
                setResponsePage(UserAttributePermissionModifyPage.class, parameters);
            } else {
                PageParameters parameters = new PageParameters();
                parameters.add("attributeId", attributeId);
                setResponsePage(UserAttributePermissionCreatePage.class, parameters);
            }
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            Boolean system = (Boolean) object.get("system");
            if (!system) {
                return true;
            }
        }
        if ("Permission".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            Boolean system = (Boolean) object.get("system");
            if (!system) {
                return true;
            }
        }
        if ("Permission".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Permission".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}
