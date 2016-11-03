//package com.angkorteam.mbaas.server.validator;
//
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import org.apache.wicket.validation.IValidatable;
//import org.apache.wicket.validation.IValidator;
//import org.apache.wicket.validation.ValidationError;
//import org.springframework.jdbc.core.JdbcTemplate;
//
///**
// * Created by socheat on 8/6/16.
// */
//public class EnumNameValidator implements IValidator<String> {
//
//    private String enumId;
//    private String applicationCode;
//
//    public EnumNameValidator(String applicationCode) {
//        this.applicationCode = applicationCode;
//    }
//
//    public EnumNameValidator(String applicationCode, String enumId) {
//        this.applicationCode = applicationCode;
//        this.enumId = enumId;
//    }
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        String name = validatable.getValue();
//        if (name != null && !"".equals(name)) {
//            Application application = ApplicationUtils.getApplication();
//            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//
//            int count = 0;
//            if (this.enumId == null || "".equals(this.enumId)) {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.NAME + " = ?", int.class, name);
//            } else {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.ENUM + " WHERE " + Jdbc.Enum.NAME + " = ? AND " + Jdbc.Enum.ENUM_ID + " != ?", int.class, name, this.enumId);
//            }
//            if (count > 0) {
//                validatable.error(new ValidationError(this, "duplicated"));
//            }
//        }
//    }
//}
