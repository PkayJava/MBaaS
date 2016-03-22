package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.plain.enums.QueryReturnTypeEnum;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * Created by socheat on 3/21/16.
 */
public class QueryReturnSubTypeValidator extends AbstractFormValidator {

    private DropDownChoice<String> returnType;

    private DropDownChoice<String> returnSubType;

    private FormComponent[] formComponents;

    public QueryReturnSubTypeValidator(DropDownChoice<String> returnType, DropDownChoice<String> returnSubType) {
        this.returnSubType = returnSubType;
        this.returnType = returnType;
        this.formComponents = new FormComponent<?>[]{this.returnType, this.returnSubType};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.formComponents;
    }

    @Override
    public void validate(Form<?> form) {
        if (returnType.getConvertedInput() != null && !"".equals(returnType.getConvertedInput())) {
            if (QueryReturnTypeEnum.List.getLiteral().equals(returnType.getConvertedInput())) {
                if (returnSubType.getConvertedInput() == null || "".equals(returnSubType.getConvertedInput())) {
                    returnSubType.error(new ValidationError("Required"));
                }
            }
        }
    }
}
