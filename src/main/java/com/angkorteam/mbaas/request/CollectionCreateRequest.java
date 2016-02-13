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

    private BigDecimal max = new BigDecimal(Float.MAX_VALUE);
    private BigDecimal min = new BigDecimal(Float.MIN_VALUE);

    public static void main(String[] arg) throws ParseException {
//        System.out.println(Integer.MAX_VALUE);
        System.out.println(Long.MAX_VALUE);
        System.out.println(Float.MAX_VALUE);
        Gson gson = new Gson();
        System.out.println(gson.toJson(new CollectionCreateRequest()));
        DecimalFormat decimalFormat = new DecimalFormat("0.000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        System.out.println(decimalFormat.format(0.1f + 0.1f));
        System.out.println(new BigDecimal(0.1d + 0.1d + 0.1d + 0.1d + 0.1d).toString());

        System.out.println(0.1d == 0.1d);
    }
}
