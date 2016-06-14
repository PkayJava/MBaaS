package com.angkorteam.mbaas.server.page.cms.page;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.page.PagePage;
import com.angkorteam.mbaas.server.page.job.JobManagementPage;
import com.angkorteam.mbaas.server.provider.PageProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 5/26/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/cms/page/management")
public class PageManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Page Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageProvider provider = new PageProvider(getSession().getApplicationCode());

        provider.selectField(String.class, "pageId");
        provider.selectField(String.class, "userId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("code", this), "code", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("title", this), "title", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("security", this), "security", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Grant", "Deny", "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", PageManagementPage.class);
        add(refreshLink);
    }


    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String pageId = (String) object.get("pageId");
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("pageId", object.get("pageId"));
            setResponsePage(PageModifyPage.class, parameters);
        }
        if ("Grant".equals(link)) {
           // jdbcTemplate.update("UPDATE " + Jdbc.PAGE + " SET " + Jdbc.Page.SECURITY + " = ? WHERE " + Jdbc.Page.PAGE_ID + " = ?", SecurityEnum.Granted.getLiteral(), pageId);
            return;
        }
        if ("Deny".equals(link)) {
           // jdbcTemplate.update("UPDATE " + Jdbc.PAGE + " SET " + Jdbc.Page.SECURITY + " = ? WHERE " + Jdbc.Page.PAGE_ID + " = ?", SecurityEnum.Denied.getLiteral(), pageId);
            return;
        }
        if ("Delete".equals(link)) {
            String cacheKey = PagePage.class.getName() + "_" + pageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
            String filename = PagePage.class.getName().replaceAll("\\.", "/") + "_" + pageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
            File temp = new File(FileUtils.getTempDirectory(), filename);
            FileUtils.deleteQuietly(temp);
            getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
            jdbcTemplate.update("DELETE FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", pageId);
            return;
        }
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
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return hasAccess(link, object);
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return hasAccess(link, object);
    }

    private boolean hasAccess(String link, Map<String, Object> object) {
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
