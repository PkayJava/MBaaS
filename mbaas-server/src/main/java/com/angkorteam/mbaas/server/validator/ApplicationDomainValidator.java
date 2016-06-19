package com.angkorteam.mbaas.server.validator;

import com.angkorteam.framework.extension.share.validation.JooqValidator;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.HostnameTable;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 6/19/16.
 */
public class ApplicationDomainValidator extends JooqValidator<String> {

    private final String domain;

    private static final List<Character> CODES = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

    public ApplicationDomainValidator(String domain) {
        this.domain = domain;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        String fqdn = validatable.getValue();
        if (fqdn.length() > this.domain.length()) {
            String domain = fqdn.substring(fqdn.length() - this.domain.length());
            if (!domain.equals(this.domain)) {
                ValidationError error = new ValidationError(this, "invalid");
                validatable.error(error);
            } else {
                String hostname = fqdn.substring(0, fqdn.length() - this.domain.length());
                for (int index = 0; index < hostname.length(); index++) {
                    Character character = hostname.charAt(index);
                    if (!CODES.contains(character)) {
                        validatable.error(new ValidationError(this, "invalid"));
                        return;
                    }
                }
                DSLContext context = getDSLContext();
                HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
                int count = context.selectCount().from(hostnameTable).where(hostnameTable.FQDN.eq(fqdn)).fetchOneInto(int.class);
                if (count > 0) {
                    validatable.error(new ValidationError(this, "invalid"));
                    return;
                }
            }
        } else {
            ValidationError error = new ValidationError(this, "invalid");
            validatable.error(error);
        }
    }

}
