package com.angkorteam.mbaas.api;

import com.angkorteam.baasbox.sdk.java.json.*;
import com.angkorteam.baasbox.sdk.java.request.SendPushNotificationRequest;
import com.angkorteam.baasbox.sdk.java.response.StringResponse;
import com.angkorteam.baasbox.sdk.java.response.SuccessResponse;
import com.angkorteam.mbaas.request.*;
import com.angkorteam.mbaas.response.Response;
import com.google.gson.Gson;
import org.jasypt.encryption.StringEncryptor;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import retrofit.http.*;
import retrofit.mime.TypedFile;

import java.io.File;
import java.util.Map;

/**
 * Created by socheat on 2/4/16.
 */
@Controller
public class RestAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAPI.class);

    @Autowired
    private DSLContext context;

    @Autowired
    private StringEncryptor encryptor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Gson gson;

    @RequestMapping(
            method = RequestMethod.POST, path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> signup(
            @RequestBody SignupRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logout(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/logout/{pushToken}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> logout(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("pushToken") String pushToken,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me/suspend",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/user/suspend/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> suspendUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/user/activate/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> activateUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> loggedUserProfile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> updateUserProfile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody UpdateUserProfileRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUserProfile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchUsers(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody FetchUsersRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me/password",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changePassword(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangePasswordJson request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/me/username",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> changeUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody ChangeUsernameRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/user/{username}/password/reset",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> passwordReset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/social",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> social(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLogin(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLoginRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody SocialLinkRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialUnlink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("socialNetwork") String socialNetwork,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/follow/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> followUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/follow/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> unfollowUser(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/following/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowing(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/followers/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowers(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/push/enable/{os}/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> enablePushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("os") String os,
            @PathVariable("token") String token,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/push/disable/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> disablePushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("token") String token,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }


    @RequestMapping(
            method = RequestMethod.POST, path = "/push/message",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> sendPushNotification(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody SendPushNotificationRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/admin/collection/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createCollection(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/collection/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteCollection(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/document/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody CreateDocumentRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/document/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveDocumentById(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/document/{collection}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveDocumentByQuery(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody RetrieveDocumentByQueryRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/document/{collection}/count",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> countDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/document/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> modifyDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody ModifyDocumentRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/document/{collection}/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteDocument(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/document/{collection}/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionsDocumentUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/document/{collection}/{id}/{action}/role/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> grantPermissionsDocumentRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/document/{collection}/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionsDocumentUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/document/{collection}/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> revokePermissionsDocumentRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("collection") String collection,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/link/{sourceId}/{label}/{destinationId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("sourceId") String sourceId,
            @PathVariable("label") String label,
            @PathVariable("destinationId") String destinationId,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/link/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/link",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody RetrieveLinkRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/link/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteLink(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> uploadFile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody UploadFileRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteFile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFile(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/details/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFileDetail(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/details",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFilesDetail(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody RetrieveFilesDetailRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/file/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> grantFileAccessUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/file/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> grantFileAccessRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> revokeFileAccessUsername(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> revokeFileAccessRoleName(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/admin/asset",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> createAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody CreateAssetRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/asset/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/asset/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> deleteAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/asset",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchAsset(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody FetchAssetRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/configuration/dump.json",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchCurrentSetting(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/configuration/{section}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchSectionSetting(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("section") String section,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/configuration/{section}/{key}/{value}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> updateValueSetting(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("section") String section,
            @PathVariable("key") String key,
            @PathVariable("value") String value,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/endpoints",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> listGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/endpoints/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> readSpecificGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/endpoints/{name}/enabled",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> enableEndpointGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/endpoints/{name}/enabled",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> disableEndpointGroup(
            @Header("X-MBAAS-APPCODE") String appCode,
            @Header("X-MBAAS-SESSION") String session,
            @PathVariable("name") String name,
            @RequestBody Request request
    ) {
        return ResponseEntity.ok(null);
    }
}
