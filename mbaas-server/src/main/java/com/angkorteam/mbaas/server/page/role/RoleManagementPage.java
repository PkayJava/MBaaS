package com.angkorteam.mbaas.server.page.role;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.server.provider.*;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
@Mount("/role/management")
public class RoleManagementPage extends Page implements ActionFilteredJooqColumn.Event<RoleItemModel> {

    public RoleManagementPage() {
    }

    public RoleManagementPage(IModel<?> model) {
        super(model);
    }

    public RoleManagementPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        RoleProvider provider = new RoleProvider();
        provider.selectField("roleId", provider.getRoleId());
        provider.selectField("system", provider.getSystem());

        provider.setGroupBy(provider.getRoleId());

        FilterForm<RoleFilterModel> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<RoleItemModel, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn<>(String.class, JooqUtils.lookup("name", this), RoleFilterModel.class, "name", provider, provider.getName()));
        columns.add(new TextFilteredJooqColumn<>(String.class, JooqUtils.lookup("description", this), RoleFilterModel.class, "description", provider, provider.getDescription()));
        columns.add(new ActionFilteredJooqColumn<>(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Delete"));

        DataTable<RoleItemModel, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);
    }

    @Override
    public void onClickEventLink(String link, RoleItemModel roleItemModel) {
        if ("Edit".equals(link)) {
            String roleId = roleItemModel.getRoleId();
            PageParameters parameters = new PageParameters();
            parameters.add("id", roleId);
            setResponsePage(RoleModifyPage.class, parameters);
        } else if ("Delete".equals(link)) {
            DSLContext context = getDSLContext();
            RoleTable roleTable = Tables.ROLE.as("roleTable");
            context.delete(roleTable).where(roleTable.ROLE_ID.eq(roleItemModel.getRoleId())).execute();
        }
    }

    @Override
    public boolean isVisibleEventLink(String link, RoleItemModel roleItemModel) {
        if (roleItemModel.isSystem()) {
            return false;
        } else {
            return true;
        }
    }
}
