package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.List;

/**
 * Created by socheat on 3/11/16.
 */
public class FileProvider extends JooqProvider {

    private FileTable fileTable;

    private TableLike<?> from;

    public FileProvider() {
        this.fileTable = Tables.FILE.as("fileTable");
        this.from = this.fileTable;
    }

    @Override
    protected DSLContext getDSLContext() {
        return Spring.getBean(DSLContext.class);
    }

    public Field<String> getFileId() {
        return this.fileTable.FILE_ID;
    }

    public Field<Integer> getLength() {
        return this.fileTable.LENGTH;
    }

    public Field<String> getMime() {
        return this.fileTable.MIME;
    }

    public Field<String> getName() {
        return this.fileTable.NAME;
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
