package com.angkorteam.mbaas.jooq;

import org.joda.time.DateTime;
import org.jooq.impl.AbstractConverter;

import java.sql.Timestamp;

/**
 * Created by socheat on 2/20/16.
 */
public class JodaTimeConverter extends AbstractConverter<Timestamp, DateTime> {

    public JodaTimeConverter() {
        super(Timestamp.class, DateTime.class);
    }

    @Override
    public DateTime from(Timestamp databaseObject) {
        if (databaseObject != null) {
            return new DateTime(databaseObject.getTime());
        }
        return null;
    }

    @Override
    public Timestamp to(DateTime userObject) {
        if (userObject != null) {
            return new Timestamp(userObject.toDate().getTime());
        }
        return null;
    }

}
