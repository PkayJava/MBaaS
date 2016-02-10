package com.angkorteam.mbaas.mariadb;

import java.io.Serializable;

/**
 * Created by Khauv Socheat on 2/10/2016.
 */
public class Pair implements Serializable {

    private String name;

    private Serializable value;

    public Pair(String name, Serializable value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Serializable getValue() {
        return value;
    }
}
