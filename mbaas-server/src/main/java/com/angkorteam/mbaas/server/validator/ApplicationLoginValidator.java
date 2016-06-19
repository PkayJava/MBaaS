package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.configuration.Constants;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

/**
 * Created by socheat on 6/19/16.
 */
public class ApplicationLoginValidator extends JooqValidator<String> {

    public ApplicationLoginValidator() {
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String login = validatable.getValue();
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String admin = configuration.getString(Constants.USER_ADMIN);
        if (admin.equals(login)) {
            ValidationError error = new ValidationError(this, "invalid");
            validatable.error(error);
        }
    }
}
