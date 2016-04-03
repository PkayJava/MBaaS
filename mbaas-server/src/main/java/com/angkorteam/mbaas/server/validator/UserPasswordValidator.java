package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

/**
 * Created by socheat on 3/8/16.
 */
public class UserPasswordValidator extends JooqValidator<String> {

    private String userId;

    public UserPasswordValidator(String userId) {
        this.userId = userId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String password = validatable.getValue();
        if (password != null && !"".equals(password)) {
            UserTable userTable = Tables.USER.as("userTable");
            DSLContext context = getDSLContext();
            int count = context.selectCount().from(userTable).where(userTable.USER_ID.eq(userId)).and(userTable.PASSWORD.eq(DSL.md5(password))).fetchOneInto(int.class);
            if (count == 0) {
                validatable.error(new ValidationError(this, "invalid"));
            }
        }
    }
}
