package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.share.provider.TableProvider;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateFilteredColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateTimeFilteredColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TimeFilteredColumn;
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

    public static void addColumn(TableProvider provider, List<IColumn<Map<String, Object>, String>> columns, AttributePojo attribute, TypeEnum type) {
        String column = attribute.getName();
        if (TypeEnum.Boolean == type) {
            columns.add(new TextFilteredColumn(Boolean.class, Model.of(column), column, provider));
        } else if (TypeEnum.Long == type) {
            columns.add(new TextFilteredColumn(Long.class, Model.of(column), column, provider));
        } else if (TypeEnum.Double == type) {
            columns.add(new TextFilteredColumn(Double.class, Model.of(column), column, provider));
        } else if (TypeEnum.Character == type) {
            columns.add(new TextFilteredColumn(Character.class, Model.of(column), column, provider));
        } else if (TypeEnum.String == type || TypeEnum.Text == type) {
            columns.add(new TextFilteredColumn(String.class, Model.of(column), column, provider));
        } else if (TypeEnum.Time == type) {
            columns.add(new TimeFilteredColumn(Model.of(column), column, provider));
        } else if (TypeEnum.Date == type) {
            columns.add(new DateFilteredColumn(Model.of(column), column, provider));
        } else if (TypeEnum.DateTime == type) {
            columns.add(new DateTimeFilteredColumn(Model.of(column), column, provider));
        }
    }
}
