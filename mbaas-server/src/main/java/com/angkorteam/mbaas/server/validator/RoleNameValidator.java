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
//public class RoleNameValidator extends JooqValidator<String> {
//
//    private String roleId;
//
//    private final String applicationCode;
//
//    public RoleNameValidator(String applicationCode) {
//        this.applicationCode = applicationCode;
//    }
//
//    public RoleNameValidator(String applicationCode, String roleId) {
//        this.roleId = roleId;
//        this.applicationCode = applicationCode;
//    }
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        String name = validatable.getValue();
//        if (name != null && !"".equals(name)) {
//            Application application = ApplicationUtils.getApplication();
//            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//            int count = 0;
//            if (roleId == null || "".equals(roleId)) {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.NAME + " = ?", int.class, name);
//            } else {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.ROLE + " WHERE " + Jdbc.Role.NAME + " = ? AND " + Jdbc.Role.ROLE_ID + " != ?", int.class, name, this.roleId);
//            }
//            if (count > 0) {
//                validatable.error(new ValidationError(this, "duplicated"));
//            }
//        }
//    }
//}
