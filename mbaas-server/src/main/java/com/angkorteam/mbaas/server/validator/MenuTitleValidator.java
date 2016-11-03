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
// * Created by socheat on 4/24/16.
// */
//public class MenuTitleValidator extends JooqValidator<String> {
//
//    private final String applicationCode;
//
//    private String menuId;
//
//    public MenuTitleValidator(String applicationCode) {
//        this.applicationCode = applicationCode;
//    }
//
//    public MenuTitleValidator(String applicationCode, String menuId) {
//        this.applicationCode = applicationCode;
//        this.menuId = menuId;
//    }
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        String title = validatable.getValue();
//        if (title != null && !"".equals(title)) {
//            Application application = ApplicationUtils.getApplication();
//            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
//            int count = 0;
//            if (menuId == null || "".equals(menuId)) {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.TITLE + " = ?", int.class, title);
//            } else {
//                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.MENU + " WHERE " + Jdbc.Menu.TITLE + " = ? AND " + Jdbc.Menu.MENU_ID + " != ?", int.class, title, this.menuId);
//            }
//            if (count > 0) {
//                validatable.error(new ValidationError(this, "duplicated"));
//            }
//        }
//    }
//
//}
