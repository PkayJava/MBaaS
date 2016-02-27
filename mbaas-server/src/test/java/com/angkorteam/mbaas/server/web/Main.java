package com.angkorteam.mbaas.server.web;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by socheat on 2/27/16.
 */
public class Main {

    public static void main(String[] args) throws ScriptException {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("nashorn");
            StringBuilder javascript = new StringBuilder("");
            javascript.append("function sum(request,response){return request + response + ss;}");
            engine.eval(javascript.toString());
            Invocable invocable = (Invocable) engine;
            Hello hello = invocable.getInterface(Hello.class);
            System.out.println(hello.sum(1, 3));
        } catch (Throwable throwable) {
            System.out.println(throwable.getClass().getName());
        }
    }

    public interface Hello {
        int sum(int a, int b, int p);

        int sum(int a, int b);
    }
}
