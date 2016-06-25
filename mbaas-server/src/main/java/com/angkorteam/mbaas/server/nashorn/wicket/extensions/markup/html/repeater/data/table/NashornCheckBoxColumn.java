package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.GoAndClearFilter;
import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter.NashornCheckBoxFilter;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel.CheckBoxPanel;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Map;

/**
 * Created by socheat on 6/18/16.
 */
public class NashornCheckBoxColumn extends FilteredAbstractColumn<Map<String, Object>, String> {

    private Map<String, String> actions;

    private String tableId;

    private String script;

    private Factory factory;

    private Disk disk;

    public NashornCheckBoxColumn(IModel<String> displayModel, Map<String, String> actions, String tableId) {
        super(displayModel);
        this.actions = actions;
        this.tableId = tableId;
    }

    @Override
    public void populateItem(Item<ICellPopulator<Map<String, Object>>> cellItem, String componentId, IModel<Map<String, Object>> rowModel) {
        cellItem.add(new CheckBoxPanel(componentId));
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        NashornCheckBoxFilter filter = new NashornCheckBoxFilter(componentId, this.tableId, getDisplayModel().getObject(), form, actions);
        filter.setScript(this.script);
        filter.setDisk(this.disk);
        filter.setFactory(this.factory);
        return filter;
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