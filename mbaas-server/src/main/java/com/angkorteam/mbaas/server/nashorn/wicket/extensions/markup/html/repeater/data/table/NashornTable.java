package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.NashornTableProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;

import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/2/16.
 */
public class NashornTable extends DefaultDataTable<Map<String, Object>, String> {

    public NashornTable(String id, List<IColumn<Map<String, Object>, String>> columns, NashornTableProvider tableProvider, int rowsPerPage) {
        super(id, columns, tableProvider, rowsPerPage);
        setOutputMarkupId(true);
    }

}
