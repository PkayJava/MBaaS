package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.request.Request;
import com.angkorteam.mbaas.plain.request.security.SecurityLoginRequest;
import com.angkorteam.mbaas.plain.request.security.SecurityLogoutRequest;
import com.angkorteam.mbaas.plain.request.security.SecuritySignUpRequest;
import com.angkorteam.mbaas.plain.response.security.SecurityLoginResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutSessionResponse;
import com.angkorteam.mbaas.plain.response.security.SecuritySignUpResponse;
import com.angkorteam.mbaas.server.function.UserFunction;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Khauv Socheat on 2/14/2016.
 */
@Controller
@RequestMapping(path = "/security")
public class SecurityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Gson gson;

    //region /security/signup

    @RequestMapping(
            method = RequestMethod.POST, path = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecuritySignUpResponse> signup(
            HttpServletRequest request,
            @RequestBody SecuritySignUpRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        UserTable userTable = Tables.USER.as("userTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        ClientTable clientTable = Tables.CLIENT.as("clientTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");

        ClientRecord clientRecord = null;
        if (requestBody.getSecret() == null || "".equals(requestBody.getSecret())) {
            errorMessages.put("secret", "is request");
        } else {
            clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.SECRET.eq(requestBody.getSecret())).fetchOneInto(clientTable);
        }
        if (clientRecord == null) {
            errorMessages.put("secret", "is bad");
        }
        ApplicationRecord applicationRecord = null;
        if (clientRecord != null) {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        }

        if (applicationRecord == null) {
            errorMessages.put("secret", "is bad");
        }

        if (applicationRecord != null) {
            if (applicationRecord.getAutoRegistration()) {
                if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
                    requestBody.setUsername(UUID.randomUUID().toString());
                }
                if (requestBody.getPassword() == null || "".equals(requestBody.getPassword())) {
                    requestBody.setPassword(UUID.randomUUID().toString());
                }
            }
            if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
                errorMessages.put("username", "is required");
            } else {
                int count = context.selectCount().from(userTable).where(userTable.LOGIN.eq(requestBody.getUsername())).fetchOneInto(Integer.class);
                if (count > 0) {
                    errorMessages.put("username", "is not available");
                }
            }
        }

        if (requestBody.getPassword() == null || "".equals(requestBody.getPassword())) {
            errorMessages.put("password", "is required");
        }

        // begin field duplication check
        Map<String, Object> fields = new LinkedHashMap<>();
        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
            requestBody.getVisibleByAnonymousUsers().entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
                if (fields.containsKey(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "overridden other field");
                } else {
                    fields.put(entry.getKey(), entry.getValue());
                }
            });
        }
        if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
            requestBody.getVisibleByFriends().entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
                if (fields.containsKey(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "overridden other field");
                } else {
                    fields.put(entry.getKey(), entry.getValue());
                }
            });
        }
        if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
            requestBody.getVisibleByRegisteredUsers().entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
                if (fields.containsKey(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "overridden other field");
                } else {
                    fields.put(entry.getKey(), entry.getValue());
                }
            });
        }
        if (requestBody.getVisibleByTheUser() != null && !requestBody.getVisibleByTheUser().isEmpty()) {
            requestBody.getVisibleByTheUser().entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
                if (fields.containsKey(entry.getKey())) {
                    errorMessages.put(entry.getKey(), "overridden other field");
                } else {
                    fields.put(entry.getKey(), entry.getValue());
                }
            });
        }
        // finish field duplication check
        Pattern patternAttributeName = Pattern.compile(Constants.getXmlPropertiesConfiguration().getString(Constants.PATTERN_ATTRIBUTE_NAME));
        for (Map.Entry<String, Object> field : fields.entrySet()) {
            String name = field.getKey();
            if (!patternAttributeName.matcher(name).matches()) {
                errorMessages.put(name, "bad name");
            }
        }

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(collectionTable);
        List<AttributeRecord> attributeRecords = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable);

        // begin field type validation
        for (AttributeRecord attributeRecord : attributeRecords) {
            Object object = fields.get(attributeRecord.getName());
            if (object != null) {
                AttributeTypeEnum attributeTypeEnum = AttributeTypeEnum.valueOf(attributeRecord.getJavaType());
                if (attributeTypeEnum == AttributeTypeEnum.Boolean) {
                    if (object instanceof Boolean) {
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be boolean");
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.Byte) {
                    if (object instanceof Byte) {
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be byte");
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.Short) {
                    if (object instanceof Short) {
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be short");
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.Integer) {
                    if (object instanceof Integer) {
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be integer");
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.Long) {
                    if (object instanceof Long) {
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be long");
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.Float) {
                    if (object instanceof Float) {
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be float");
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.Double) {
                    if (object instanceof Double) {
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be double");
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.Character) {
                    if (object instanceof Character) {
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be char or character");
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.String) {
                    if (object instanceof String) {
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be string");
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.Time) {
                    if (object instanceof Date) {
                    } else if (object instanceof String) {
                        DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_TIME));
                        Date value = null;
                        try {
                            value = dateFormat.parse((String) object);
                        } catch (ParseException e) {
                            dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                            try {
                                value = dateFormat.parse((String) object);
                            } catch (ParseException e1) {
                            }
                        }
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be date or string format " + configuration.getString(Constants.PATTERN_TIME));
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.Date) {
                    if (object instanceof Date) {
                    } else if (object instanceof String) {
                        DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATE));
                        Date value = null;
                        try {
                            value = dateFormat.parse((String) object);
                        } catch (ParseException e) {
                            dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                            try {
                                value = dateFormat.parse((String) object);
                            } catch (ParseException e1) {
                            }
                        }
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be date or string format " + configuration.getString(Constants.PATTERN_DATE));
                    }
                } else if (attributeTypeEnum == AttributeTypeEnum.DateTime) {
                    if (object instanceof Date) {
                    } else if (object instanceof String) {
                        DateFormat dateFormat = new SimpleDateFormat(configuration.getString(Constants.PATTERN_DATETIME));
                        Date value = null;
                        try {
                            value = dateFormat.parse((String) object);
                        } catch (ParseException e) {
                        }
                    } else {
                        errorMessages.put(attributeRecord.getName(), "data type must be date or string format " + configuration.getString(Constants.PATTERN_DATETIME));
                    }
                }
            }
        }
        fields.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
            if (entry.getValue() instanceof Boolean) {
            } else if (entry.getValue() instanceof Byte) {
            } else if (entry.getValue() instanceof Short) {
            } else if (entry.getValue() instanceof Integer) {
            } else if (entry.getValue() instanceof Long) {
            } else if (entry.getValue() instanceof Float) {
            } else if (entry.getValue() instanceof Double) {
            } else if (entry.getValue() instanceof Character) {
            } else if (entry.getValue() instanceof String) {
            } else if (entry.getValue() instanceof Date) {
            } else {
                errorMessages.put(entry.getKey(), "data type must be boolean, byte, short, integer, long, float, double, character, string, date ");
            }
        });
        // finish type validation

        if (!errorMessages.isEmpty()) {
            SecuritySignUpResponse response = new SecuritySignUpResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        String bearer = UserFunction.createUser(context, jdbcTemplate, request, requestBody);

        SecuritySignUpResponse responseBody = new SecuritySignUpResponse();

        responseBody.getData().setBearer(bearer);
        responseBody.getData().setLogin(requestBody.getUsername());

        return ResponseEntity.ok(responseBody);
    }

    //endregion

    //region /security/login

    @RequestMapping(
            method = RequestMethod.POST, path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecurityLoginResponse> login(
            HttpServletRequest request,
            @RequestBody SecurityLoginRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        UserTable userTable = Tables.USER.as("userTable");
        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        ClientTable clientTable = Tables.CLIENT.as("clientTable");

        ClientRecord clientRecord = null;
        if (requestBody.getSecret() == null || "".equals(requestBody.getSecret())) {
            errorMessages.put("secret", "is required");
        } else {
            clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.SECRET.eq(requestBody.getSecret())).fetchOneInto(clientTable);
        }
        if (clientRecord == null) {
            errorMessages.put("credential", "bad credential");
        }

        ApplicationRecord applicationRecord = null;
        if (clientRecord != null) {
            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
        }
        if (applicationRecord == null) {
            errorMessages.put("credential", "bad credential");
        }

        UserRecord userRecord = null;
        MobileRecord mobileRecord = null;
        if (applicationRecord != null) {
            if (applicationRecord.getAutoRegistration()) {
                if (requestBody.getDeviceToken() != null && !"".equals(requestBody.getDeviceToken())
                        && requestBody.getDeviceType() != null && !"".equals(requestBody.getDeviceType())) {
                    mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.DEVICE_TOKEN.eq(requestBody.getDeviceToken())).and(mobileTable.DEVICE_TYPE.eq(requestBody.getDeviceType())).fetchOneInto(mobileTable);
                    if (mobileRecord == null) {
                        mobileRecord = context.newRecord(mobileTable);
                        mobileRecord.setMobileId(UUID.randomUUID().toString());
                        mobileRecord.setDateCreated(new Date());
                        mobileRecord.setClientId(clientRecord.getClientId());
                        mobileRecord.setDeviceToken(requestBody.getDeviceToken());
                        mobileRecord.setApplicationId(clientRecord.getApplicationId());
                        mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
                        mobileRecord.setClientIp(request.getRemoteAddr());
                        mobileRecord.store();
                    }
                } else {
                    if (requestBody.getDeviceToken() == null || "".equals(requestBody.getDeviceToken())) {
                        errorMessages.put("deviceToken", "is required");
                    }
                    if (requestBody.getDeviceType() == null || "".equals(requestBody.getDeviceType())) {
                        errorMessages.put("deviceType", "is required");
                    }
                }
            } else {
                if (requestBody.getUsername() != null && !"".equals(requestBody.getUsername())
                        && requestBody.getPassword() != null && !"".equals(requestBody.getPassword())) {
                    List<Condition> where = new ArrayList<>();
                    where.add(userTable.LOGIN.eq(requestBody.getUsername()));
                    where.add(userTable.PASSWORD.eq(requestBody.getPassword()));
                    userRecord = context.select(userTable.fields()).from(userTable).where(where).fetchOneInto(userTable);
                    if (userRecord == null) {
                        errorMessages.put("credential", "bad credential");
                    } else {
                        mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.DEVICE_TOKEN.eq(requestBody.getDeviceToken())).and(mobileTable.DEVICE_TYPE.eq(requestBody.getDeviceType())).fetchOneInto(mobileTable);
                        if (mobileRecord == null) {
                            mobileRecord = context.newRecord(mobileTable);
                            mobileRecord.setMobileId(UUID.randomUUID().toString());
                            mobileRecord.setDateCreated(new Date());
                            mobileRecord.setClientId(clientRecord.getClientId());
                            mobileRecord.setDeviceToken(requestBody.getDeviceToken());
                            mobileRecord.setApplicationId(clientRecord.getApplicationId());
                            mobileRecord.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
                            mobileRecord.setClientIp(request.getRemoteAddr());
                            mobileRecord.store();
                        }
                    }
                } else {
                    if (requestBody.getUsername() == null || "".equals(requestBody.getUsername())) {
                        errorMessages.put("username", "is required");
                    }
                    if (requestBody.getPassword() == null || "".equals(requestBody.getPassword())) {
                        errorMessages.put("password", "is required");
                    }
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            SecurityLoginResponse response = new SecurityLoginResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        SecurityLoginResponse responseBody = new SecurityLoginResponse();

        if (mobileRecord != null) {
            responseBody.getData().setBearer(mobileRecord.getMobileId());
            responseBody.getData().setDateCreated(mobileRecord.getDateCreated());
        }
        if (userRecord != null) {
            responseBody.getData().setLogin(userRecord.getLogin());
        }

        return ResponseEntity.ok(responseBody);
    }

    //endregion

    //region /security/logout

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecurityLogoutResponse> logout(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
            @RequestBody SecurityLogoutRequest requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        SecurityLogoutResponse responseBody = new SecurityLogoutResponse();

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(session)).fetchOneInto(mobileTable);
        String userId = mobileRecord.getUserId();

        context.delete(mobileTable).where(mobileTable.USER_ID.eq(userId)).execute();

        return ResponseEntity.ok(responseBody);
    }

    //endregion

    //region /security/logout/{session}

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout/{session}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SecurityLogoutSessionResponse> logoutSession(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @PathVariable("session") String session,
            @RequestBody Request requestBody
    ) {
        LOGGER.info("{} appCode=>{} session=> body=>{}", request.getRequestURL(), appCode, request.getHeader("X-MBAAS-MOBILE"), gson.toJson(requestBody));

        SecurityLogoutSessionResponse responseBody = new SecurityLogoutSessionResponse();

        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        context.delete(mobileTable).where(mobileTable.MOBILE_ID.eq(session)).execute();

        return ResponseEntity.ok(responseBody);
    }

    //endregion

}
