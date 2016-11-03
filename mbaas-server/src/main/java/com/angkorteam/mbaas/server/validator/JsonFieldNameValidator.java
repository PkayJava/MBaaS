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
//public class JsonFieldNameValidator implements IValidator<String> {
//
//    private String jsonId;
//    private String jsonFieldId;
//    private String applicationCode;
//
//    public JsonFieldNameValidator(String applicationCode, String jsonId) {
//        this.applicationCode = applicationCode;
//        this.jsonId = jsonId;
//    }
//
//    public JsonFieldNameValidator(String applicationCode, String jsonId, String jsonFieldId) {
//        this.applicationCode = applicationCode;
//        this.jsonFieldId = jsonFieldId;
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
//            if (this.jsonFieldId == null || "".equals(this.jsonFieldId)) {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ? AND " + Jdbc.JsonField.NAME + " = ?", int.class, this.jsonId, name);
//            } else {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.JSON_FIELD + " WHERE " + Jdbc.JsonField.JSON_ID + " = ? AND " + Jdbc.JsonField.JSON_FIELD_ID + " != ? AND " + Jdbc.JsonField.NAME + " = ?", int.class, this.jsonId, this.jsonFieldId, name);
//            }
//            if (count > 0) {
//                validatable.error(new ValidationError(this, "duplicated"));
//                return;
//            }
//        }
//    }
//}
