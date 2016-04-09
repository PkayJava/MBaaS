package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.server.service.PusherClient;
import okhttp3.ResponseBody;
import org.apache.commons.codec.binary.Base64;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Created by socheat on 2/27/16.
 */
public class Main {

    public static void main(String argsp[]) throws IOException {
        String pushAddress = "http://192.168.1.110:6080/ag-push/";
        String authorization = "Basic " + Base64.encodeBase64String(("eb268a39-9460-45c0-b4c6-ac409eacabdf" + ":" + "b33239fd-68a3-4508-b117-2b322bed24b5").getBytes());
        String httpAddress = pushAddress.endsWith("/") ? pushAddress : pushAddress + "/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(httpAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PusherClient client = retrofit.create(PusherClient.class);
        String messageId = "749eb890-192e-4a3f-a359-3a6ae3a8a1f7";
        Call<ResponseBody> responseCall = client.sendMetrics(authorization, messageId);
        Response<ResponseBody> responseBody = null;
        try {
            responseBody = responseCall.execute();
        } catch (Throwable e) {
            System.out.print(e.getMessage());
        }
        String ss = responseBody.body().string();
        System.out.println(ss);
        if (responseBody != null) {
            System.out.println(responseBody.code());
        }
    }

}
