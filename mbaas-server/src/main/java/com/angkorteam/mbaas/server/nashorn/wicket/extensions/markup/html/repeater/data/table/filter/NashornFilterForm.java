package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter;

import com.angkorteam.mbaas.server.nashorn.wicket.provider.NashornTableProvider;

import java.util.Map;

/**
 * Created by socheat on 6/12/16.
 */
public class NashornFilterForm extends org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm<Map<String, String>> {

    public NashornFilterForm(String id, NashornTableProvider locator) {
        super(id, locator);
    }

}
