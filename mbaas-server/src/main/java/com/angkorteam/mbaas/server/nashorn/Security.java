package com.angkorteam.mbaas.server.nashorn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by socheat on 3/18/16.
 */
public class Security {
//    private final HttpServletRequest request;

    public static void main(String[] args) {
        String sql = ":selec * from where pp =:ww_w";
        Pattern pattern = Pattern.compile("\\:[a-z]{1}[a-z0-9\\_]*");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
