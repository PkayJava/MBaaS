package com.angkorteam.mbaas.test;

import com.angkorteam.mbaas.request.SignupRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class Hello {

    public static void main(String args[]) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        engine.eval("print('Hello World!');");

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setAppCode("test");
        signupRequest.setUsername("admin");
        signupRequest.setPassword("12313a");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(signupRequest);
        System.out.println(json);
    }
}
