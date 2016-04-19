package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.framework.extension.wicket.table.filter.*;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;

import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class ProviderUtils {

    public static void addColumn(JooqProvider provider, List<IColumn<Map<String, Object>, String>> columns, AttributeRecord attributeRecord, AttributeTypeEnum attributeType, Page page) {
        if (AttributeTypeEnum.Boolean == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Byte == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TextFilteredJooqColumn(Byte.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Short == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TextFilteredJooqColumn(Short.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Integer == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Long == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TextFilteredJooqColumn(Long.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Float == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TextFilteredJooqColumn(Float.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Double == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TextFilteredJooqColumn(Double.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Character == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TextFilteredJooqColumn(Character.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.String == attributeType || AttributeTypeEnum.Text == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Time == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new TimeFilteredJooqColumn(JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.Date == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new DateFilteredJooqColumn(JooqUtils.lookup(column, page), column, provider));
        } else if (AttributeTypeEnum.DateTime == attributeType) {
            String column = attributeRecord.getName();
            columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup(column, page), column, provider));
        }
    }
}
