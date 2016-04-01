package com.angkorteam.mbaas.server.oauth2;

import com.angkorteam.mbaas.plain.request.oauth2.OAuth2RefreshRequest;
import com.angkorteam.mbaas.plain.request.oauth2.OAuth2TokenRequest;
import com.angkorteam.mbaas.plain.response.monitor.MonitorTimeResponse;
import com.angkorteam.mbaas.plain.response.oauth2.*;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by socheat on 3/31/16.
 */
public interface OAuth2Client {

    @POST("api/oauth2/authorize")
    @FormUrlEncoded
    Call<OAuth2AuthorizeResponse> oauth2Authorize(@Field("client_id") String clientId,
                                                  @Field("client_secret") String clientSecret,
                                                  @Field("grant_type") String grantType,
                                                  @Field("redirect_uri") String redirectUri,
                                                  @Field("code") String code);

    @POST("api/oauth2/password")
    @FormUrlEncoded
    Call<OAuth2PasswordResponse> oauth2Password(@Field("grant_type") String grantType,
                                                @Field("username") String username,
                                                @Field("password") String password,
                                                @Field("scope") String scope);

    @POST("api/oauth2/client")
    @FormUrlEncoded
    Call<OAuth2ClientResponse> oauth2Client(@Field("grant_type") String grantType,
                                            @Field("scope") String scope);

    @GET("api/monitor/time")
    Call<MonitorTimeResponse> monitorTime(@retrofit2.http.Header("Authorization") String authorization);

    @POST("api/oauth2/token")
    Call<OAuth2TokenResponse> oauth2Token(@Body OAuth2TokenRequest request);

    @POST("api/oauth2/refresh")
    Call<OAuth2RefreshResponse> oauth2Token(@Body OAuth2RefreshRequest request);

}
