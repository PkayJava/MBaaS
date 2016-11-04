package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.base.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 11/3/16.
 */
public class RestNameValidator implements IValidator<String> {

    private String restId;

    public RestNameValidator() {
    }

    public RestNameValidator(String restId) {
        this.restId = restId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (!Strings.isNullOrEmpty(name)) {
            DSLContext context = Spring.getBean(DSLContext.class);
            RestTable restTable = Tables.REST.as("restTable");
            int count = 0;
            if (Strings.isNullOrEmpty(this.restId)) {
                count = context.selectCount().from(restTable).where(restTable.NAME.eq(name)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(restTable).where(restTable.NAME.eq(name)).and(restTable.REST_ID.notEqual(this.restId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }

}
