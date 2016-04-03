package com.angkorteam.mbaas.plain.enums;

import java.io.Serializable;

public enum DocumentPermissionEnum implements Serializable {
    Read(1),
    Modify(2),
    Delete(4);

    private final int literal;

    DocumentPermissionEnum(int literal) {
        this.literal = literal;
    }

    public final int getLiteral() {
        return literal;
    }
}
