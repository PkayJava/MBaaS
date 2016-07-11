package com.angkorteam.mbaas.server.page.cms.page;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.function.RestoreFunction;
import com.angkorteam.mbaas.server.page.PagePage;
import com.angkorteam.mbaas.server.provider.PageProvider;
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
        provider.selectField(Boolean.class, "modified");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("code", this), "code", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("title", this), "title", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("description", this), "description", this, provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Delete", "Live Preview", "Go Live", "Stage Preview"));

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
        if ("Delete".equals(link)) {
            Application application = ApplicationUtils.getApplication();
            RestoreFunction.backup(application.getJdbcGson(), getApplicationJdbcTemplate(), Jdbc.PAGE, pageId);
            String cacheKey = PagePage.class.getName() + "_" + pageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
            getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
            jdbcTemplate.update("DELETE FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", pageId);
            return;
        }
        if ("Go Live".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.PAGE + " SET " + Jdbc.Page.HTML + " = " + Jdbc.Page.STAGE_HTML + ", " + Jdbc.Page.JAVASCRIPT + " = " + Jdbc.Page.STAGE_JAVASCRIPT + ", " + Jdbc.Page.MODIFIED + " = false " + " WHERE " + Jdbc.Page.PAGE_ID + " = ?", pageId);
            {
                String cacheKey = com.angkorteam.mbaas.server.page.PagePage.class.getName() + "_" + pageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
                getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
            }
            {
                String cacheKey = com.angkorteam.mbaas.server.page.MasterPage.class.getName() + "_" + pageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
                getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
            }
            return;
        }
        if ("Stage Preview".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("pageId", object.get("pageId"));
            parameters.add("stage", true);
            setResponsePage(com.angkorteam.mbaas.server.page.PagePage.class, parameters);
            return;
        }
        if ("Live Preview".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("pageId", object.get("pageId"));
            setResponsePage(com.angkorteam.mbaas.server.page.PagePage.class, parameters);
            return;
        }
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Go Live".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Stage Preview".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Live Preview".equals(link)) {
            return "btn-xs btn-info";
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
        if ("Delete".equals(link)) {
            return true;
        }
        if ("Go Live".equals(link) || "Stage Preview".equals(link)) {
            boolean modified = (boolean) object.get("modified");
            return modified;
        }
        if ("Live Preview".equals(link)) {
            return true;
        }
        return false;
    }
}
