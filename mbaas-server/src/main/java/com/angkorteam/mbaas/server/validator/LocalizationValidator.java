//package com.angkorteam.mbaas.server.validator;
//
//import com.angkorteam.framework.extension.wicket.markup.html.form.validation.JooqFormValidator;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.LocalizationTable;
//import org.apache.wicket.markup.html.form.Form;
//import org.apache.wicket.markup.html.form.FormComponent;
//import org.apache.wicket.markup.html.form.TextField;
//import org.apache.wicket.util.lang.Classes;
//import org.apache.wicket.validation.ValidationError;
//import org.jooq.Condition;
//import org.jooq.DSLContext;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by socheat on 3/14/16.
// */
//public class LocalizationValidator extends JooqFormValidator {
//
//    private String resourceId;
//
//    private TextField<String> pageField;
//
//    private TextField<String> keyField;
//
//    private TextField<String> languageField;
//
//    private FormComponent<?>[] components;
//
//    public LocalizationValidator(String resourceId, TextField<String> pageField, TextField<String> keyField, TextField<String> languageField) {
//        this.resourceId = resourceId;
//        this.pageField = pageField;
//        this.keyField = keyField;
//        this.languageField = languageField;
//        this.components = new FormComponent<?>[]{pageField, keyField, languageField};
//    }
//
//    public LocalizationValidator(TextField<String> pageField, TextField<String> keyField, TextField<String> languageField) {
//        this.pageField = pageField;
//        this.keyField = keyField;
//        this.languageField = languageField;
//        this.components = new FormComponent<?>[]{pageField, keyField, languageField};
//    }
//
//    @Override
//    public FormComponent<?>[] getDependentFormComponents() {
//        return this.components;
//    }
//
//    @Override
//    public void validate(Form<?> form) {
//        String page = pageField.getConvertedInput();
//        String language = languageField.getConvertedInput();
//        String key = keyField.getConvertedInput();
//        DSLContext context = getDSLContext();
//        LocalizationTable localizationTable = Tables.LOCALIZATION.as("localizationTable");
//        List<Condition> where = new ArrayList<>();
//        if (resourceId != null && !"".equals(resourceId)) {
//            where.add(localizationTable.LOCALIZATION_ID.ne(resourceId));
//        }
//        if (key != null && !"".equals(key)
//                && page != null && !"".equals(page)
//                && language != null && !"".equals(language)) {
//            where.add(localizationTable.PAGE.eq(page));
//            where.add(localizationTable.KEY.eq(key));
//            where.add(localizationTable.LANGUAGE.eq(language));
//            int count = context.selectCount().from(localizationTable).where(where).fetchOneInto(int.class);
//            if (count > 0) {
//                keyField.error(new ValidationError(Classes.simpleName(LocalizationValidator.class) + ".duplicated"));
//                pageField.error(new ValidationError(Classes.simpleName(LocalizationValidator.class) + ".duplicated"));
//                languageField.error(new ValidationError(Classes.simpleName(LocalizationValidator.class) + ".duplicated"));
//            }
//            return;
//        }
//        if (key != null && !"".equals(key)
//                && page != null && !"".equals(page)
//                ) {
//            where.add(localizationTable.KEY.eq(key));
//            where.add(localizationTable.PAGE.eq(page));
//            where.add(localizationTable.LANGUAGE.isNull());
//            int count = context.selectCount().from(localizationTable).where(where).fetchOneInto(int.class);
//            if (count > 0) {
//                keyField.error(new ValidationError(Classes.simpleName(LocalizationValidator.class) + ".duplicated"));
//                pageField.error(new ValidationError(Classes.simpleName(LocalizationValidator.class) + ".duplicated"));
//            }
//            return;
//        }
//        if (key != null && !"".equals(key)
//                && language != null && !"".equals(language)) {
//            where.add(localizationTable.KEY.eq(key));
//            where.add(localizationTable.PAGE.isNull());
//            where.add(localizationTable.LANGUAGE.eq(language));
//            int count = context.selectCount().from(localizationTable).where(where).fetchOneInto(int.class);
//            if (count > 0) {
//                keyField.error(new ValidationError(Classes.simpleName(LocalizationValidator.class) + ".duplicated"));
//                languageField.error(new ValidationError(Classes.simpleName(LocalizationValidator.class) + ".duplicated"));
//            }
//            return;
//        }
//
//        if (key != null && !"".equals(key)
//                ) {
//            where.add(localizationTable.KEY.eq(key));
//            where.add(localizationTable.PAGE.isNull());
//            where.add(localizationTable.LANGUAGE.isNull());
//            int count = context.selectCount().from(localizationTable).where(where).fetchOneInto(int.class);
//            if (count > 0) {
//                keyField.error(new ValidationError(Classes.simpleName(LocalizationValidator.class) + ".duplicated"));
//            }
//            return;
//        }
//    }
//}
