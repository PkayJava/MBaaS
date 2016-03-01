package com.angkorteam.mbaas.server.api;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.plain.enums.ScopeEnum;
import com.angkorteam.mbaas.plain.request.*;
import com.angkorteam.mbaas.plain.request.user.*;
import com.angkorteam.mbaas.plain.response.Response;
import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.angkorteam.mbaas.plain.response.user.*;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.*;

/**
 * Created by Khauv Socheat on 2/14/2016.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private StringEncryptor encryptor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private Gson gson;

    @Autowired
    private TaskExecutor executor;

    @Autowired
    private TaskScheduler scheduler;

    @RequestMapping(
            method = RequestMethod.POST, path = "/admin/suspend/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserSuspendResponse> suspend(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username,
            @RequestBody UserSuspendRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);

        if (userRecord == null) {
            errorMessages.put(username, "is not found");
        }

        if (!errorMessages.isEmpty()) {
            UserSuspendResponse response = new UserSuspendResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        userRecord.setAccountNonLocked(false);
        userRecord.update();

        UserSuspendResponse responseBody = new UserSuspendResponse();
        responseBody.setData(userRecord.getUserId());

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/admin/activate/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> activate(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username,
            @RequestBody Request requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        UnknownResponse responseBody = new UnknownResponse();

        UserTable userTable = Tables.USER.as("userTable");
        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);

        if (userRecord == null) {
            errorMessages.put(username, "is not found");
        }

        if (!errorMessages.isEmpty()) {
            UserSuspendResponse response = new UserSuspendResponse();
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.getErrorMessages().putAll(errorMessages);
            return ResponseEntity.ok(response);
        }

        userRecord.setAccountNonLocked(true);
        userRecord.update();

        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/retrieve/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserRetrieveResponse> fetchUserProfile(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username,
            @RequestBody UserRetrieveRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        UserTable userTable = Tables.USER.as("userTable");

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);

        UserRetrieveResponse response = new UserRetrieveResponse();
        response.setData(userRecord.getUserId());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/query",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserQueryResponse> fetchUsers(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody UserQueryRequest requestBody
    ) {
        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
        Map<String, String> errorMessages = new LinkedHashMap<>();

        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

        UnknownResponse responseBody = new UnknownResponse();

        List<String> columnNames = new LinkedList<>();
        Map<String, Object> columnValues = new LinkedHashMap<>();

        List<UserRecord> userRecords = context.select(userTable.fields()).from(userTable).fetchInto(userTable);

        UserQueryResponse response = new UserQueryResponse();

        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/password/reset/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserPasswordResetResponse> passwordReset(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username,
            @RequestBody UserPasswordResetRequest requestBody
    ) {
        UserTable userTable = Tables.USER.as("userTable");

        String uuid = UUID.randomUUID().toString();

        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.LOGIN.eq(username)).fetchOneInto(userTable);
        DateTime now = new DateTime();
        userRecord.setPasswordResetToken(uuid);
        userRecord.setPasswordResetTokenExpiredDate(now.plusMinutes(10).toDate());
        userRecord.update();
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(configuration.getString(Constants.MAIL_FROM));
        message.setSubject("Transaction ID => " + uuid);
        message.setText("Dear Sir/Madam just reply to this email, password will be reset to first line of the email");
        message.setTo(userRecord.getLogin());
        executor.execute(() -> mailSender.send(message));

        UserPasswordResetResponse response = new UserPasswordResetResponse();

        return ResponseEntity.ok(response);
    }
}
