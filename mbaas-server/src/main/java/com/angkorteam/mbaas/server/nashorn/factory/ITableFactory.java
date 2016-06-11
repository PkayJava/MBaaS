package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter.NashornFilterForm;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 6/2/16.
 */
public interface ITableFactory extends Serializable {

    NashornFilterForm createTable(String id, Map<String, Class<?>> tableColumns, Map<String, String> queryColumns, int rowsPerPage);

    NashornFilterForm createTable(MarkupContainer container, String id, Map<String, Class<?>> tableColumns, Map<String, String> queryColumns, int rowsPerPage);

}
