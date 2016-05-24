package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/3/16.
 */
public class ApplicationCodeValidator extends JooqValidator<String> {

    private String applicationId;

    public ApplicationCodeValidator() {
    }

    public ApplicationCodeValidator(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String code = validatable.getValue();
        if (code != null && !"".equals(code)) {
            boolean error = false;
            for (int index = 0; index < code.length(); index++) {
                Character character = code.charAt(index);
                if (character == 'q' || character == 'w' || character == 'e' || character == 'r' || character == 't' || character == 'y' || character == 'u' || character == 'i' || character == 'o' || character == 'p'
                        || character == 'a' || character == 's' || character == 'd' || character == 'f' || character == 'g' || character == 'h' || character == 'j' || character == 'k' || character == 'l'
                        || character == 'z' || character == 'x' || character == 'c' || character == 'v' || character == 'b' || character == 'n' || character == 'm') {
                    continue;
                } else {
                    error = true;
                    break;
                }
            }
            if (!error) {
                Application application = ApplicationUtils.getApplication();
                DbSupport dbSupport = application.getDbSupport();
                if (dbSupport.getSchema(code).exists()) {
                    error = true;
                }
            }
            if (error) {
                validatable.error(new ValidationError(this, "format"));
            } else {
                ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
                DSLContext context = getDSLContext();
                int count = 0;
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
}
