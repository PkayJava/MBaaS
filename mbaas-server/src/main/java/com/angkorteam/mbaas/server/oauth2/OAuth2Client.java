package com.angkorteam.mbaas.server.oauth2;

import com.angkorteam.mbaas.plain.response.monitor.MonitorTimeResponse;
import com.angkorteam.mbaas.plain.response.oauth2.OAuth2AuthorizeResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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

    @GET("api/monitor/time")
    Call<MonitorTimeResponse> monitorTime(@retrofit2.http.Header("Authorization") String authorization);

}
