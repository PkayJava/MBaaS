package com.angkorteam.mbaas.server.page.cms.master;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.provider.MasterPageProvider;
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
@Mount("/cms/master/management")
public class MasterManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Layout Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        MasterPageProvider provider = new MasterPageProvider(getSession().getApplicationCode());

        provider.selectField(String.class, "masterPageId");
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

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", MasterManagementPage.class);
        add(refreshLink);
    }


    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String masterPageId = (String) object.get("masterPageId");
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("masterPageId", object.get("masterPageId"));
            setResponsePage(MasterModifyPage.class, parameters);
        }
        if ("Delete".equals(link)) {
            jdbcTemplate.update("DELETE FROM " + Jdbc.MASTER_PAGE + " WHERE " + Jdbc.MasterPage.MASTER_PAGE_ID + " = ?", masterPageId);
            {
                String cacheKey = com.angkorteam.mbaas.server.page.MasterPage.class.getName() + "_" + masterPageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
                String filename = com.angkorteam.mbaas.server.page.MasterPage.class.getName().replaceAll("\\.", "/") + "_" + masterPageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
                File temp = new File(FileUtils.getTempDirectory(), filename);
                FileUtils.deleteQuietly(temp);
                getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
            }
            {
                String cacheKey = com.angkorteam.mbaas.server.page.MasterPage.class.getName() + "_" + masterPageId + "-stage" + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
                String filename = com.angkorteam.mbaas.server.page.MasterPage.class.getName().replaceAll("\\.", "/") + "_" + masterPageId + "-stage" + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
                File temp = new File(FileUtils.getTempDirectory(), filename);
                FileUtils.deleteQuietly(temp);
                getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
            }
            return;
        }
        if ("Go Live".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.MASTER_PAGE + " SET " + Jdbc.MasterPage.HTML + " = " + Jdbc.MasterPage.STAGE_HTML + ", " + Jdbc.MasterPage.JAVASCRIPT + " = " + Jdbc.MasterPage.STAGE_JAVASCRIPT + ", " + Jdbc.MasterPage.MODIFIED + " = false " + " WHERE " + Jdbc.MasterPage.MASTER_PAGE_ID + " = ?", masterPageId);
            String cacheKey = com.angkorteam.mbaas.server.page.MasterPage.class.getName() + "_" + masterPageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
            String filename = com.angkorteam.mbaas.server.page.MasterPage.class.getName().replaceAll("\\.", "/") + "_" + masterPageId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
            File temp = new File(FileUtils.getTempDirectory(), filename);
            FileUtils.deleteQuietly(temp);
            getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
            return;
        }
        if ("Stage Preview".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("masterPageId", object.get("masterPageId"));
            parameters.add("stage", true);
            setResponsePage(com.angkorteam.mbaas.server.page.MasterPage.class, parameters);
            return;
        }
        if ("Live Preview".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("masterPageId", object.get("masterPageId"));
            setResponsePage(com.angkorteam.mbaas.server.page.MasterPage.class, parameters);
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
