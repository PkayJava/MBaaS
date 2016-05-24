package com.angkorteam.mbaas.plain.enums;

/**
 * Created by socheat on 5/7/16.
 */
public enum AskSubscriptionTypeEnum {

    /**
     * no pending subscriptions
     */
    NOT_SET(null),
    /**
     * new subscription request to be confirmed by the contact
     */
    ASK_SUBSCRIBE("subscribe"),
    /**
     * confirmed subscription request
     */
    ASK_SUBSCRIBED("subscribed");

    private final String literal;

    AskSubscriptionTypeEnum(String literal) {
        this.literal = literal;
    }

    public final String getLiteral() {
        return literal;
    }

}
