package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.List;

/**
 * Created by socheat on 3/11/16.
 */
public class FileProvider extends JooqProvider {

    private final String applicationCode;

    private Table<?> fileTable;
    private Table<?> userTable;

    private TableLike<?> from;

    public FileProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.fileTable = DSL.table(Jdbc.FILE).as("fileTable");
        this.userTable = DSL.table(Jdbc.USER).as("userTable");
        this.from = this.fileTable.join(this.userTable).on(DSL.field(this.fileTable.getName() + "." + Jdbc.File.USER_ID, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class)));
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }

    public Field<String> getFileId() {
        return DSL.field(this.fileTable.getName() + "." + Jdbc.File.FILE_ID, String.class);
    }

    public Field<String> getApplicationUser() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.User.LOGIN, String.class);
    }

    public Field<Integer> getLength() {
        return DSL.field(this.fileTable.getName() + "." + Jdbc.File.LENGTH, Integer.class);
    }

    public Field<String> getMime() {
        return DSL.field(this.fileTable.getName() + "." + Jdbc.File.MIME, String.class);
    }

    public Field<String> getName() {
        return DSL.field(this.fileTable.getName() + "." + Jdbc.File.LABEL, String.class);
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        return null;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
