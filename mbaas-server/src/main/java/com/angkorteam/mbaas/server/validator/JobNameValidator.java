package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 4/24/16.
 */
public class JobNameValidator extends JooqValidator<String> {

    private String jobId;

    public JobNameValidator() {
    }

    public JobNameValidator(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            DSLContext context = getDSLContext();
            int count = 0;
            if (jobId == null || "".equals(jobId)) {
                count = context.selectCount().from(Tables.JOB).where(Tables.JOB.NAME.eq(name)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(Tables.JOB).where(Tables.JOB.NAME.eq(name)).and(Tables.JOB.JOB_ID.ne(this.jobId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }

}
