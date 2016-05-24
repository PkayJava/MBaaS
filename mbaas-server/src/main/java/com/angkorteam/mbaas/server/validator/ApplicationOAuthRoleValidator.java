package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.configuration.Constants;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by socheat on 3/26/16.
 */
public class ApplicationOAuthRoleValidator extends JooqValidator<String> {

    public ApplicationOAuthRoleValidator() {
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        Pattern patternOAuthRoleName = Pattern.compile(configuration.getString(Constants.PATTERN_OAUTH_ROLE_NAME));
        if (validatable.getValue() != null && !"".equals(validatable.getValue())) {
            String oauthRoles[] = StringUtils.split(validatable.getValue(), ',');
            for (String oauthRole : oauthRoles) {
                String trimmed = oauthRole.trim();
                if ("".equals(trimmed)) {
                    validatable.error(new ValidationError(this, "format"));
                    break;
                } else {
                    Matcher matcher = patternOAuthRoleName.matcher(trimmed);
                    if (!matcher.matches()) {
                        validatable.error(new ValidationError(this, "format"));
                        break;
                    }
                }
            }
        }
    }
}
