package com.angkorteam.mbaas.sdk;

import com.angkorteam.baasbox.sdk.java.json.LoginJson;
import com.angkorteam.baasbox.sdk.java.response.SuccessResponse;
import com.angkorteam.mbaas.request.CreateDocumentRequest;
import com.angkorteam.mbaas.request.LoginRequest;
import com.angkorteam.mbaas.request.Request;
import com.angkorteam.mbaas.request.SignupRequest;
import com.angkorteam.mbaas.response.Response;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Khauv Socheat on 2/10/2016.
 */
public interface ClientSDK {

    @POST("/login")
    public Response login(@Body LoginRequest request);

    @POST("/user")
    public Response signup(@Body SignupRequest request);

    @POST("/document/create/{collection}")
    public Response createDocument(@Path("collection") String collection, @Body CreateDocumentRequest request);

    @POST("/admin/collection/create/{collection}")
    public Response createCollection(@Header("X-MBAAS-SESSION") String session, @Path("collection") String collection, @Body Request request);

    @POST("/admin/collection/delete/{collection}")
    public Response deleteCollection(@Header("X-MBAAS-SESSION") String session, @Path("collection") String collection, @Body Request request);

}
