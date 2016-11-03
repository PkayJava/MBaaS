//package com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table;
//
//import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
//import org.apache.commons.lang3.time.DateFormatUtils;
//import org.apache.wicket.Component;
//import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
//import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
//import org.apache.wicket.model.IModel;
//import org.apache.wicket.model.Model;
//import org.apache.wicket.model.PropertyModel;
//
//import java.util.Date;
//import java.util.Map;
//
///**
// * Created by socheat on 6/11/16.
// */
//public class NashornDateTimeColumn extends TextFilteredPropertyColumn<Map<String, Object>, Map<String, String>, String> {
//
//    public NashornDateTimeColumn(IModel<String> headerModel, String columnName) {
//        super(headerModel, columnName, columnName);
//    }
//
//    @Override
//    public Component getFilter(String componentId, FilterForm<?> form) {
//        IModel<Map<String, String>> filterModel = this.getFilterModel(form);
//        TextFilter<Map<String, String>> filter = new TextFilter<>(componentId, filterModel, form);
//        return filter;
//    }
//
//    public IModel<?> getDataModel(IModel<Map<String, Object>> rowModel) {
//        PropertyModel propertyModel = new PropertyModel(rowModel, getPropertyExpression());
//        Date date = (Date) propertyModel.getObject();
//        return date == null ? null : new Model<>(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
//    }
//}
