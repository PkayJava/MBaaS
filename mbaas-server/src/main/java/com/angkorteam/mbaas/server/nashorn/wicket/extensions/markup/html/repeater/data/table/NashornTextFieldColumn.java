package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel.TextFieldPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import java.util.Map;

/**
 * Created by socheat on 6/18/16.
 */
public class NashornTextFieldColumn extends HeaderlessColumn<Map<String, Object>, String> {

    public NashornTextFieldColumn() {
    }

    @Override
    public void populateItem(Item<ICellPopulator<Map<String, Object>>> cellItem, String componentId, IModel<Map<String, Object>> rowModel) {
        cellItem.add(new TextFieldPanel(componentId));
    }
}
