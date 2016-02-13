package com.angkorteam.mbaas.request;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Khauv Socheat on 2/12/2016.
 */
public class CollectionCreateRequest extends Request {

    private String name;

    public static class Attribute {

        private String name;

        private boolean nullable = true;

        private String javaType;

    }
}
