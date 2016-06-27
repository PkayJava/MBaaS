package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table;

import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter.NashornGoAndClearFilter;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel.ActionPanel;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Map;

/**
 * Created by socheat on 6/18/16.
 */
public class NashornActionColumn extends FilteredAbstractColumn<Map<String, Object>, String> {

    private Map<String, String> actions;

    private Map<String, String> links;

    private Map<String, Object> pageModel;

    private String tableId;

    private String script;

    private Factory factory;

    private Disk disk;

    public NashornActionColumn(IModel<String> displayModel, Map<String, String> links, Map<String, String> actions, String tableId, Map<String, Object> pageModel) {
        super(displayModel);
        this.actions = actions;
        this.links = links;
        this.tableId = tableId;
        this.pageModel = pageModel;
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        NashornGoAndClearFilter object = new NashornGoAndClearFilter(componentId, this.tableId, getDisplayModel().getObject(), form, Model.of("Filter"), Model.of("Clear"), this.actions, this.pageModel);
        object.setDisk(this.disk);
        object.setFactory(this.factory);
        object.setScript(this.script);
        return object;
    }

    @Override
    public void populateItem(Item<ICellPopulator<Map<String, Object>>> cellItem, String componentId, IModel<Map<String, Object>> itemModel) {
        ActionPanel object = new ActionPanel(componentId, this.tableId, getDisplayModel().getObject(), this.links, this.pageModel, itemModel);
        object.setDisk(this.disk);
        object.setScript(this.script);
        object.setFactory(this.factory);
        cellItem.add(object);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public Disk getDisk() {
        return disk;
    }

    public void setDisk(Disk disk) {
        this.disk = disk;
    }
}