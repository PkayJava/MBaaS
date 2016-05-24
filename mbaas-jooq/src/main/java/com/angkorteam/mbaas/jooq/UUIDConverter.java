package com.angkorteam.mbaas.jooq;

import org.jooq.impl.AbstractConverter;

import java.util.UUID;

/**
 * Created by socheat on 2/20/16.
 */
public class UUIDConverter extends AbstractConverter<String, UUID> {

    public UUIDConverter() {
        super(String.class, UUID.class);
    }

    @Override
    public UUID from(String databaseObject) {
        if (databaseObject != null) {
            return UUID.fromString(databaseObject);
        }
        return null;
    }

    @Override
    public String to(UUID userObject) {
        if (userObject != null) {
            return userObject.toString();
        }
        return null;
    }

}
