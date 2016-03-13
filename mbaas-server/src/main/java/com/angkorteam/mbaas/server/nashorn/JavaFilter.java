package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.NashornTable;
import com.angkorteam.mbaas.model.entity.tables.records.NashornRecord;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import jdk.nashorn.api.scripting.ClassFilter;
import org.jooq.DSLContext;

import java.util.Date;

/**
 * Created by socheat on 3/12/16.
 */
public class JavaFilter implements ClassFilter {

    private final DSLContext context;
    private NashornTable nashornTable = Tables.NASHORN.as("nashornTable");

    public JavaFilter(DSLContext context) {
        this.context = context;
    }

    @Override
    public synchronized boolean exposeToScripts(String s) {
        NashornRecord nashornRecord = context.select(nashornTable.fields()).from(nashornTable).where(nashornTable.NASHORN_ID.eq(s)).fetchOneInto(nashornTable);
        if (nashornRecord == null) {
            nashornRecord = context.newRecord(nashornTable);
            nashornRecord.setNashornId(s);
            nashornRecord.setDateCreated(new Date());
            nashornRecord.setSecurity(SecurityEnum.Denied.getLiteral());
            nashornRecord.store();
            return false;
        } else {
            return SecurityEnum.Granted.getLiteral().equals(nashornRecord.getSecurity());
        }
    }

}
