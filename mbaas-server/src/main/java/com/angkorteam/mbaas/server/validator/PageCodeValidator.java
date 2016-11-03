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
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Created by socheat on 6/14/16.
// */
//public class PageCodeValidator extends JooqValidator<String> {
//
//    private static final List<Character> CODES = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '_');
//
//    private final String applicationCode;
//
//    public PageCodeValidator(String applicationCode) {
//        this.applicationCode = applicationCode;
//    }
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        String code = validatable.getValue();
//        if (code != null && !"".equals(code)) {
//            for (int i = 0; i < code.length(); i++) {
//                Character character = code.charAt(i);
//                if (!CODES.contains(character)) {
//                    validatable.error(new ValidationError(this, "format"));
//                    return;
//                }
//            }
//
//            Application application = ApplicationUtils.getApplication();
//            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.CODE + " = ?", int.class, code);
//            if (count > 0) {
//                validatable.error(new ValidationError(this, "duplicated"));
//            }
//        }
//    }
//}
