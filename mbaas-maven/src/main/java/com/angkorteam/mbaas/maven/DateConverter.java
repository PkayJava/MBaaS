package com.angkorteam.mbaas.maven;

import org.jooq.impl.AbstractConverter;

/**
 * Created by socheat on 2/20/16.
 */
public class DateConverter<T, U> extends AbstractConverter<T, U> {

    public DateConverter(Class<T> fromType, Class<U> toType) {
        super(fromType, toType);
    }

    @Override
    public U from(T databaseObject) {
        return null;
    }

    @Override
    public T to(U userObject) {
        return null;
    }
}
