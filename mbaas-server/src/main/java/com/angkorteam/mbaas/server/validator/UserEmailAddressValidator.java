package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/3/16.
 */
public class UserEmailAddressValidator extends JooqValidator<String> {

    private final String applicationCode;

    private String applicationUserId;

    public UserEmailAddressValidator(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public UserEmailAddressValidator(String applicationCode, String applicationUserId) {
        this.applicationCode = applicationCode;
        this.applicationUserId = applicationUserId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String emailAddress = validatable.getValue();
        if (emailAddress != null && !"".equals(emailAddress)) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
            int count = 0;
            if (applicationUserId == null || "".equals(applicationUserId)) {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.APPLICATION_USER + " WHERE " + Jdbc.ApplicationUser.EMAIL_ADDRESS + " = ?", int.class, emailAddress);
            } else {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.APPLICATION_USER + " WHERE " + Jdbc.ApplicationUser.EMAIL_ADDRESS + " = ? AND " + Jdbc.ApplicationUser.APPLICATION_USER_ID + " != ?", int.class, emailAddress, this.applicationUserId);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
