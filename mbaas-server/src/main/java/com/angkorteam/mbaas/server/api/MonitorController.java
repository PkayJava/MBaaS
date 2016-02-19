package com.angkorteam.mbaas.server.api;

import com.angkorteam.mbaas.plain.request.MonitorCpuRequest;
import com.angkorteam.mbaas.plain.request.MonitorMemRequest;
import com.angkorteam.mbaas.plain.request.UpdateUserProfileRequest;
import com.angkorteam.mbaas.plain.response.MonitorCpuResponse;
import com.angkorteam.mbaas.plain.response.MonitorMemResponse;
import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.Gson;
import org.apache.wicket.util.lang.Bytes;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;

/**
 * Created by socheat on 2/18/16.
 */
@Controller
@RequestMapping(path = "/monitor")
public class MonitorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorController.class);

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    @Autowired
    private Gson gson;

    @RequestMapping(
            method = RequestMethod.POST, path = "/cpu",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MonitorCpuResponse> cpu(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody MonitorCpuRequest requestBody
    ) throws SigarException {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        MonitorCpuResponse response = new MonitorCpuResponse();
        Sigar sigar = new Sigar();
        for (CpuInfo cpuInfo : sigar.getCpuInfoList()) {
            MonitorCpuResponse.Body body = new MonitorCpuResponse.Body();
            body.setCacheSize(cpuInfo.getCacheSize());
            body.setCoresPerSocket(cpuInfo.getCoresPerSocket());
            body.setMhz(cpuInfo.getMhz());
            body.setMhzMax(cpuInfo.getMhzMax());
            body.setMhzMin(cpuInfo.getMhzMin());
            body.setModel(cpuInfo.getModel());
            body.setTotalCores(cpuInfo.getTotalCores());
            body.setTotalSockets(cpuInfo.getTotalSockets());
            body.setVendor(cpuInfo.getVendor());
            response.getData().add(body);
        }
        sigar.close();
        return ResponseEntity.ok(response);
    }

    @RequestMapping(
            method = RequestMethod.POST, path = "/mem",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MonitorMemResponse> mem(
            HttpServletRequest request,
            @RequestHeader(name = "X-MBAAS-APPCODE", required = false) String appCode,
            @RequestHeader(name = "X-MBAAS-SESSION", required = false) String session,
            @RequestBody MonitorMemRequest requestBody
    ) throws SigarException {
        LOGGER.info("{} appCode=>{} session=>{} body=>{}", request.getRequestURL(), appCode, session, gson.toJson(requestBody));

        MonitorMemResponse response = new MonitorMemResponse();
        Sigar sigar = new Sigar();
        Mem mem = sigar.getMem();
        response.getData().setRam(mem.getRam());
        response.getData().setFreePercent(Double.valueOf(DECIMAL_FORMAT.format(mem.getFreePercent())));
        response.getData().setUsedPercent(Double.valueOf(DECIMAL_FORMAT.format(mem.getUsedPercent())));
        response.getData().setFree(DECIMAL_FORMAT.format(Bytes.bytes(mem.getFree()).gigabytes()) + " GB");
        response.getData().setUsed(DECIMAL_FORMAT.format(Bytes.bytes(mem.getUsed()).gigabytes()) + " GB");
        response.getData().setActualFree(DECIMAL_FORMAT.format(Bytes.bytes(mem.getActualFree()).gigabytes()) + " GB");
        response.getData().setActualUsed(DECIMAL_FORMAT.format(Bytes.bytes(mem.getActualUsed()).gigabytes()) + " GB");
        response.getData().setTotal(DECIMAL_FORMAT.format(Bytes.bytes(mem.getTotal()).gigabytes()) + " GB");

        sigar.close();
        return ResponseEntity.ok(response);
    }
}
