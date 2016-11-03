//package com.angkorteam.mbaas.server.validator;
//
//import com.angkorteam.framework.extension.share.validation.JooqValidator;
//import com.angkorteam.mbaas.configuration.Constants;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import org.apache.wicket.validation.IValidatable;
//import org.apache.wicket.validation.ValidationError;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.regex.Pattern;
//
///**
// * Created by socheat on 3/3/16.
// */
//public class JavascriptPathValidator extends JooqValidator<String> {
//
//    private final String applicationCode;
//
//    private String javascriptId;
//
//    public JavascriptPathValidator(String applicationCode) {
//        this.applicationCode = applicationCode;
//    }
//
//    public JavascriptPathValidator(String applicationCode, String javascriptId) {
//        this.applicationCode = applicationCode;
//        this.javascriptId = javascriptId;
//    }
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        String path = validatable.getValue();
//        if (path != null && !"".equals(path)) {
//            Pattern patternNaming = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_PATH));
//            if (!patternNaming.matcher(path).matches()) {
//                validatable.error(new ValidationError(this, "format"));
//            } else {
//                Application application = ApplicationUtils.getApplication();
//                JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//                int count = 0;
//                if (javascriptId == null || "".equals(javascriptId)) {
//                    count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM javascript WHERE path = ?", int.class, path);
//                } else {
//                    count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM javascript WHERE path = ? AND javascript_id != ?", int.class, path, this.javascriptId);
//                }
//                if (count > 0) {
//                    validatable.error(new ValidationError(this, "duplicated"));
//                }
//            }
//        }
//    }
//}
