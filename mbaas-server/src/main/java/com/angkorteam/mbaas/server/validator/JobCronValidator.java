package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.springframework.scheduling.support.CronSequenceGenerator;

/**
 * Created by socheat on 4/24/16.
 */
public class JobCronValidator extends JooqValidator<String> {

    private String jobId;

    public JobCronValidator() {
    }

    public JobCronValidator(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String cron = validatable.getValue();
        if (cron != null && !"".equals(cron)) {
            try {
                CronSequenceGenerator generator = new CronSequenceGenerator(cron);
            } catch (IllegalArgumentException e) {
                validatable.error(new ValidationError(this, "format"));
            }
        }
    }

}
