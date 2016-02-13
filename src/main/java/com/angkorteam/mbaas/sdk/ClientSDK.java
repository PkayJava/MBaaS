package com.angkorteam.mbaas.sdk;

import com.angkorteam.mbaas.request.*;
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
    public Response login(@Body SecurityLoginRequest request);

    @POST("/user")
    public Response signup(@Body SecuritySignupRequest request);

    @POST("/document/create/{collection}")
    public Response createDocument(@Path("collection") String collection, @Body CreateDocumentRequest request);

    @POST("/collection/create")
    public Response createCollection(@Header("X-MBAAS-SESSION") String session, @Body CollectionCreateRequest request);

    @POST("/collection/delete")
    public Response deleteCollection(@Header("X-MBAAS-SESSION") String session, @Body CollectionDeleteRequest request);

}
