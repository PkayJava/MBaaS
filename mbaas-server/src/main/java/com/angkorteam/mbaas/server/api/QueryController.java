package com.angkorteam.mbaas.server.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by socheat on 2/22/16.
 */
@Controller
@RequestMapping(path = "/query")
public class QueryController {

    @RequestMapping(path = "/create")
    public void create() {
    }

    @RequestMapping(path = "/delete/{queryId}")
    public void delete() {
    }

    @RequestMapping(path = "/modify/{queryId}")
    public void modify() {
    }

    @RequestMapping(path = "/retrieve/{queryId}")
    public void retrieve() {
    }

    @RequestMapping(path = "/count/{queryId}")
    public void count() {
    }
}
