//package com.angkorteam.mbaas.server.validator;
//
//import com.angkorteam.framework.extension.share.validation.JooqValidator;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.MbaasUserTable;
//import org.apache.wicket.validation.IValidatable;
//import org.apache.wicket.validation.ValidationError;
//import org.jooq.DSLContext;
//
///**
// * Created by socheat on 3/3/16.
// */
//public class MBaaSUserEmailAddressValidator extends JooqValidator<String> {
//
//    private String mbaasUserId;
//
//    public MBaaSUserEmailAddressValidator() {
//    }
//
//    public MBaaSUserEmailAddressValidator(String mbaasUserId) {
//        this.mbaasUserId = mbaasUserId;
//    }
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        String emailAddress = validatable.getValue();
//        if (emailAddress != null && !"".equals(emailAddress)) {
//            MbaasUserTable userTable = Tables.MBAAS_USER.as("userTable");
//            DSLContext context = getDSLContext();
//            int count = 0;
//            if (mbaasUserId == null || "".equals(mbaasUserId)) {
//                count = context.selectCount().from(userTable).where(userTable.EMAIL_ADDRESS.eq(emailAddress)).fetchOneInto(int.class);
//            } else {
//                count = context.selectCount().from(userTable).where(userTable.EMAIL_ADDRESS.eq(emailAddress)).and(userTable.MBAAS_USER_ID.ne(this.mbaasUserId)).fetchOneInto(int.class);
//            }
//            if (count > 0) {
//                validatable.error(new ValidationError(this, "duplicated"));
//            }
//        }
//    }
//}
