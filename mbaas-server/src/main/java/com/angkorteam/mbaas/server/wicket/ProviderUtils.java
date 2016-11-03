package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TimeFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.Model;

import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class ProviderUtils {

    public static void addColumn(JooqProvider provider, List<IColumn<Map<String, Object>, String>> columns, AttributePojo attribute, TypeEnum type) {
        String column = attribute.getName();
        if (TypeEnum.Boolean == type) {
            columns.add(new TextFilteredJooqColumn(Boolean.class, Model.of(column), column, provider));
        } else if (TypeEnum.Long == type) {
            columns.add(new TextFilteredJooqColumn(Long.class, Model.of(column), column, provider));
        } else if (TypeEnum.Double == type) {
            columns.add(new TextFilteredJooqColumn(Double.class, Model.of(column), column, provider));
        } else if (TypeEnum.Character == type) {
            columns.add(new TextFilteredJooqColumn(Character.class, Model.of(column), column, provider));
        } else if (TypeEnum.String == type || TypeEnum.Text == type) {
            columns.add(new TextFilteredJooqColumn(String.class, Model.of(column), column, provider));
        } else if (TypeEnum.Time == type) {
            columns.add(new TimeFilteredJooqColumn(Model.of(column), column, provider));
        } else if (TypeEnum.Date == type) {
            columns.add(new DateFilteredJooqColumn(Model.of(column), column, provider));
        } else if (TypeEnum.DateTime == type) {
            columns.add(new DateTimeFilteredJooqColumn(Model.of(column), column, provider));
        }
    }
}
