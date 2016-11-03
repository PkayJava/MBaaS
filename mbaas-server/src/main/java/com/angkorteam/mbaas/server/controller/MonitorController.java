//package com.angkorteam.mbaas.server.controller;
//
//import com.angkorteam.mbaas.plain.response.monitor.MonitorTimeResponse;
//import com.google.gson.Gson;
//import org.apache.commons.lang3.time.DateFormatUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import javax.servlet.http.HttpServletRequest;
//import java.text.DecimalFormat;
//import java.util.Date;
//
///**
// * Created by socheat on 2/18/16.
// */
//@Controller
//@RequestMapping(path = "/monitor")
//public class MonitorController {
//
//    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
//
//    @Autowired
//    private Gson gson;
//
//    @RequestMapping(
//            method = RequestMethod.GET, path = "/time",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<MonitorTimeResponse> time(
//            HttpServletRequest request
//    ) {
//        MonitorTimeResponse response = new MonitorTimeResponse();
//        response.setData(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
//        return ResponseEntity.ok(response);
//    }
//
//}
