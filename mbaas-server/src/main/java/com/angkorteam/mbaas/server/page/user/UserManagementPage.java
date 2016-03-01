package com.angkorteam.mbaas.server.page.user;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.server.provider.UserFilterModel;
import com.angkorteam.mbaas.server.provider.UserItemModel;
import com.angkorteam.mbaas.server.provider.UserProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
@Mount("/user/management")
public class UserManagementPage extends Page {

    public UserManagementPage() {
    }

    public UserManagementPage(IModel<?> model) {
        super(model);
    }

    public UserManagementPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        UserProvider provider = new UserProvider();
        provider.selectField("userId", provider.getUserId());

        provider.setGroupBy(provider.getRoleId());

        FilterForm<UserFilterModel> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<UserItemModel, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn<>(String.class, JooqUtils.lookup("login", this), UserFilterModel.class, "login", provider, provider.getLogin()));
        columns.add(new TextFilteredJooqColumn<>(String.class, JooqUtils.lookup("roleName", this), UserFilterModel.class, "roleName", provider, provider.getRoleName()));

        DataTable<UserItemModel, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

    }
}
