package com.angkorteam.mbaas.server.page.job;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.function.RestoreFunction;
import com.angkorteam.mbaas.server.provider.JobProvider;
import com.angkorteam.mbaas.server.wicket.*;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 4/24/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/job/management")
public class JobManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Job Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        JobProvider provider = new JobProvider(getSession().getApplicationCode());

        provider.selectField(String.class, "jobId");
        provider.selectField(String.class, "applicationUserId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("cron", this), "cron", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("errorClass", this), "errorClass", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("errorMessage", this), "errorMessage", this, provider));
        columns.add(new TextFilteredJooqColumn(Double.class, JooqUtils.lookup("consume", this), "consume", this, provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateLastExecuted", this), "dateLastExecuted", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("applicationUser", this), "applicationUser", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("security", this), "security", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Grant", "Deny", "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", JobManagementPage.class);
        add(refreshLink);

    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Grant".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Deny".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        return "";
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        String jobId = (String) object.get("jobId");
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("jobId", object.get("jobId"));
            setResponsePage(JobModifyPage.class, parameters);
        }
        if ("Grant".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.JOB + " SET " + Jdbc.Job.SECURITY + " = ? WHERE " + Jdbc.Job.JOB_ID + " = ?", SecurityEnum.Granted.getLiteral(), jobId);
            return;
        }
        if ("Deny".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.JOB + " SET " + Jdbc.Job.SECURITY + " = ? WHERE " + Jdbc.Job.JOB_ID + " = ?", SecurityEnum.Denied.getLiteral(), jobId);
            return;
        }
        if ("Delete".equals(link)) {
            Application application = ApplicationUtils.getApplication();
            RestoreFunction.backup(application.getJdbcGson(), getApplicationJdbcTemplate(), Jdbc.JOB, jobId);
            jdbcTemplate.update("DELETE FROM " + Jdbc.JOB + " WHERE " + Jdbc.Job.JOB_ID + " = ?", jobId);
            return;
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return isAccess(link, object);
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return isAccess(link, object);
    }

    protected boolean isAccess(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Grant".equals(link)) {
            String security = (String) object.get("security");
            if (SecurityEnum.Denied.getLiteral().equals(security)) {
                return true;
            }
        }
        if ("Deny".equals(link)) {
            String security = (String) object.get("security");
            if (SecurityEnum.Granted.getLiteral().equals(security)) {
                return true;
            }
        }
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }
}
