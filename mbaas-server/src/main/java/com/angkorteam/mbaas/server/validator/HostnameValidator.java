//package com.angkorteam.mbaas.server.validator;
//
//import com.angkorteam.framework.extension.share.validation.JooqValidator;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.HostnameTable;
//import org.apache.wicket.validation.IValidatable;
//import org.apache.wicket.validation.ValidationError;
//import org.jooq.DSLContext;
//
///**
// * Created by socheat on 6/13/16.
// */
//public class HostnameValidator extends JooqValidator<String> {
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        DSLContext context = getDSLContext();
//        String hostname = validatable.getValue();
//        if (hostname != null && !"".equals(hostname)) {
//            HostnameTable hostnameTable = Tables.HOSTNAME.as("hostnameTable");
//            int count = context.selectCount().from(hostnameTable).where(hostnameTable.FQDN.eq(hostname)).fetchOneInto(int.class);
//            if (count == 0) {
//                validatable.error(new ValidationError(this, "invalid"));
//            }
//        }
//    }
//
//}
