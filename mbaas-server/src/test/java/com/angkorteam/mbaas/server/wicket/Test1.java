package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.plain.security.otp.Totp;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptException;

/**
 * Created by socheat on 3/12/16.
 */
public class Test1 {

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        String hash = StringUtils.split("857fad6a-4a7d-4618-9b20-4463cad96778||NHRKVMESWTEDMJSB", "||")[1];
        Totp totp = new Totp(hash);
//        System.out.println(totp.uri("sd"));
//        System.out.println(totp.now());
//        Totp totp = new Totp(secret);
        System.out.println(totp.now());
    }
}
