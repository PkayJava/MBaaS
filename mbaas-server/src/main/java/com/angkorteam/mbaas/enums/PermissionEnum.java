package com.angkorteam.mbaas.enums;

public enum PermissionEnum {
    Read(1),
    Modify(2),
    Delete(4),
    Create(8);

    private final int literal;

    PermissionEnum(int literal) {
        this.literal = literal;
    }

    public final int getLiteral() {
        return literal;
    }
}
