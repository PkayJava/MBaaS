package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.form.validation.JooqFormValidator;
import com.angkorteam.mbaas.server.Jdbc;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 8/22/16.
 */
public class RestHttpHeaderValidator extends JooqFormValidator {

    private FormComponent<?>[] formComponents;

    private String applicationCode;

    private Select2MultipleChoice<Map<String, Object>> headerRequiredField;

    private Select2MultipleChoice<Map<String, Object>> headerOptionalField;

    public RestHttpHeaderValidator(String applicationCode, Select2MultipleChoice<Map<String, Object>> headerRequiredField, Select2MultipleChoice<Map<String, Object>> headerOptionalField) {
        this.applicationCode = applicationCode;
        this.headerOptionalField = headerOptionalField;
        this.headerRequiredField = headerRequiredField;
        this.formComponents = new FormComponent[]{this.headerRequiredField, this.headerOptionalField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.formComponents;
    }

    @Override
    public void validate(Form<?> form) {
        List<String> headerOptional = new ArrayList<>();
        if (headerOptionalField.getConvertedInput() != null) {
            for (Map<String, Object> header : headerOptionalField.getConvertedInput()) {
                headerOptional.add((String) header.get(Jdbc.HttpHeader.HTTP_HEADER_ID));
            }
        }
        List<String> headerRequired = new ArrayList<>();
        if (headerRequiredField.getConvertedInput() != null) {
            for (Map<String, Object> header : headerRequiredField.getConvertedInput()) {
                headerRequired.add((String) header.get(Jdbc.HttpHeader.HTTP_HEADER_ID));
            }
        }
        boolean hasError = false;
        for (String header : headerRequired) {
            if (headerOptional.contains(header)) {
                hasError = true;
                break;
            }
        }
        if (hasError) {
            for (FormComponent<?> formComponent : this.formComponents) {
                ValidationError error = new ValidationError();
                error.addKey("Conflicted");
                formComponent.error(error);
            }
        }
    }
}
