package com.angkorteam.mbaas.server.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by socheat on 2/22/16.
 */
@Controller
@RequestMapping(path = "/data")
public class DataController {

    @RequestMapping(path = "/query/{queryId}")
    public void query() {
    }

    @RequestMapping(path = "/count/{queryId}")
    public void count() {
    }
}
