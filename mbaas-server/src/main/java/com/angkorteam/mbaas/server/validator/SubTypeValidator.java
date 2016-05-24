package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.plain.enums.TypeEnum;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * Created by socheat on 3/21/16.
 */
public class SubTypeValidator extends AbstractFormValidator {

    private DropDownChoice<String> fieldType;

    private DropDownChoice<String> fieldSubType;

    private FormComponent[] formComponents;

    public SubTypeValidator(DropDownChoice<String> fieldType, DropDownChoice<String> fieldSubType) {
        this.fieldSubType = fieldSubType;
        this.fieldType = fieldType;
        this.formComponents = new FormComponent<?>[]{this.fieldType, this.fieldSubType};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.formComponents;
    }

    @Override
    public void validate(Form<?> form) {
        if (fieldType.getConvertedInput() != null && !"".equals(fieldType.getConvertedInput())) {
            if (TypeEnum.List.getLiteral().equals(fieldType.getConvertedInput())) {
                if (fieldSubType.getConvertedInput() == null || "".equals(fieldSubType.getConvertedInput())) {
                    fieldSubType.error(new ValidationError("Required"));
                }
            }
        }
    }
}
