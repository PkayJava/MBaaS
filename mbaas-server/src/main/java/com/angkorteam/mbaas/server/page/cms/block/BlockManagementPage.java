package com.angkorteam.mbaas.server.page.cms.block;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.block.BlockPanel;
import com.angkorteam.mbaas.server.provider.BlockProvider;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 5/26/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/cms/block/management")
public class BlockManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "Block Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BlockProvider provider = new BlockProvider(getSession().getApplicationCode());

        provider.selectField(String.class, "blockId");
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

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", BlockManagementPage.class);
        add(refreshLink);
    }


    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String blockId = (String) object.get("blockId");
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("blockId", object.get("blockId"));
            setResponsePage(BlockModifyPage.class, parameters);
        }
        if ("Delete".equals(link)) {
            List<String> temp = new ArrayList<>();
            jdbcTemplate.update("DELETE FROM " + Jdbc.BLOCK + " WHERE " + Jdbc.Block.BLOCK_ID + " = ?", blockId);
            temp.add(BlockPanel.class.getName() + "_" + blockId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html");
            temp.add(BlockPanel.class.getName() + "_" + blockId + "-stage" + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html");
            for (String cacheKey : temp) {
                getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
            }
            return;
        }
        if ("Go Live".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.BLOCK + " SET " + Jdbc.Block.HTML + " = " + Jdbc.Block.STAGE_HTML + ", " + Jdbc.Block.JAVASCRIPT + " = " + Jdbc.Block.STAGE_JAVASCRIPT + ", " + Jdbc.Block.MODIFIED + " = false " + " WHERE " + Jdbc.Block.BLOCK_ID + " = ?", blockId);
            String cacheKey = BlockPanel.class.getName() + "_" + blockId + "_" + getSession().getStyle() + "_" + getLocale().toString() + ".html";
            getApplication().getMarkupSettings().getMarkupFactory().getMarkupCache().removeMarkup(cacheKey);
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
        if ("Go Live".equals(link)) {
            boolean modified = (boolean) object.get("modified");
            return modified;
        }
        return false;
    }
}
