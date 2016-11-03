package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.server.Application;
import com.angkorteam.mbaas.server.Spring;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/3/16.
 */
public class QueryNameValidator implements IValidator<String> {

    private String queryId;

    public QueryNameValidator() {
    }

    public QueryNameValidator(String queryId) {
        this.queryId = queryId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            if (!Application.CHARACTERS.contains(name.charAt(0))) {
                validatable.error(new ValidationError(this, "format"));
                return;
            }
            if (name.length() > 1) {
                for (int i = 1; i < name.length(); i++) {
                    char ch = name.charAt(i);
                    if (ch != '_' && !Application.CHARACTERS.contains(ch) && !Application.NUMBERS.contains(ch)) {
                        validatable.error(new ValidationError(this, "format"));
                        return;
                    }
                }
            }
            DSLContext context = Spring.getBean(DSLContext.class);
            QueryTable queryTable = Tables.QUERY.as("queryTable");
            int count = 0;
            if (queryId == null || "".equals(queryId)) {
                count = context.selectCount().from(queryTable).where(queryTable.NAME.eq(name)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(queryTable).where(queryTable.NAME.eq(name)).and(queryTable.QUERY_ID.notEqual(this.queryId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
