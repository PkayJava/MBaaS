package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.plain.response.RestResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 11/4/16.
 */
public interface RestService {

    ResponseEntity<RestResponse> service(HttpServletRequest request) throws Throwable;

    String getRestUUID();

}
