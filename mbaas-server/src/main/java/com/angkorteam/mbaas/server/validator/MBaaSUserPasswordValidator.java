//package com.angkorteam.mbaas.server.validator;
//
//import com.angkorteam.framework.extension.share.validation.JooqValidator;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import org.apache.wicket.validation.IValidatable;
//import org.apache.wicket.validation.ValidationError;
//import org.springframework.jdbc.core.JdbcTemplate;
//
///**
// * Created by socheat on 3/8/16.
// */
//public class MBaaSUserPasswordValidator extends JooqValidator<String> {
//
//    private String mbaasUserId;
//
//    public MBaaSUserPasswordValidator(String mbaasUserId) {
//        this.mbaasUserId = mbaasUserId;
//    }
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        String password = validatable.getValue();
//        if (password != null && !"".equals(password)) {
//            Application application = ApplicationUtils.getApplication();
//            JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
//            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Tables.MBAAS_USER.getName() + " WHERE " + Tables.MBAAS_USER.MBAAS_USER_ID.getName() + " = ? AND " + Tables.MBAAS_USER.PASSWORD.getName() + " = MD5(?)", int.class, this.mbaasUserId, password);
//            if (count == 0) {
//                validatable.error(new ValidationError(this, "invalid"));
//            }
//        }
//    }
//}
