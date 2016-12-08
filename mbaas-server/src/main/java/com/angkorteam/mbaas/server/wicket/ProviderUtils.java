package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.share.provider.TableProvider;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ItemClass;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilterColumn;
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
            columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of(column), column, ProviderUtils::getModelValue));
        } else if (TypeEnum.Long == type) {
            columns.add(new TextFilterColumn(provider, ItemClass.Long, Model.of(column), column, ProviderUtils::getModelValue));
        } else if (TypeEnum.Double == type) {
            columns.add(new TextFilterColumn(provider, ItemClass.Double, Model.of(column), column, ProviderUtils::getModelValue));
        } else if (TypeEnum.Character == type) {
            columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of(column), column, ProviderUtils::getModelValue));
        } else if (TypeEnum.String == type || TypeEnum.Text == type) {
            columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of(column), column, ProviderUtils::getModelValue));
        } else if (TypeEnum.Time == type) {
            columns.add(new TextFilterColumn(provider, ItemClass.Time, Model.of(column), column, ProviderUtils::getModelValue));
        } else if (TypeEnum.Date == type) {
            columns.add(new TextFilterColumn(provider, ItemClass.Date, Model.of(column), column, ProviderUtils::getModelValue));
        } else if (TypeEnum.DateTime == type) {
            columns.add(new TextFilterColumn(provider, ItemClass.DateTime, Model.of(column), column, ProviderUtils::getModelValue));
        }
    }

    private static Object getModelValue(String s, Map<String, Object> stringObjectMap) {
        return stringObjectMap.get(s);
    }
}
