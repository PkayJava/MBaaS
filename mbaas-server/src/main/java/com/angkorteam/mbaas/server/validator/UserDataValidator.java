//package com.angkorteam.mbaas.server.validator;
//
//import com.angkorteam.framework.extension.share.validation.JooqValidator;
//import com.google.gson.Gson;
//import org.apache.wicket.validation.IValidatable;
//import org.apache.wicket.validation.ValidationError;
//
//import java.util.Map;
//
///**
// * Created by socheat on 4/10/16.
// */
//public class UserDataValidator extends JooqValidator<String> {
//
//    @Override
//    public void validate(IValidatable<String> validatable) {
//        String userData = validatable.getValue();
//        if (userData != null && !"".equals(userData)) {
//            try {
//                Gson gson = new Gson();
//                gson.fromJson(userData, Map.class);
//            } catch (Throwable e) {
//                validatable.error(new ValidationError(this, "format"));
//            }
//        }
//    }
//}
