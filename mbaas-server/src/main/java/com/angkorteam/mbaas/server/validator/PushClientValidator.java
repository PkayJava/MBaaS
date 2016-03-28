package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.wicket.markup.html.form.validation.JooqFormValidator;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.validation.ValidationError;

/**
 * Created by socheat on 3/27/16.
 */
public class PushClientValidator extends JooqFormValidator {

    private TextField<String> pushVariantIdField;
    private TextField<String> pushSecretField;
    private TextField<String> pushGcmSenderIdField;

    private FormComponent<?>[] formComponents;

    public PushClientValidator(TextField<String> pushVariantIdField, TextField<String> pushSecretField, TextField<String> pushGcmSenderIdField) {
        this.pushVariantIdField = pushVariantIdField;
        this.pushSecretField = pushSecretField;
        this.pushGcmSenderIdField = pushGcmSenderIdField;
        this.formComponents = new FormComponent[]{this.pushVariantIdField, this.pushSecretField, this.pushGcmSenderIdField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.formComponents;
    }

    @Override
    public void validate(Form<?> form) {
        String pushVariantId = this.pushVariantIdField.getConvertedInput();
        String pushSecret = this.pushSecretField.getConvertedInput();
        String pushGcmSenderId = this.pushGcmSenderIdField.getConvertedInput();

        if ((pushVariantId == null || "".equals(pushVariantId))
                && (pushSecret == null || "".equals(pushSecret))
                && (pushGcmSenderId == null || "".equals(pushGcmSenderId))) {
        } else {
            if (pushVariantId == null || "".equals(pushVariantId)) {
                this.pushVariantIdField.error(new ValidationError("Required"));
            }
            if (pushSecret == null || "".equals(pushSecret)) {
                this.pushSecretField.error(new ValidationError("Required"));
            }
            if (pushGcmSenderId == null || "".equals(pushGcmSenderId)) {
                this.pushGcmSenderIdField.error(new ValidationError("Required"));
            }
        }
    }
}
