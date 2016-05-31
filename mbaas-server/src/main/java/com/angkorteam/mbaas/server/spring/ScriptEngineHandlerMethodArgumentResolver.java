package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.server.nashorn.JavascripUtils;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 * Created by socheat on 5/31/16.
 */
public class ScriptEngineHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private ScriptEngineFactory scriptEngineFactory;

    private ClassFilter classFilter;

    public ScriptEngineHandlerMethodArgumentResolver() {
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(ScriptEngine.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ScriptEngine scriptEngine = null;
        if (this.scriptEngineFactory instanceof NashornScriptEngineFactory) {
            scriptEngine = ((NashornScriptEngineFactory) this.scriptEngineFactory).getScriptEngine(getClassFilter());
        } else {
            scriptEngine = this.scriptEngineFactory.getScriptEngine();
        }
        JavascripUtils.eval(scriptEngine);
        return scriptEngine;
    }

    public ScriptEngineFactory getScriptEngineFactory() {
        return scriptEngineFactory;
    }

    public void setScriptEngineFactory(ScriptEngineFactory scriptEngineFactory) {
        this.scriptEngineFactory = scriptEngineFactory;
    }

    public ClassFilter getClassFilter() {
        return classFilter;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.classFilter = classFilter;
    }
}
