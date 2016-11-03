//package com.angkorteam.mbaas.server.validator;
//
//import com.angkorteam.framework.extension.share.validation.JooqValidator;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import org.apache.wicket.validation.IValidatable;
//import org.apache.wicket.validation.ValidationError;
//import org.springframework.jdbc.core.JdbcTemplate;
//
///**
// * Created by socheat on 3/3/16.
// */
//public class UserMobileNumberValidator extends JooqValidator<String> {
//
//    private String applicationUserId;
//
//    private final String applicationCode;
//
//    public UserMobileNumberValidator(String applicationCode) {
//        this.applicationCode = applicationCode;
//    }
//
//    public UserMobileNumberValidator(String applicationCode, String applicationUserId) {
//        this.applicationUserId = applicationUserId;
//        this.applicationCode = applicationCode;
//    }
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        String mobileNumber = validatable.getValue();
//        if (mobileNumber != null && !"".equals(mobileNumber)) {
//            Application application = ApplicationUtils.getApplication();
//            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//            int count = 0;
//            if (applicationUserId == null || "".equals(applicationUserId)) {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.USER + " WHERE " + Jdbc.User.MOBILE_NUMBER + " = ?", int.class, mobileNumber);
//            } else {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.USER + " WHERE " + Jdbc.User.MOBILE_NUMBER + " = ? AND " + Jdbc.User.USER_ID + " != ?", int.class, mobileNumber, this.applicationUserId);
//            }
//            if (count > 0) {
//                validatable.error(new ValidationError(this, "duplicated"));
//            }
//        }
//    }
//}
