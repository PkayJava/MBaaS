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
//public class JsonNameValidator implements IValidator<String> {
//
//    private String jsonId;
//    private String applicationCode;
//
//    public JsonNameValidator(String applicationCode) {
//        this.applicationCode = applicationCode;
//    }
//
//    public JsonNameValidator(String applicationCode, String jsonId) {
//        this.applicationCode = applicationCode;
//        this.jsonId = jsonId;
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
//            if (this.jsonId == null || "".equals(this.jsonId)) {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.NAME + " = ?", int.class, name);
//            } else {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.JSON + " WHERE " + Jdbc.Json.NAME + " = ? AND " + Jdbc.Json.JSON_ID + " != ?", int.class, name, this.jsonId);
//            }
//            if (count > 0) {
//                validatable.error(new ValidationError(this, "duplicated"));
//            }
//        }
//    }
//}
