package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.server.service.PusherClient;
import com.angkorteam.mbaas.server.service.PusherDTOResponse;
import com.angkorteam.mbaas.server.service.RevokerDTOResponse;
import com.google.gson.Gson;
import okhttp3.ResponseBody;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;

/**
 * Created by socheat on 2/27/16.
 */
public class Main {

    public static void main(String argsp[]) throws IOException {
        String pushAddress = "http://192.168.1.110:6080/ag-push/";
        String basic = "Basic " + Base64.encodeBase64String(("eb268a39-9460-45c0-b4c6-ac409eacabdf" + ":" + "b33239fd-68a3-4508-b117-2b322bed24b5").getBytes());
        String httpAddress = pushAddress.endsWith("/") ? pushAddress : pushAddress + "/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(httpAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PusherClient client = retrofit.create(PusherClient.class);

        Call<RevokerDTOResponse> responseCall = client.unregister(basic, "d6Gxxb-k28A:APA91bGrz0tlFvAiUh6UL8RiFB0r7KXRyix-4SJxthv8izcE77tmuO_voGQKGwg_p2BxebK71hPIO3O9cSp13PUZhx-aH46P8ML1XjzpxPcyVhk3V2Z5cwH2fdsRDChamx1yttWGCuIT");
        Response<RevokerDTOResponse> responseBody = null;
        try {
            responseBody = responseCall.execute();
        } catch (Throwable e) {
            System.out.print(e.getMessage());
        }
        if (responseBody != null) {
            System.out.println(responseBody.code());
        }
    }

}
