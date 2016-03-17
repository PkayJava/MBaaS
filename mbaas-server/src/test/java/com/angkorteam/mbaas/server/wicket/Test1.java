package com.angkorteam.mbaas.server.wicket;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptException;

/**
 * Created by socheat on 3/12/16.
 */
public class Test1 {

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
//        ScriptEngine nashorn = factory.getScriptEngine(new NoJavaFilter());
//        Object result = nashorn.eval("var JavaScript = Java.type('com.angkorteam.mbaas.server.wicket.JavaScript');var test = JavaScript.fun3({foo: 'bar1',bar: 'foo1'});");
//        Object result = nashorn.eval("var JavaScript = Java.type('com.angkorteam.mbaas.server.wicket.JavaScript');var test = JavaScript.fun3(function (a){if(a==undefined){print('abc')}else{print(a);}});");
//        System.out.println(result);
//        System.out.println(result.getClass().getName());
    }
}
