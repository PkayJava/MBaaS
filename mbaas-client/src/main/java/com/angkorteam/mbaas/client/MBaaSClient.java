package com.angkorteam.mbaas.client;

import com.angkorteam.mbaas.plain.request.*;
import com.angkorteam.mbaas.plain.response.Response;
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

    private Object currentUser;

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

    private static interface Client {
        @POST("/security/login")
        public Response login(@Body SecurityLoginRequest request);

        @POST("/security/signup")
        public Response signUp(@Body SecuritySignUpRequest request);

        @POST("/document/create/{collection}")
        public Response createDocument(@Header("X-MBAAS-SESSION") String session, @Path("collection") String collection, @Body DocumentCreateRequest request);

        @POST("/collection/create")
        public Response createCollection(@Header("X-MBAAS-SESSION") String session, @Body CollectionCreateRequest request);

        @POST("/collection/delete")
        public Response deleteCollection(@Header("X-MBAAS-SESSION") String session, @Body CollectionDeleteRequest request);

        @POST("/collection/attribute/create")
        public Response createCollectionAttribute(@Header("X-MBAAS-SESSION") String session, @Body CollectionAttributeCreateRequest request);

        @POST("/collection/attribute/delete")
        public Response deleteCollectionAttribute(@Header("X-MBAAS-SESSION") String session, @Body CollectionAttributeDeleteRequest request);
    }
}
