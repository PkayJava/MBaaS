package com.angkorteam.mbaas.server.nashorn;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.*;

/**
 * Created by socheat on 3/20/16.
 */
public class Request {

    private final Map<String, List<String>> parameterValues;

    private final Map<String, String> parameterValue;

    private final Map<String, String> headerValue;

    private final Map<String, List<String>> headerValues;

    private final List<String> headerNames;

    private final List<String> parameterNames;

    private final List<Part> parts;

    private final Map<String, Part> partsMap;

    public Request(HttpServletRequest request) throws IOException, ServletException {
        {
            List<String> parameterNames = new LinkedList<>();
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                parameterNames.add(names.nextElement());
            }
            if (parameterNames.isEmpty()) {
                this.parameterNames = Collections.unmodifiableList(new ArrayList<>(0));
            } else {
                this.parameterNames = Collections.unmodifiableList(parameterNames);
            }
        }

        {
            Map<String, List<String>> parameterValues = new HashMap<>();
            Map<String, String> parameterValue = new HashMap<>();
            for (String parameterName : this.parameterNames) {
                String values[] = request.getParameterValues(parameterName);
                if (values == null || values.length == 0) {
                    parameterValues.put(parameterName, Collections.unmodifiableList(new ArrayList<>(0)));
                } else {
                    parameterValues.put(parameterName, Collections.unmodifiableList(Arrays.asList(values)));
                }
                String value = request.getParameter(parameterName);
                if (value != null) {
                    parameterValue.put(parameterName, value);
                }
            }
            if (parameterValues.isEmpty()) {
                this.parameterValues = Collections.unmodifiableMap(new HashMap<>(0));
            } else {
                this.parameterValues = Collections.unmodifiableMap(parameterValues);
            }
            if (parameterValue.isEmpty()) {
                this.parameterValue = Collections.unmodifiableMap(new HashMap<>(0));
            } else {
                this.parameterValue = Collections.unmodifiableMap(parameterValue);
            }
        }

        {
            List<Part> parts = new LinkedList<>();
            Map<String, Part> partsMap = new LinkedHashMap<>();
            for (String parameterName : this.parameterNames) {
                Part part = request.getPart(parameterName);
                if (part != null) {
                    parts.add(part);
                    partsMap.put(parameterName, part);
                }
            }
            if (parts.isEmpty()) {
                this.parts = Collections.unmodifiableList(new ArrayList<>(0));
            } else {
                this.parts = Collections.unmodifiableList(parts);
            }
            if (partsMap.isEmpty()) {
                this.partsMap = Collections.unmodifiableMap(new HashMap<>(0));
            } else {
                this.partsMap = Collections.unmodifiableMap(partsMap);
            }
        }

        {
            List<String> headerNames = new LinkedList<>();
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                headerNames.add(name);
            }
            if (headerNames.isEmpty()) {
                this.headerNames = Collections.unmodifiableList(new ArrayList<>(0));
            } else {
                this.headerNames = Collections.unmodifiableList(headerNames);
            }
        }

        {
            Map<String, List<String>> headerValues = new HashMap<>();
            Map<String, String> headerValue = new HashMap<>();
            for (String headerName : this.headerNames) {
                List<String> values = new ArrayList<>();
                {
                    Enumeration<String> ns = request.getHeaders(headerName);
                    while (ns.hasMoreElements()) {
                        values.add(ns.nextElement());
                    }
                }
                if (values.isEmpty()) {
                    headerValues.put(headerName, Collections.unmodifiableList(new ArrayList<>(0)));
                } else {
                    headerValues.put(headerName, Collections.unmodifiableList(values));
                }
                String value = request.getHeader(headerName);
                if (value != null) {
                    headerValue.put(headerName, value);
                }
            }
            if (headerValues.isEmpty()) {
                this.headerValues = Collections.unmodifiableMap(new HashMap<>(0));
            } else {
                this.headerValues = Collections.unmodifiableMap(headerValues);
            }
            if (headerValue.isEmpty()) {
                this.headerValue = Collections.unmodifiableMap(new HashMap<>(0));
            } else {
                this.headerValue = Collections.unmodifiableMap(headerValue);
            }
        }
    }

    public String getParameter(String name) {
        return this.parameterValue.get(name);
    }

    public Part getPart(String name) {
        return this.partsMap.get(name);
    }

    public List<String> getParameterValues(String name) {
        return this.parameterValues.get(name);
    }

    public List<Part> getParts() {
        return this.parts;
    }

    public List<String> getParameterNames() {
        return this.parameterNames;
    }

    public String getHeader(String name) {
        return this.headerValue.get(name);
    }

    public List<String> getHeaderNames() {
        return this.headerNames;
    }

    public List<String> getHeaderValues(String name) {
        return this.headerValues.get(name);
    }
}
