package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 4/24/16.
 */
public class JobNameValidator extends JooqValidator<String> {

    private final String applicationCode;

    private String jobId;

    public JobNameValidator(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public JobNameValidator(String applicationCode, String jobId) {
        this.applicationCode = applicationCode;
        this.jobId = jobId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
            int count = 0;
            if (jobId == null || "".equals(jobId)) {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.JOB + " WHERE " + Jdbc.Job.NAME + " = ?", int.class, name);
            } else {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.JOB + " WHERE " + Jdbc.Job.NAME + " = ? AND " + Jdbc.Job.JOB_ID + " != ?", int.class, name, this.jobId);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }

}
