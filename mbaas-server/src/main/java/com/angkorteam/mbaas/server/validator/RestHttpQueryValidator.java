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
public class RestHttpQueryValidator extends JooqFormValidator {

    private FormComponent<?>[] formComponents;

    private String applicationCode;

    private Select2MultipleChoice<Map<String, Object>> requestQueryRequiredField;

    private Select2MultipleChoice<Map<String, Object>> requestQueryOptionalField;

    public RestHttpQueryValidator(String applicationCode, Select2MultipleChoice<Map<String, Object>> requestQueryRequiredField, Select2MultipleChoice<Map<String, Object>> requestQueryOptionalField) {
        this.applicationCode = applicationCode;
        this.requestQueryOptionalField = requestQueryOptionalField;
        this.requestQueryRequiredField = requestQueryRequiredField;
        this.formComponents = new FormComponent[]{this.requestQueryRequiredField, this.requestQueryOptionalField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.formComponents;
    }

    @Override
    public void validate(Form<?> form) {
        List<String> requestQueryOptional = new ArrayList<>();
        if (requestQueryOptionalField.getConvertedInput() != null) {
            for (Map<String, Object> query : requestQueryOptionalField.getConvertedInput()) {
                requestQueryOptional.add((String) query.get(Jdbc.HttpQuery.HTTP_QUERY_ID));
            }
        }
        List<String> requestQueryRequired = new ArrayList<>();
        if (requestQueryRequiredField.getConvertedInput() != null) {
            for (Map<String, Object> query : requestQueryRequiredField.getConvertedInput()) {
                requestQueryRequired.add((String) query.get(Jdbc.HttpQuery.HTTP_QUERY_ID));
            }
        }
        boolean hasError = false;
        for (String query : requestQueryRequired) {
            if (requestQueryOptional.contains(query)) {
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
