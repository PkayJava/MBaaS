package com.angkorteam.mbaas.server.service;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by socheat on 4/9/16.
 */
public interface PusherClient {

    @POST("rest/registry/device")
    Call<PusherDTOResponse> register(@Header("authorization") String authorization, @Body PusherDTORequest request);

    @DELETE("rest/registry/device/{deviceToken}")
    Call<RevokerDTOResponse> unregister(@Header("authorization") String authorization, @Path("deviceToken") String deviceToken);

    @PUT("rest/registry/device/pushMessage/{messageId}")
    Call<MetricsDTOResponse> sendMetrics(@Header("authorization") String authorization, @Path("messageId") String messageId);

    @POST("rest/sender")
    @Streaming
    Call<MessageDTOResponse> send(@Header("authorization") String authorization, @Body MessageDTORequest request);

}
