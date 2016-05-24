//package com.angkorteam.mbaas.server.function;
//
//import com.angkorteam.mbaas.configuration.Constants;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
//import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
//import com.angkorteam.mbaas.model.entity.tables.RoleTable;
//import com.angkorteam.mbaas.model.entity.tables.UserTable;
//import com.angkorteam.mbaas.model.entity.tables.records.*;
//import com.angkorteam.mbaas.plain.enums.UserStatusEnum;
//import com.angkorteam.mbaas.plain.request.security.SecuritySignUpRequest;
//import org.apache.commons.configuration.XMLPropertiesConfiguration;
//import org.apache.commons.lang3.StringUtils;
//import org.jooq.DSLContext;
//import org.jooq.impl.DSL;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.*;
//
///**
// * Created by socheat on 3/16/16.
// */
//public class UserFunction {
//
//    public static boolean createUser(String userId, DSLContext context, JdbcTemplate jdbcTemplate, HttpServletRequest request, SecuritySignUpRequest requestBody) {
//        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
//        RoleTable roleTable = Tables.ROLE.as("roleTable");
//        UserTable userTable = Tables.USER.as("userTable");
//        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
//        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
//
//        // TODO
//        RoleRecord roleRecord = null;
////        roleRecord= context.select(roleTable.fields()).from(roleTable).where(roleTable.NAME.eq(configuration.getString(Constants.ROLE_REGISTERED))).fetchOneInto(roleTable);
//
//        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(collectionTable);
//        Map<String, AttributeRecord> attributeRecords = new HashMap<>();
//        for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
//            attributeRecords.put(attributeRecord.getName(), attributeRecord);
//        }
//
//        // remove null field and empty field
//        CommonFunction.cleanEmpty(requestBody.getVisibleByTheUser());
//        CommonFunction.cleanEmpty(requestBody.getVisibleByFriends());
//        CommonFunction.cleanEmpty(requestBody.getVisibleByRegisteredUsers());
//        CommonFunction.cleanEmpty(requestBody.getVisibleByAnonymousUsers());
//
//        // duplication checked
//        List<String> temp = new ArrayList<>();
//        boolean good = CommonFunction.checkDuplication(temp, requestBody.getVisibleByTheUser());
//        if (good) {
//            good = CommonFunction.checkDuplication(temp, requestBody.getVisibleByFriends());
//        }
//        if (good) {
//            good = CommonFunction.checkDuplication(temp, requestBody.getVisibleByRegisteredUsers());
//        }
//        if (good) {
//            good = CommonFunction.checkDuplication(temp, requestBody.getVisibleByAnonymousUsers());
//        }
//
//        Map<String, Object> eavExternalAttributes = new java.util.HashMap<>();
//        eavExternalAttributes.putAll(requestBody.getVisibleByTheUser());
//        eavExternalAttributes.putAll(requestBody.getVisibleByFriends());
//        eavExternalAttributes.putAll(requestBody.getVisibleByRegisteredUsers());
//        eavExternalAttributes.putAll(requestBody.getVisibleByAnonymousUsers());
//        if (good) {
//            // ensure attribute
//            good = CommonFunction.ensureAttributes(attributeRecords, eavExternalAttributes);
//        }
//
//        Map<String, Object> goodDocument = new java.util.HashMap<>();
//        if (good) {
//            // data type checked
//            good = CommonFunction.checkDataTypes(attributeRecords, eavExternalAttributes, goodDocument);
//        }
//
//        if (good) {
//
//            String login = requestBody.getUsername();
//            String password = requestBody.getPassword();
//
//            goodDocument.put(Tables.USER.ROLE_ID.getName(), roleRecord.getRoleId());
//            goodDocument.put(Tables.USER.ACCOUNT_NON_EXPIRED.getName(), true);
//            goodDocument.put(Tables.USER.CREDENTIALS_NON_EXPIRED.getName(), true);
//            goodDocument.put(Tables.USER.ACCOUNT_NON_LOCKED.getName(), true);
//            goodDocument.put(Tables.USER.STATUS.getName(), UserStatusEnum.Active.getLiteral());
//            goodDocument.put(Tables.USER.LOGIN.getName(), login);
//            goodDocument.put(Tables.USER.PASSWORD.getName(), password);
//            goodDocument.put(Tables.USER.USER_ID.getName(), userId);
//
//            List<String> fields = new LinkedList<>();
//            List<Object> values = new LinkedList<>();
//            Map<String, Object> eavs = new HashMap<>();
//            for (Map.Entry<String, Object> item : goodDocument.entrySet()) {
//                AttributeRecord attributeRecord = attributeRecords.get(item.getKey());
//                if (!attributeRecord.getEav()) {
//                    fields.add(item.getKey());
//                    values.add(":" + item.getKey());
//                } else {
//                    eavs.put(item.getKey(), item.getValue());
//                }
//            }
//            String jdbc = "INSERT INTO " + Tables.USER.getName() + "(" + StringUtils.join(fields, ",") + ") VALUES(" + StringUtils.join(values, ",") + ")";
//            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
//            template.update(jdbc, goodDocument);
//
//            context.update(userTable).set(userTable.PASSWORD, DSL.md5(password)).where(userTable.USER_ID.eq(userId)).execute();
//
//            if (!eavs.isEmpty()) {
//                CommonFunction.saveEavAttributes(collectionRecord.getCollectionId(), userId, context, attributeRecords, eavs);
//            }
//
//            if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
//                for (String name : requestBody.getVisibleByAnonymousUsers().keySet()) {
//                    String attributeId = attributeRecords.get(name).getAttributeId();
//                    VisibleByAnonymousRecord record = context.newRecord(Tables.VISIBLE_BY_ANONYMOUS);
//                    record.setVisibleByAnonymousId(UUID.randomUUID().toString());
//                    record.setAttributeId(attributeId);
//                    record.setUserId(userId);
//                    record.store();
//                }
//            }
//            if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
//                for (String name : requestBody.getVisibleByFriends().keySet()) {
//                    String attributeId = attributeRecords.get(name).getAttributeId();
//                    VisibleByFriendRecord record = context.newRecord(Tables.VISIBLE_BY_FRIEND);
//                    record.setVisibleByFriendId(UUID.randomUUID().toString());
//                    record.setAttributeId(attributeId);
//                    record.setUserId(userId);
//                    record.store();
//                }
//            }
//            if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
//                for (String name : requestBody.getVisibleByRegisteredUsers().keySet()) {
//                    String attributeId = attributeRecords.get(name).getAttributeId();
//                    VisibleByRegisteredUserRecord record = context.newRecord(Tables.VISIBLE_BY_REGISTERED_USER);
//                    record.setVisibleByRegisteredUserId(UUID.randomUUID().toString());
//                    record.setAttributeId(attributeId);
//                    record.setUserId(userId);
//                    record.store();
//                }
//            }
//        }
//
//        return good;
//    }
//
//
//}
