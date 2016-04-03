package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/3/16.
 */
public class UserEmailAddressValidator extends JooqValidator<String> {

    private String userId;

    public UserEmailAddressValidator() {
    }

    public UserEmailAddressValidator(String userId) {
        this.userId = userId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String emailAddress = validatable.getValue();
        if (emailAddress != null && !"".equals(emailAddress)) {
            UserTable userTable = Tables.USER.as("userTable");
            DSLContext context = getDSLContext();
            int count = 0;
            if (userId == null || "".equals(userId)) {
                count = context.selectCount().from(userTable).where(userTable.EMAIL_ADDRESS.eq(emailAddress)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(userTable).where(userTable.EMAIL_ADDRESS.eq(emailAddress)).and(userTable.USER_ID.ne(this.userId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
