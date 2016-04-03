package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

public enum CollectionPermissionEnum implements Serializable {
    Read(1),
    Attribute(2),
    Drop(4),
    Insert(8);

    private final int literal;

    CollectionPermissionEnum(int literal) {
        this.literal = literal;
    }

    public final int getLiteral() {
        return literal;
    }
}
