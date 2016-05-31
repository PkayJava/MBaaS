package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TimeFilteredJooqColumn;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;

import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class ProviderUtils {

    public static void addColumn(JooqProvider provider, List<IColumn<Map<String, Object>, String>> columns, Map<String, Object> attributeRecord, AttributeTypeEnum attributeType, Page page) {
        String column = (String) attributeRecord.get("name");
        if (AttributeTypeEnum.Boolean == attributeType) {
            columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Byte == attributeType) {
            columns.add(new TextFilteredJooqColumn(Byte.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Short == attributeType) {
            columns.add(new TextFilteredJooqColumn(Short.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Integer == attributeType) {
            columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Long == attributeType) {
            columns.add(new TextFilteredJooqColumn(Long.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Float == attributeType) {
            columns.add(new TextFilteredJooqColumn(Float.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Double == attributeType) {
            columns.add(new TextFilteredJooqColumn(Double.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Character == attributeType) {
            columns.add(new TextFilteredJooqColumn(Character.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.String == attributeType || AttributeTypeEnum.Text == attributeType) {
            columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Time == attributeType) {
            columns.add(new TimeFilteredJooqColumn(JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Date == attributeType) {
            columns.add(new DateFilteredJooqColumn(JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.DateTime == attributeType) {
            columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup(column, page), column, provider));
        }
    }
}
