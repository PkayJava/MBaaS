package com.angkorteam.mbaas.plain.enums;

/**
 * Created by socheat on 3/10/16.
 */
public interface AttributeExtraEnum {

    int NULLABLE = 1;

    int INDEX = 2;

    int UNIQUE = 4;

    int PRIMARY = 8;

    int FULLTEXT = 16;

    int SPATIAL = 32;

    int AUTO_INCREMENT = 64;

    int EXPOSED = 256;

    int EAV = 512;

}
