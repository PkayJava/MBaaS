package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.*;
import com.angkorteam.mbaas.plain.enums.UserStatusEnum;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.server.provider.UserProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/user/management")
public class UserManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = context.select(collectionTable.fields())
                .from(collectionTable)
                .where(collectionTable.NAME.eq(Tables.USER.getName()))
                .fetchOneInto(collectionTable);
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        List<AttributeRecord> attributeRecords = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .fetchInto(attributeTable);

        Map<String, AttributeRecord> virtualAttributeRecords = new HashMap<>();
        for (AttributeRecord attributeRecord : attributeRecords) {
            virtualAttributeRecords.put(attributeRecord.getAttributeId(), attributeRecord);
        }

        UserProvider provider = new UserProvider();
        provider.selectField(String.class, "userId");
        provider.selectField(Boolean.class, "system");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("login", this), "login", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("roleName", this), "roleName", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("status", this), "status", provider));
        for (AttributeRecord attributeRecord : attributeRecords) {
            if (attributeRecord.getSystem()) {
                continue;
            }
            if (AttributeTypeEnum.Boolean.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.Byte.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Byte.class, JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.Short.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Short.class, JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.Integer.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.Long.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Long.class, JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.Float.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Float.class, JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.Double.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Double.class, JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.Character.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Character.class, JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.String.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.Time.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TimeFilteredJooqColumn(JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.Date.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new DateFilteredJooqColumn(JooqUtils.lookup(column, this), column, provider));
            } else if (AttributeTypeEnum.DateTime.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup(column, this), column, provider));
            }
        }
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Change PWD", "Suspend", "Activate"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<Void>("refreshLink", UserManagementPage.class, getPageParameters());
        add(refreshLink);
    }

    @Override
    public String getPageHeader() {
        return "User Management";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        DSLContext context = getDSLContext();
        UserTable userTable = Tables.USER.as("userTable");
        String userId = (String) object.get("userId");
        if ("Suspend".equals(link)) {
            context.update(userTable).set(userTable.STATUS, UserStatusEnum.Suspended.getLiteral()).where(userTable.USER_ID.eq(userId)).execute();
            return;
        }
        if ("Activate".equals(link)) {
            context.update(userTable).set(userTable.STATUS, UserStatusEnum.Active.getLiteral()).where(userTable.USER_ID.eq(userId)).execute();
            return;
        }
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("userId", userId);
            setResponsePage(UserModifyPage.class, parameters);
            return;
        }
        if ("Change PWD".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("userId", userId);
            setResponsePage(UserPasswordModifyPage.class, parameters);
            return;
        }
        if ("login".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("userId", userId);
            setResponsePage(UserModifyPage.class, parameters);
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        Boolean system = (Boolean) object.get("system");
        if ("login".equals(link)) {
            return !system;
        }
        if ("Edit".equals(link)) {
            return !system;
        }
        if ("Change PWD".equals(link)) {
            return true;
        }
        if ("Suspend".equals(link)) {
            return !system;
        }
        if ("Activate".equals(link)) {
            return !system;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        String status = (String) object.get("status");
        Boolean system = (Boolean) object.get("system");
        if ("Suspend".equals(link)) {
            if (system) {
                return false;
            }
            if (UserStatusEnum.Suspended.getLiteral().equals(status)) {
                return false;
            }
        }
        if ("Activate".equals(link)) {
            if (system) {
                return false;
            }
            if (UserStatusEnum.Active.getLiteral().equals(status)) {
                return false;
            }
        }
        if ("Edit".equals(link)) {
            if (system) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Suspend".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Activate".equals(link)) {
            return "btn-xs btn-warning";
        }
        if ("Change PWD".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}
