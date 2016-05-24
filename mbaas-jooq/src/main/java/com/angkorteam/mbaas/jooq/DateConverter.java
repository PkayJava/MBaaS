package com.angkorteam.mbaas.jooq;

import org.jooq.impl.AbstractConverter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by socheat on 2/20/16.
 */
public class DateConverter extends AbstractConverter<Timestamp, Date> {

    public DateConverter() {
        super(Timestamp.class, Date.class);
    }

    @Override
    public Date from(Timestamp databaseObject) {
        if (databaseObject != null) {
            return databaseObject;
        }
        return null;
    }

    @Override
    public Timestamp to(Date userObject) {
        if (userObject != null) {
            return new Timestamp(userObject.getTime());
        }
        return null;
    }

}
