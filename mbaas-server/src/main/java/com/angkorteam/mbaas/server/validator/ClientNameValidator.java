package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ClientTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

/**
 * Created by socheat on 3/26/16.
 */
public class ClientNameValidator extends JooqValidator<String> {

    private String applicationId;

    private String clientId;

    public ClientNameValidator(String applicationId) {
        this.applicationId = applicationId;
    }

    public ClientNameValidator(String applicationId, String clientId) {
        this.applicationId = applicationId;
        this.clientId = clientId;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String name = validatable.getValue();
        if (name != null && !"".equals(name)) {
            ClientTable clientTable = Tables.CLIENT.as("clientTable");

            DSLContext context = getDSLContext();
            int count = 0;
            if (this.clientId == null || "".equals(this.clientId)) {
                count = context.selectCount().from(clientTable).where(clientTable.NAME.eq(name)).and(clientTable.APPLICATION_ID.eq(this.applicationId)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(clientTable).where(clientTable.NAME.eq(name)).and(clientTable.APPLICATION_ID.eq(this.applicationId)).and(clientTable.CLIENT_ID.ne(clientId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                validatable.error(new ValidationError(this, "duplicated"));
            }
        }
    }
}
