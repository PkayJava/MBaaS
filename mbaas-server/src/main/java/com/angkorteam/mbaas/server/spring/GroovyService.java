package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.plain.response.GroovyResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 11/4/16.
 */
public interface GroovyService {

    ResponseEntity<GroovyResponse> service(HttpServletRequest request) throws Throwable;

}
