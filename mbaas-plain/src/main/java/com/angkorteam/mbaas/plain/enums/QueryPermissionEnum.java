package com.angkorteam.mbaas.plain.enums;

public enum QueryPermissionEnum {
    Read(1),
    Modify(2),
    Delete(4),
    Execute(8);

    private final int literal;

    QueryPermissionEnum(int literal) {
        this.literal = literal;
    }

    public final int getLiteral() {
        return literal;
    }
}
