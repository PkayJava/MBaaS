package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilter;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/11/16.
 */
public class NashornChoiceColumn extends ChoiceFilteredPropertyColumn<Map<String, Object>, String, String> {

    private Class<?> columnClass;

    private String columnName;

    public NashornChoiceColumn(Class<?> columnClass, IModel<String> displayModel, String columnName, IModel<List<String>> filterChoices) {
        super(displayModel, columnName, columnName, filterChoices);
        this.columnClass = columnClass;
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        ChoiceFilter<String> filter = new ChoiceFilter<>(componentId, getFilterModel(form), form, getFilterChoices(), enableAutoSubmit());
        IChoiceRenderer<String> renderer = getChoiceRenderer();
        if (renderer != null) {
            filter.getChoice().setChoiceRenderer(renderer);
        }
        return filter;
    }
}
