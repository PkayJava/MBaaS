package com.angkorteam.mbaas.client;

import com.angkorteam.mbaas.plain.request.*;
import com.angkorteam.mbaas.plain.response.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

import java.util.concurrent.TimeUnit;

/**
 * Created by socheat on 2/16/16.
 */
public class MBaaSClient {

    private String appCode;

    private String currentUser;

    private Client client;

    private Gson gson;

    private String session;

    public MBaaSClient(String appCode, String address) {
        this.appCode = appCode;
        {
            GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
            gson = builder.create();
        }
        OkHttpClient http = new OkHttpClient();
        OkClient client = new OkClient(http);
        http.setReadTimeout(1, TimeUnit.MINUTES);
        http.setConnectTimeout(1, TimeUnit.MINUTES);
        http.setWriteTimeout(1, TimeUnit.MINUTES);
        {
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder.setConverter(new GsonConverter(gson));
            builder.setEndpoint(address);
            builder.setClient(client);
            RestAdapter restAdapter = builder.build();
            this.client = restAdapter.create(Client.class);
        }
    }

    //region Security Controller

    public SecuritySignUpResponse securitySignUp(SecuritySignUpRequest request) {
        return this.client.securitySignUp(request);
    }

    public SecurityLoginResponse securityLogin(SecurityLoginRequest request) {
        SecurityLoginResponse response = this.client.securityLogin(request);
        this.session = response.getData().getSession();
        this.currentUser = response.getData().getLogin();
        return response;
    }

    public SecurityLogoutResponse securityLogout(SecurityLogoutRequest request) {
        return client.securityLogout(this.session, request);
    }

    public SecurityLogoutSessionResponse securityLogoutSession(String session, SecurityLogoutSessionRequest request) {
        return this.client.securityLogoutSession(this.session, session, request);
    }

    //endregion

    //region Collection Controller

    public CollectionCreateResponse collectionCreate(CollectionCreateRequest request) {
        return this.client.collectionCreate(this.session, request);
    }

    public CollectionAttributeCreateResponse collectionAttributeCreate(CollectionAttributeCreateRequest request) {
        return this.client.collectionAttributeCreate(this.session, request);
    }

    public CollectionAttributeDeleteResponse collectionAttributeDelete(CollectionAttributeDeleteRequest request) {
        return this.client.collectionAttributeDelete(this.session, request);
    }

    public CollectionDeleteResponse collectionDelete(CollectionDeleteRequest request) {
        return this.client.collectionDelete(this.session, request);
    }

    public CollectionPermissionUsernameResponse collectionPermissionGrantUsername(CollectionPermissionUsernameRequest request) {
        return this.client.collectionPermissionGrantUsername(this.session, request);
    }

    public CollectionPermissionRoleNameResponse collectionPermissionGrantRoleName(CollectionPermissionRoleNameRequest request) {
        return this.client.collectionPermissionGrantRoleName(this.session, request);
    }

    public CollectionPermissionUsernameResponse collectionPermissionRevokeUsername(CollectionPermissionUsernameRequest request) {
        return this.client.collectionPermissionRevokeUsername(this.session, request);
    }

    public CollectionPermissionRoleNameResponse collectionPermissionRevokeRoleName(CollectionPermissionRoleNameRequest request) {
        return this.client.collectionPermissionRevokeRoleName(this.session, request);
    }

    //endregion

    //region Document Controller

    public DocumentCreateResponse createDocument(String collectionName, DocumentCreateRequest request) {
        return client.createDocument(this.session, collectionName, request);
    }

    //endregion

    //region Monitor Controller

    public MonitorCpuResponse cpu(MonitorCpuRequest request) {
        return client.cpu(this.session, request);
    }

    public MonitorMemResponse mem(MonitorMemRequest request) {
        return client.mem(this.session, request);
    }

    //endregion

    private interface Client {

        //region Security Controller Interface

        @POST("/security/login")
        public SecurityLoginResponse securityLogin(@Body SecurityLoginRequest request);

        @POST("/security/signup")
        public SecuritySignUpResponse securitySignUp(@Body SecuritySignUpRequest request);

        @POST("/security/logout")
        public SecurityLogoutResponse securityLogout(@Header("X-MBAAS-SESSION") String session, @Body SecurityLogoutRequest request);

        @POST("/security/logout/{session}")
        public SecurityLogoutSessionResponse securityLogoutSession(@Header("X-MBAAS-SESSION") String currentSession, @Path("session") String logoutSession, @Body SecurityLogoutSessionRequest request);

        //endregion

        //region Collection Controller Interface

        @POST("/collection/create")
        public CollectionCreateResponse collectionCreate(@Header("X-MBAAS-SESSION") String session, @Body CollectionCreateRequest request);

        @POST("/collection/delete")
        public CollectionDeleteResponse collectionDelete(@Header("X-MBAAS-SESSION") String session, @Body CollectionDeleteRequest request);

        @POST("/collection/attribute/create")
        public CollectionAttributeCreateResponse collectionAttributeCreate(@Header("X-MBAAS-SESSION") String session, @Body CollectionAttributeCreateRequest request);

        @POST("/collection/attribute/delete")
        public CollectionAttributeDeleteResponse collectionAttributeDelete(@Header("X-MBAAS-SESSION") String session, @Body CollectionAttributeDeleteRequest request);

        @POST("/collection/permission/grant/username")
        public CollectionPermissionUsernameResponse collectionPermissionGrantUsername(@Header("X-MBAAS-SESSION") String session, @Body CollectionPermissionUsernameRequest request);

        @POST("/collection/permission/grant/rolename")
        public CollectionPermissionRoleNameResponse collectionPermissionGrantRoleName(@Header("X-MBAAS-SESSION") String session, @Body CollectionPermissionRoleNameRequest request);

        @POST("/collection/permission/revoke/username")
        public CollectionPermissionUsernameResponse collectionPermissionRevokeUsername(@Header("X-MBAAS-SESSION") String session, @Body CollectionPermissionUsernameRequest request);

        @POST("/collection/permission/revoke/rolename")
        public CollectionPermissionRoleNameResponse collectionPermissionRevokeRoleName(@Header("X-MBAAS-SESSION") String session, @Body CollectionPermissionRoleNameRequest request);

        //endregion

        //region Document Controller Interface

        @POST("/document/create/{collection}")
        public DocumentCreateResponse createDocument(@Header("X-MBAAS-SESSION") String session, @Path("collection") String collection, @Body DocumentCreateRequest request);

        //endregion

        //region Monitor Controller Interface

        @POST("/monitor/cpu")
        public MonitorCpuResponse cpu(@Header("X-MBAAS-SESSION") String session, @Body MonitorCpuRequest request);

        @POST("/monitor/mem")
        public MonitorMemResponse mem(@Header("X-MBAAS-SESSION") String session, @Body MonitorMemRequest request);

        //endregion
    }
}
