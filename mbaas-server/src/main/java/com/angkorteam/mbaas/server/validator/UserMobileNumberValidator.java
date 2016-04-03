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
public class UserMobileNumberValidator extends JooqValidator<String> {

    private String userId;

    public UserMobileNumberValidator() {
    }

    public UserMobileNumberValidator(String userId) {
        this.userId = userId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String mobileNumber = validatable.getValue();
        if (mobileNumber != null && !"".equals(mobileNumber)) {
            UserTable userTable = Tables.USER.as("userTable");
            DSLContext context = getDSLContext();
            int count = 0;
            if (userId == null || "".equals(userId)) {
                count = context.selectCount().from(userTable).where(userTable.MOBILE_NUMBER.eq(mobileNumber)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(userTable).where(userTable.MOBILE_NUMBER.eq(mobileNumber)).and(userTable.USER_ID.ne(this.userId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
