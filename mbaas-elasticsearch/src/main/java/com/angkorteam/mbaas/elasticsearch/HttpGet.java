package com.angkorteam.mbaas.elasticsearch;

import org.apache.http.client.methods.HttpPut;

import java.net.URI;

/**
 * Created by socheat on 9/24/16.
 */
public class HttpGet extends HttpPut {

    public HttpGet() {
    }

    public HttpGet(URI uri) {
        super(uri);
    }

    public HttpGet(String uri) {
        super(uri);
    }

    @Override
    public String getMethod() {
        return "GET";
    }
}
