package com.angkorteam.mbaas.server.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by socheat on 4/9/16.
 */
public interface PusherClient {

    @POST("rest/registry/device")
    Call<PusherDTOResponse> register(@Header("authorization") String authorization, @Body PusherDTORequest request);

    @POST("rest/registry/device")
    Call<ResponseBody> register1(@Header("authorization") String authorization, @Body PusherDTORequest request);

    @DELETE("rest/registry/device/{deviceToken}")
    Call<RevokerDTOResponse> unregister(@Header("authorization") String authorization, @Path("deviceToken") String deviceToken);
}
