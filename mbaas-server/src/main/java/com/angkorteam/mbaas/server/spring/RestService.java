package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.plain.response.RestResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by socheat on 11/4/16.
 */
public interface RestService {

    ResponseEntity<RestResponse> service(HttpServletRequest request, Map<String, String> pathVariables) throws Throwable;

    String getRestUUID();

}
