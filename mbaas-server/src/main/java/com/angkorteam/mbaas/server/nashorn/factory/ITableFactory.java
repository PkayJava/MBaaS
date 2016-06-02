package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.NashornTable;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.NashornTableProvider;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/2/16.
 */
public interface ITableFactory extends Serializable {

    NashornTable createTable(String id, List<IColumn<Map<String, Object>, String>> columns, NashornTableProvider tableProvider, int rowsPerPage);

    NashornTable createTable(MarkupContainer container, String id, List<IColumn<Map<String, Object>, String>> columns, NashornTableProvider tableProvider, int rowsPerPage);
    
}
