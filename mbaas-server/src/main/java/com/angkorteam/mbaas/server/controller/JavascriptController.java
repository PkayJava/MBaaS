package com.angkorteam.mbaas.server.controller;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JavascriptTable;
import com.angkorteam.mbaas.model.entity.tables.records.JavascriptRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.request.javascript.JavaScriptExecuteRequest;
import com.angkorteam.mbaas.plain.response.javascript.JavaScriptExecuteResponse;
import com.angkorteam.mbaas.server.nashorn.JavaFilter;
import com.angkorteam.mbaas.server.nashorn.MBaaS;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.script.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("script") String script,
            @RequestBody(required = false) JavaScriptExecuteRequest requestBody
    ) throws ScriptException, IOException, ServletException {
        JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");
        JavascriptRecord javascriptRecord = context.select(javascriptTable.fields()).from(javascriptTable).where(javascriptTable.PATH.eq(script)).fetchOneInto(javascriptTable);

        if (javascriptRecord == null || javascriptRecord.getScript() == null || "".equals(javascriptRecord.getScript()) || SecurityEnum.Denied.getLiteral().equals(javascriptRecord.getSecurity())) {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            return ResponseEntity.ok(response);
        }

        com.angkorteam.mbaas.server.nashorn.Request request = new com.angkorteam.mbaas.server.nashorn.Request(req);
        ScriptEngine engine = getScriptEngine(request);
        engine.eval(javascriptRecord.getScript());
        Invocable invocable = (Invocable) engine;
        HttpMethod method = HttpMethod.valueOf(req.getMethod());
        boolean found = false;
        boolean error = false;
        Throwable exception = null;
        Object responseBody = null;
        if (method == HttpMethod.POST) {
            HttpPost http = invocable.getInterface(HttpPost.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpPost(request, requestBody);
                } catch (Throwable e) {
                    error = true;
                    exception = e;
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
                    exception = e;
                }
            }
        }

        if (!found) {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            return ResponseEntity.ok(response);
        } else {
            if (error) {
                JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
                response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setResult(exception.getMessage());
                return ResponseEntity.ok(response);
            } else {
                JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
                response.getData().setScript(script);
                response.getData().setBody(parseBody(responseBody));
                return ResponseEntity.ok(response);
            }
        }
    }

    @RequestMapping(
            path = "/execute/{script}",
            method = {RequestMethod.GET, RequestMethod.HEAD, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.TRACE},
            consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<JavaScriptExecuteResponse> execute(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @PathVariable("script") String script,
            @RequestBody(required = false) JavaScriptExecuteRequest requestBody
    ) throws ScriptException, IOException, ServletException {
        JavascriptTable javascriptTable = Tables.JAVASCRIPT.as("javascriptTable");
        JavascriptRecord javascriptRecord = context.select(javascriptTable.fields()).from(javascriptTable).where(javascriptTable.PATH.eq(script)).fetchOneInto(javascriptTable);

        if (javascriptRecord == null || javascriptRecord.getScript() == null || "".equals(javascriptRecord.getScript()) || SecurityEnum.Denied.getLiteral().equals(javascriptRecord.getSecurity())) {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            return ResponseEntity.ok(response);
        }

        com.angkorteam.mbaas.server.nashorn.Request request = new com.angkorteam.mbaas.server.nashorn.Request(req);
        ScriptEngine engine = getScriptEngine(request);
        engine.eval(javascriptRecord.getScript());
        Invocable invocable = (Invocable) engine;
        HttpMethod method = HttpMethod.valueOf(req.getMethod());
        Object responseBody = null;
        boolean found = false;
        boolean error = false;
        Throwable exception = null;
        if (method == HttpMethod.GET) {
            HttpGet http = invocable.getInterface(HttpGet.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpGet(request, requestBody);
                } catch (Throwable e) {
                    error = true;
                    exception = e;
                }
            }
        } else if (method == HttpMethod.HEAD) {
            HttpHead http = invocable.getInterface(HttpHead.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpHead(request, requestBody);
                } catch (Throwable e) {
                    error = true;
                    exception = e;
                }
            }
        } else if (method == HttpMethod.PATCH) {
            HttpPatch http = invocable.getInterface(HttpPatch.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpPatch(request, requestBody);
                } catch (Throwable e) {
                    error = true;
                    exception = e;
                }
            }
        } else if (method == HttpMethod.DELETE) {
            HttpDelete http = invocable.getInterface(HttpDelete.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpDelete(request, requestBody);
                } catch (Throwable e) {
                    error = true;
                    exception = e;
                }
            }
        } else if (method == HttpMethod.OPTIONS) {
            HttpOptions http = invocable.getInterface(HttpOptions.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpOptions(request, requestBody);
                } catch (Throwable e) {
                    error = true;
                    exception = e;
                }
            }
        } else if (method == HttpMethod.TRACE) {
            HttpTrace http = invocable.getInterface(HttpTrace.class);
            if (http != null) {
                found = true;
                try {
                    responseBody = http.httpTrace(request, requestBody);
                } catch (Throwable e) {
                    error = true;
                    exception = e;
                }
            }
        }

        if (!found) {
            JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
            response.setHttpCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            return ResponseEntity.ok(response);
        } else {
            if (error) {
                JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
                response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setResult(exception.getMessage());
                return ResponseEntity.ok(response);
            } else {
                JavaScriptExecuteResponse response = new JavaScriptExecuteResponse();
                response.getData().setScript(script);
                response.getData().setBody(parseBody(responseBody));
                return ResponseEntity.ok(response);
            }
        }
    }

    private ScriptEngine getScriptEngine(com.angkorteam.mbaas.server.nashorn.Request request) {
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new JavaFilter(context));
        Bindings bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        bindings.put("MBaaS", new MBaaS(context, jdbcTemplate, request));
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

    public interface Http {
    }

    public interface HttpGet extends Http {
        Object httpGet(com.angkorteam.mbaas.server.nashorn.Request request, JavaScriptExecuteRequest requestBody);
    }

    public interface HttpHead extends Http {
        Object httpHead(com.angkorteam.mbaas.server.nashorn.Request request, JavaScriptExecuteRequest requestBody);
    }

    public interface HttpPost extends Http {
        Object httpPost(com.angkorteam.mbaas.server.nashorn.Request request, JavaScriptExecuteRequest requestBody);
    }

    public interface HttpPut extends Http {
        Object httpPut(com.angkorteam.mbaas.server.nashorn.Request request, JavaScriptExecuteRequest requestBody);
    }

    public interface HttpPatch extends Http {
        Object httpPatch(com.angkorteam.mbaas.server.nashorn.Request request, JavaScriptExecuteRequest requestBody);
    }

    public interface HttpDelete extends Http {
        Object httpDelete(com.angkorteam.mbaas.server.nashorn.Request request, JavaScriptExecuteRequest requestBody);
    }

    public interface HttpOptions extends Http {
        Object httpOptions(com.angkorteam.mbaas.server.nashorn.Request request, JavaScriptExecuteRequest requestBody);
    }

    public interface HttpTrace extends Http {
        Object httpTrace(com.angkorteam.mbaas.server.nashorn.Request request, JavaScriptExecuteRequest requestBody);
    }
}
