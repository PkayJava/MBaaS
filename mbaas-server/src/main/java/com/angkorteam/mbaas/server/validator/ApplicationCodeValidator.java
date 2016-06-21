package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.jooq.DSLContext;

import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 3/3/16.
 */
public class ApplicationCodeValidator extends JooqValidator<String> {

    private String applicationId;

    private static final List<Character> CODES = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

    public ApplicationCodeValidator() {
    }

    public ApplicationCodeValidator(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String code = validatable.getValue();
        if (code != null && !"".equals(code)) {
            for (int index = 0; index < code.length(); index++) {
                Character character = code.charAt(index);
                if (!CODES.contains(character)) {
                    validatable.error(new ValidationError(this, "format"));
                    return;
                }
            }

            if ("root".equalsIgnoreCase(code)) {
                validatable.error(new ValidationError(this, "duplicated"));
                return;
            }

            Application application = ApplicationUtils.getApplication();
            DbSupport dbSupport = application.getDbSupport();
            Schema schema = dbSupport.getSchema(code);
            boolean exists = false;
            boolean error = true;
            while (error) {
                try {
                    exists = schema.exists();
                    error = false;
                } catch (FlywayException e) {
                }
            }
            if (exists) {
                validatable.error(new ValidationError(this, "duplicated"));
                return;
            }

            ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
            DSLContext context = getDSLContext();
            int count;
            if (applicationId == null || "".equals(applicationId)) {
                count = context.selectCount().from(applicationTable).where(applicationTable.CODE.eq(code)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(applicationTable).where(applicationTable.CODE.eq(code)).and(applicationTable.APPLICATION_ID.ne(this.applicationId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
