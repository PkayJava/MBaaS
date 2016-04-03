package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by socheat on 3/30/16.
 */
public enum GrantTypeEnum implements Serializable {

    Authorization("Authorization", "Authorization Code Grant"),
    Implicit("Implicit", "Implicit Grant"),
    Password("Password", "Resource Owner Password Credentials Grant"),
    Client("Client", "Client Credentials Grant");

    private final String literal;

    private final String description;

    GrantTypeEnum(String literal, String description) {
        this.literal = literal;
        this.description = description;
    }

    public final String getLiteral() {
        return literal;
    }

    public final String getDescription() {
        return description;
    }
}
