package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ResourceTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.List;

/**
 * Created by socheat on 3/13/16.
 */
public class ResourceProvider extends JooqProvider {

    private ResourceTable resourceTable = Tables.RESOURCE.as("resourceTable");

    private TableLike<?> from;

    public ResourceProvider() {
        this.from = resourceTable;
    }

    public Field<String> getResourceId() {
        return this.resourceTable.RESOURCE_ID;
    }

    public Field<String> getKey() {
        return this.resourceTable.KEY;
    }

    public Field<String> getLanguage() {
        return this.resourceTable.LANGUAGE;
    }

    public Field<String> getPage() {
        return this.resourceTable.PAGE;
    }

    public Field<String> getLabel() {
        return this.resourceTable.LABEL;
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
