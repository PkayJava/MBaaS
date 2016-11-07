package com.angkorteam.mbaas.server.page.section;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.event.TableEvent;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredColumn;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.SectionProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheatkhauv on 10/26/16.
 */
public class SectionBrowsePage extends MBaaSPage implements TableEvent {

    @Override
    public String getPageUUID() {
        return SectionBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        SectionProvider provider = new SectionProvider();
        provider.selectField("sectionId", String.class);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredColumn(String.class, Model.of("title"), "title", this, provider));
        columns.add(new TextFilteredColumn(Boolean.class, Model.of("system"), "system", provider));
        columns.add(new ActionFilteredColumn(Model.of("action"), Model.of("filter"), Model.of("clear"), this, "Edit"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", SectionBrowsePage.class);
        layout.add(refreshLink);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", SectionCreatePage.class);
        layout.add(createLink);
    }

    @Override
    public String onCSSLink(String s, Map<String, Object> map) {
        if ("Edit".equals(s)) {
            return "btn-xs btn-info";
        }
        return "";
    }

    @Override
    public void onClickEventLink(String s, Map<String, Object> map) {
        String sectionId = (String) map.get("sectionId");
        PageParameters parameters = new PageParameters();
        parameters.add("sectionId", sectionId);
        setResponsePage(SectionModifyPage.class, parameters);
    }

    @Override
    public boolean isClickableEventLink(String s, Map<String, Object> map) {
        Boolean system = (Boolean) map.get("system");
        if ("Edit".equals(s)) {
            if (system) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String s, Map<String, Object> map) {
        return true;
    }

}
