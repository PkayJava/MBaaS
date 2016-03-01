package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

/**
 * Created by Khauv Socheat on 2/10/2016.
 */
public enum ScopeEnum implements Serializable {

    VisibleByTheUser("Private"),
    VisibleByFriend("Friend"),
    VisibleByRegisteredUser("Registered"),
    VisibleByAnonymousUser("Public");

    private final String literal;

    ScopeEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }
}
