package com.angkorteam.mbaas.sdk;

import com.angkorteam.baasbox.sdk.java.json.LoginJson;
import com.angkorteam.baasbox.sdk.java.response.SuccessResponse;
import com.angkorteam.mbaas.request.LoginRequest;
import com.angkorteam.mbaas.request.SignupRequest;
import com.angkorteam.mbaas.response.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Khauv Socheat on 2/10/2016.
 */
public interface ClientSDK {

    @POST("/login")
    public Response login(@Body LoginRequest request);

    @POST("/user")
    public Response signup(@Body SignupRequest request);


}
