package com.angkorteam.mbaas.test;

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
    }
}
