//package com.angkorteam.mbaas.server.wicket;
//
//import com.angkorteam.mbaas.plugin.MBaaSExtension;
//import com.angkorteam.mbaas.plugin.Response;
//import com.angkorteam.mbaas.plugin.Sync;
//import com.angkorteam.mbaas.plugin.Task;
//import com.google.gson.Gson;
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.Unirest;
//import com.mashape.unirest.http.exceptions.UnirestException;
//import com.mashape.unirest.request.HttpRequestWithBody;
//import org.sql2o.Sql2o;
//
//import java.io.File;
//import java.io.IOException;
//
///**
// * Created by socheat on 11/19/16.
// */
//public class TestPlugin extends Task {
//
//    public static void main(String[] args) throws IOException {
//        MBaaSExtension extension = new MBaaSExtension();
//        extension.setLogin("service");
//        extension.setPassword("service");
//        extension.setServer("http://localhost:9080");
//        String sqlite = "/opt/home/socheat/Documents/git/PkayJava/MBaaS/mbaas-server/mbaas.db";
//        ensureDatabase(sqlite);
//
//        File source = new File("/opt/home/socheat/Documents/git/PkayJava/MBaaS/mbaas-server/src/main/java");
//
//        Sql2o sql2o = new Sql2o("jdbc:sqlite:" + sqlite, "", "");
//        Sync sync = new Sync();
//        // page to sync, html + groovy
//        pageForSync(source, sql2o, sync);
//        // rest to sync, groovy
//        restForSync(source, sql2o, sync);
//        // page to delete sync, html + groovy
//        pageForDeleteSync(source, sql2o, sync);
//        // rest to delete sync, groovy
//        restForDeleteSync(source, sql2o, sync);
//
//        String server = null;
//        if (extension.getServer().endsWith("/")) {
//            server = extension.getServer().substring(0, extension.getServer().length() - 1);
//        } else {
//            server = extension.getServer();
//        }
//
//        String api = server + "/api/system/sync";
//        HttpRequestWithBody request = Unirest.post(api);
//        request = request.basicAuth(extension.getLogin(), extension.getPassword());
//        Gson gson = new Gson();
//        try {
//            HttpResponse<String> response = request.body(gson.toJson(sync)).asString();
//            if (response.getStatus() == 200) {
//                Response temp = gson.fromJson(response.getBody(), Response.class);
//                syncPage(source, sql2o, temp.getData());
//                syncRest(source, sql2o, temp.getData());
//            }
//        } catch (UnirestException e) {
//            e.printStackTrace();
//        }
//    }
//}
