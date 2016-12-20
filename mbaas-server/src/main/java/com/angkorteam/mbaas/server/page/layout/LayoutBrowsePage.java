package com.angkorteam.mbaas.server.page.layout;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.*;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.LayoutProvider;
import com.google.common.collect.Maps;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.elasticsearch.common.Strings;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by socheatkhauv on 10/26/16.
 */
public class LayoutBrowsePage extends MBaaSPage {

    private DataTable<Map<String, Object>, String> dataTable;

    @Override
    public String getPageUUID() {
        return LayoutBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        LayoutProvider provider = new LayoutProvider();
        provider.selectField("layoutId", String.class);
        provider.selectField("cmsLayout", Boolean.class);
        provider.selectField("dateModified", Date.class);
        provider.setSort("dateModified", SortOrder.DESCENDING);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("title"), "title", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("description"), "description", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("system"), "system", this::modelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("cms"), "cmsLayout", this::modelValue));
        columns.add(new ActionFilterColumn(Model.of("action"), this::actions, this::clickable, this::itemCss, this::itemClick));

        this.dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        this.dataTable.addTopToolbar(new FilterToolbar(this.dataTable, filterForm));
        filterForm.add(this.dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", LayoutBrowsePage.class);
        layout.add(refreshLink);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", LayoutCreatePage.class);
        layout.add(createLink);
    }

    private void itemClick(String link, Map<String, Object> object, AjaxRequestTarget target) {
        if ("Edit".equals(link)) {
            String layoutId = (String) object.get("layoutId");
            PageParameters parameters = new PageParameters();
            parameters.add("layoutId", layoutId);
            setResponsePage(LayoutModifyPage.class, parameters);
        } else if ("Delete".equals(link)) {
            String layoutId = (String) object.get("layoutId");
            JdbcTemplate jdbcTemplate = Spring.getBean(JdbcTemplate.class);
            Map<String, Object> layoutRecord = jdbcTemplate.queryForMap("SELECT * FROM layout WHERE layout_id = ?", layoutId);
            String groovyId = (String) layoutRecord.get("groovy_id");
            if (!Strings.isNullOrEmpty(groovyId)) {
                Map<String, Object> groovyRecord = jdbcTemplate.queryForMap("SELECT * FROM groovy WHERE groovy_id = ?", groovyId);
                jdbcTemplate.update("DELETE FROM groovy WHERE groovy_id = ?", groovyId);
                GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
                classLoader.removeClassCache((String) groovyRecord.get("java_class"));
                classLoader.removeSourceCache((String) groovyRecord.get("java_class"));
            }
            jdbcTemplate.update("DELETE FROM layout WHERE layout_id = ?", layoutId);
            target.add(this.dataTable);
        }
    }

    private Map<String, IModel<String>> actions() {
        Map<String, IModel<String>> actions = Maps.newHashMap();
        actions.put("Edit", Model.of("Edit"));
        actions.put("Delete", Model.of("Delete"));
        return actions;
    }

    private Boolean clickable(String link, Map<String, Object> object) {
        Boolean system = (Boolean) object.get("system");
        Boolean cms = (Boolean) object.get("cmsLayout");
        if ("Edit".equals(link)) {
            return cms;
        } else if ("Delete".equals(link)) {
            return !system;
        }
        return false;
    }

    private ItemCss itemCss(String link, Map<String, Object> model) {
        if ("Edit".equals(link)) {
            return ItemCss.INFO;
        } else if ("Delete".equals(link)) {
            return ItemCss.DANGER;
        }
        return ItemCss.NONE;
    }

    private Object modelValue(String name, Map<String, Object> stringObjectMap) {
        return stringObjectMap.get(name);
    }

}
