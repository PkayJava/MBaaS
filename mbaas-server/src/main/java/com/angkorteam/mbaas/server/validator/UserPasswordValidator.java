package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 3/8/16.
 */
public class UserPasswordValidator extends JooqValidator<String> {

    private String applicationUserId;

    private final String applicationCode;

    public UserPasswordValidator(final String applicationCode, String applicationUserId) {
        this.applicationUserId = applicationUserId;
        this.applicationCode = applicationCode;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String password = validatable.getValue();
        if (password != null && !"".equals(password)) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.USER + " WHERE " + Jdbc.User.USER_ID + " = ? AND " + Jdbc.User.PASSWORD + " = MD5(?)", int.class, this.applicationUserId, password);
            if (count == 0) {
                validatable.error(new ValidationError(this, "invalid"));
            }
        }
    }
}
