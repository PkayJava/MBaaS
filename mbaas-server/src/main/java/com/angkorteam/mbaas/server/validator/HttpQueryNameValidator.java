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
//public class HttpQueryNameValidator implements IValidator<String> {
//
//    private String httpQueryId;
//    private String applicationCode;
//
//    public HttpQueryNameValidator(String applicationCode) {
//        this.applicationCode = applicationCode;
//    }
//
//    public HttpQueryNameValidator(String applicationCode, String httpQueryId) {
//        this.applicationCode = applicationCode;
//        this.httpQueryId = httpQueryId;
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
//            if (this.httpQueryId == null || "".equals(this.httpQueryId)) {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.HTTP_QUERY + " WHERE " + Jdbc.HttpQuery.NAME + " = ?", int.class, name);
//            } else {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.HTTP_QUERY + " WHERE " + Jdbc.HttpQuery.NAME + " = ? AND " + Jdbc.HttpQuery.HTTP_QUERY_ID + " != ?", int.class, name, this.httpQueryId);
//            }
//            if (count > 0) {
//                validatable.error(new ValidationError(this, "duplicated"));
//            }
//        }
//    }
//}
