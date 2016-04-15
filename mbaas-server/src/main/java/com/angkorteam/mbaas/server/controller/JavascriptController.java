package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JavascriptTable;
import com.angkorteam.mbaas.model.entity.tables.records.JavascriptRecord;
import com.angkorteam.mbaas.plain.Identity;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.response.javascript.JavaScriptExecuteResponse;
import com.angkorteam.mbaas.server.nashorn.JavaFilter;
import com.angkorteam.mbaas.server.nashorn.MBaaS;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.collections.map.HashedMap;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.script.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by socheat on 2/27/16.
 */
@Controller
@RequestMapping(path = "/javascript")
public class JavascriptController {

    @Autowired
    private DSLContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(
            path = "/execute/{script}",
            method = {RequestMethod.POST, RequestMethod.PUT},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<JavaScriptExecuteResponse> executeJson(
            HttpServletRequest req,
            HttpServletResponse resp,
            Identity identity,
            @PathVariable("script") String script,
            @RequestBody(required = false) Map<String, Object> requestBody
    ) throws ScriptException, IOException, ServletException {
        JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");
        JavascriptRecord javascriptRecord = context.select(javascriptTable.fields()).from(javascriptTable).where(javascriptTable.PATH.eq(script)).fetchOneInto(javascriptTable);

        if (javascriptRecord == null || javascriptRecord.getScript() == null || "".equals(javascriptRecord.getScript()) || SecurityEnum.Denied.getLiteral().equals(javascriptRecord.getSecurity())) {
            return returnMethodNotAllowed();
        }

        com.angkorteam.mbaas.server.nashorn.Request request = new com.angkorteam.mbaas.server.nashorn.Request(req);
        ScriptEngine engine = getScriptEngine(request, identity);
        try {
            engine.eval(javascriptRecord.getScript());
        } catch (Throwable e) {
            return returnThrowable(e);
        }
        Invocable invocable = (Invocable) engine;
        HttpMethod method = HttpMethod.valueOf(req.getMethod());
        boolean found = false;
        boolean error = false;
        Throwable throwable = null;
        Object responseBody = null;
        if (method == HttpMethod.POST) {
            HttpPost http = invocable.getInterface(HttpPost.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpPost(request, requestBody);
                } catch (Throwable e) {
                    error = true;
                    throwable = e;
                }
            }
        } else if (method == HttpMethod.PUT) {
            HttpPut http = invocable.getInterface(HttpPut.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpPut(request, requestBody);
                } catch (Throwable e) {
                    error = true;
                    throwable = e;
                }
            }
        }

        return returnResponse(found, error, throwable, responseBody);
    }

    @RequestMapping(
            path = "/execute/{script}",
            method = {RequestMethod.GET, RequestMethod.DELETE},
            consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<JavaScriptExecuteResponse> execute(
            HttpServletRequest req,
            HttpServletResponse resp,
            Identity identity,
            @PathVariable("script") String script
    ) throws ScriptException, IOException, ServletException {
        JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");
        JavascriptRecord javascriptRecord = context.select(javascriptTable.fields()).from(javascriptTable).where(javascriptTable.PATH.eq(script)).fetchOneInto(javascriptTable);

        if (javascriptRecord == null || javascriptRecord.getScript() == null || "".equals(javascriptRecord.getScript()) || SecurityEnum.Denied.getLiteral().equals(javascriptRecord.getSecurity())) {
            return returnMethodNotAllowed();
        }

        com.angkorteam.mbaas.server.nashorn.Request request = new com.angkorteam.mbaas.server.nashorn.Request(req);
        ScriptEngine engine = getScriptEngine(request, identity);
        try {
            engine.eval(javascriptRecord.getScript());
        } catch (Throwable e) {
            return returnThrowable(e);
        }
        Invocable invocable = (Invocable) engine;
        HttpMethod method = HttpMethod.valueOf(req.getMethod());
        Object responseBody = null;
        boolean found = false;
        boolean error = false;
        Throwable throwable = null;
        if (method == HttpMethod.GET) {
            HttpGet http = invocable.getInterface(HttpGet.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpGet(request, new HashMap<>());
                } catch (Throwable e) {
                    error = true;
                    throwable = e;
                }
            }
        } else if (method == HttpMethod.DELETE) {
            HttpDelete http = invocable.getInterface(HttpDelete.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpDelete(request, new HashMap<>());
                } catch (Throwable e) {
                    error = true;
                    throwable = e;
                }
            }
        }

        return returnResponse(found, error, throwable, responseBody);
    }

    private ScriptEngine getScriptEngine(com.angkorteam.mbaas.server.nashorn.Request request, Identity identity) {
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new JavaFilter(context));
        Bindings bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        bindings.put("MBaaS", new MBaaS(context, identity, jdbcTemplate, request));
        bindings.put("Context", context);
        try {
            List<Class<?>> clazzes = new ArrayList<>();
            clazzes.add(Boolean.class);
            clazzes.add(Byte.class);
            clazzes.add(Short.class);
            clazzes.add(Integer.class);
            clazzes.add(Long.class);
            clazzes.add(Float.class);
            clazzes.add(Double.class);
            clazzes.add(Character.class);
            clazzes.add(String.class);
            clazzes.add(Date.class);
            clazzes.add(BigDecimal.class);
            clazzes.add(BigInteger.class);
            clazzes.add(Arrays.class);
            clazzes.add(Collections.class);
            clazzes.add(LinkedHashMap.class);
            clazzes.add(LinkedHashSet.class);
            clazzes.add(Hashtable.class);
            clazzes.add(Vector.class);
            clazzes.add(LinkedList.class);
            clazzes.add(ArrayList.class);
            clazzes.add(HashMap.class);
            clazzes.add(ArrayBlockingQueue.class);
            clazzes.add(SynchronousQueue.class);
            clazzes.add(LinkedBlockingDeque.class);
            clazzes.add(DelayQueue.class);
            clazzes.add(LinkedTransferQueue.class);
            clazzes.add(ArrayDeque.class);
            clazzes.add(ConcurrentLinkedDeque.class);
            clazzes.add(Stack.class);
            clazzes.add(Tables.class);
            clazzes.add(DSL.class);
            for (Class<?> clazz : clazzes) {
                engine.eval("var " + clazz.getSimpleName() + " = Java.type('" + clazz.getName() + "')");
            }
        } catch (ScriptException e) {
        }
        return engine;
    }

    private Object parseBody(Object body) {
        if (body instanceof JSObject) {
            JSObject js = (JSObject) body;
            if (js.isStrictFunction() || js.isFunction()) {
                return null;
            } else if (js.isArray()) {
                return js.values();
            } else {
                Map<String, Object> result = new LinkedHashMap<>();
                for (String key : js.keySet()) {
                    result.put(key, js.getMember(key));
                }
                return result;
            }
        } else {
            return body;
        }
    }

    protected ResponseEntity<JavaScriptExecuteResponse> returnThrowable(Throwable throwable) {
        if (throwable instanceof BadCredentialsException) {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.UNAUTHORIZED.value());
            response.setResult(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return ResponseEntity.ok(response);
        } else {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResult(throwable.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    protected ResponseEntity<JavaScriptExecuteResponse> returnResponse(boolean found, boolean error, Throwable throwable, Object responseBody) {
        if (!found) {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            return ResponseEntity.ok(response);
        } else {
            if (error) {
                return returnThrowable(throwable);
            } else {
                JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
                response.setData(parseBody(responseBody));
                return ResponseEntity.ok(response);
            }
        }
    }

    protected ResponseEntity<JavaScriptExecuteResponse> returnMethodNotAllowed() {
        JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
        response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        response.setResult(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        return ResponseEntity.ok(response);
    }

    public interface Http {
    }

    public interface HttpGet extends Http {
        Object httpGet(com.angkorteam.mbaas.server.nashorn.Request request, Map<String, Object> requestBody);
    }

    public interface HttpPost extends Http {
        Object httpPost(com.angkorteam.mbaas.server.nashorn.Request request, Map<String, Object> requestBody);
    }

    public interface HttpPut extends Http {
        Object httpPut(com.angkorteam.mbaas.server.nashorn.Request request, Map<String, Object> requestBody);
    }

    public interface HttpDelete extends Http {
        Object httpDelete(com.angkorteam.mbaas.server.nashorn.Request request, Map<String, Object> requestBody);
    }
}
