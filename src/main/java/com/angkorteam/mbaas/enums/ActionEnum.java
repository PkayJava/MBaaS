package com.angkorteam.mbaas.enums;

public enum ActionEnum {
    Read(1),
    Modify(2),
    Delete(4),
    Create(8);

    private final int literal;

    ActionEnum(int literal) {
        this.literal = literal;
    }

    public final int getLiteral() {
        return literal;
    }
}
