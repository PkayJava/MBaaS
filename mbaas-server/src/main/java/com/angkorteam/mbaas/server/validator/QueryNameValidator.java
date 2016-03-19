package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/3/16.
 */
public class QueryNameValidator extends JooqValidator<String> {

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
            DSLContext context = getDSLContext();
            int count = 0;
            if (queryId == null || "".equals(queryId)) {
                count = context.selectCount().from(Tables.QUERY).where(Tables.QUERY.NAME.eq(name)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(Tables.QUERY).where(Tables.QUERY.NAME.eq(name)).and(Tables.QUERY.QUERY_ID.ne(this.queryId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
