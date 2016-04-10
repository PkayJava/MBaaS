package com.angkorteam.mbaas.server.controller;

//import com.angkorteam.baasbox.sdk.java.request.SendPushNotificationRequest;

import com.angkorteam.mbaas.plain.request.*;
import com.angkorteam.mbaas.plain.response.Response;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 2/4/16.
 */
@Controller
public class RestAPIController {


    @RequestMapping(
            method = RequestMethod.GET, path = "/social",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> social(
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/social/{socialNetwork}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> socialLogin(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
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
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
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
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("socialNetwork") String socialNetwork
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/follow/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> followUser(
            HttpServletRequest request,
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/follow/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> unfollowUser(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/following/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowing(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/followers/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> fetchFollowers(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/push/enable/{os}/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> enablePushNotification(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("os") String os,
            @PathVariable("token") String token
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/push/disable/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> disablePushNotification(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("token") String token
    ) {
        return ResponseEntity.ok(null);
    }


//    @RequestMapping(
//            method = RequestMethod.POST, path = "/push/message",
//            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<Response> sendPushNotification(
//            @Header("client_id") String clientId,
//            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
//            @RequestBody SendPushNotificationRequest request
//    ) {
//        return ResponseEntity.ok(null);
//    }


    @RequestMapping(
            method = RequestMethod.POST, path = "/link/{sourceId}/{label}/{destinationId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> createLink(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("sourceId") String sourceId,
            @PathVariable("label") String label,
            @PathVariable("destinationId") String destinationId
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/link/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveLink(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/link",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> retrieveLink(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody RetrieveLinkRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/link/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteLink(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> uploadFile(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> deleteFile(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFile(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/details/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFileDetail(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/file/details",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveFilesDetail(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody RetrieveFilesDetailRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/file/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> grantFileAccessUsername(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/file/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> grantFileAccessRoleName(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}/{action}/user/{username}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> revokeFileAccessUsername(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/file/{id}/{action}/user/{rolename}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> revokeFileAccessRoleName(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("id") String id,
            @PathVariable("action") String action,
            @PathVariable("rolename") String rolename
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/admin/asset",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> createAsset(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/asset/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> retrieveAsset(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("name") String name
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/asset/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> deleteAsset(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("name") String name
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/asset",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchAsset(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody FetchAssetRequest request
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/configuration/dump.json",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchCurrentSetting(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/configuration/{section}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> fetchSectionSetting(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("section") String section
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/configuration/{section}/{key}/{value}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> updateValueSetting(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("section") String section,
            @PathVariable("key") String key,
            @PathVariable("value") String value
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/endpoints",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> listGroup(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.GET, path = "/admin/endpoints/{name}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> readSpecificGroup(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("name") String name
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.PUT, path = "/admin/endpoints/{name}/enabled",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> enableEndpointGroup(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("name") String name
    ) {
        return ResponseEntity.ok(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE, path = "/admin/endpoints/{name}/enabled",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InputStreamResource> disableEndpointGroup(
            @Header("client_id") String clientId,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("name") String name
    ) {
        return ResponseEntity.ok(null);
    }
}
