package com.angkorteam.mbaas.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by socheat on 2/21/16.
 */
public class BaseTest {

    public final String HOST_A = "http://pkayjava.ddns.net:7080/api";

    public final String HOST_B = "http://172.16.1.42:7080/api";

    public final String HOST_C = "http://192.168.1.106:7080/api";

    public Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").create();

    public MBaaSClient client = new MBaaSClient("1234567890", HOST_C);

}
