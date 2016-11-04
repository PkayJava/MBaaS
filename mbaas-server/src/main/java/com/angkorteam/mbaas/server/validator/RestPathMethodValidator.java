package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.base.Strings;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 11/3/16.
 */
public class RestPathMethodValidator extends AbstractFormValidator {

    private String restId;

    private TextField<String> pathField;

    private DropDownChoice<String> methodField;

    private FormComponent<?>[] components;

    public RestPathMethodValidator(TextField<String> pathField, DropDownChoice<String> methodField) {
        this.pathField = pathField;
        this.methodField = methodField;
        this.components = new FormComponent<?>[]{this.pathField, this.methodField};
    }

    public RestPathMethodValidator(String restId, TextField<String> pathField, DropDownChoice<String> methodField) {
        this.restId = restId;
        this.pathField = pathField;
        this.methodField = methodField;
        this.components = new FormComponent<?>[]{this.pathField, this.methodField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.components;
    }

    @Override
    public void validate(Form<?> form) {
        String path = this.pathField.getConvertedInput();
        String method = this.methodField.getConvertedInput();
        if (!Strings.isNullOrEmpty(path)) {
            if (!path.startsWith("/") || path.equals("/resource") || path.startsWith("/resource/") || path.endsWith("/") || path.contains("//")) {
                this.pathField.error(new ValidationError("invalid"));
                return;
            }
        }
        if (!Strings.isNullOrEmpty(path) && !Strings.isNullOrEmpty(method)) {
            DSLContext context = Spring.getBean(DSLContext.class);
            RestTable restTable = Tables.REST.as("restTable");
            int count = 0;
            if (Strings.isNullOrEmpty(this.restId)) {
                count = context.selectCount().from(restTable).where(restTable.PATH.eq(path)).and(restTable.METHOD.eq(method)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(restTable).where(restTable.PATH.eq(path)).and(restTable.METHOD.eq(method)).and(restTable.REST_ID.notEqual(this.restId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                this.pathField.error(new ValidationError("duplicated"));
                this.methodField.error(new ValidationError("duplicated"));
            }
        }
    }
}
