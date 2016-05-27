package com.angkorteam.mbaas.server.wicket;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by socheat on 5/25/16.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        File file = new File("/home/socheat/Documents/country.txt");
        for (String line : FileUtils.readLines(file)) {
            System.out.println("COUNTRIES.add(\"" + line.substring(3).trim() + "\");");
        }
    }
}
