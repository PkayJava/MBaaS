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
public class QueryNameValidator extends JooqValidator<String> {

    private final String applicationCode;

    private String queryId;

    public QueryNameValidator(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public QueryNameValidator(String applicationCode, String queryId) {
        this.queryId = queryId;
        this.applicationCode = applicationCode;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
            int count = 0;
            if (queryId == null || "".equals(queryId)) {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.QUERY + " WHERE " + Jdbc.Query.NAME + " = ?", int.class, name);
            } else {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.QUERY + " WHERE " + Jdbc.Query.NAME + " = ? AND " + Jdbc.Query.QUERY_ID + " != ?", int.class, name, this.queryId);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
