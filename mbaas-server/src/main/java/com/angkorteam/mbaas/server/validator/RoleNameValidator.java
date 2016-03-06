package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/3/16.
 */
public class RoleNameValidator extends JooqValidator<String> {

    private String roleId;

    public RoleNameValidator() {
    }

    public RoleNameValidator(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            RoleTable roleTable = Tables.ROLE.as("roleTable");
            DSLContext context = getDSLContext();
            int count = 0;
            if (roleId == null || "".equals(roleId)) {
                count = context.selectCount().from(roleTable).where(roleTable.NAME.eq(name)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(roleTable).where(roleTable.NAME.eq(name)).and(roleTable.ROLE_ID.ne(this.roleId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
