package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
public class CollectionProvider extends JooqProvider<CollectionItemModel, CollectionFilterModel> {

    private Field<Integer> count;

    private TableLike<?> from;

    private CollectionTable collectionTable;

    public CollectionProvider() {
        super(CollectionItemModel.class, CollectionFilterModel.class);
        this.collectionTable = Tables.COLLECTION.as("collectionTable");
        {
            DSLContext context = getDSLContext();
            List<String> names = context.select(collectionTable.NAME).from(collectionTable).fetchInto(String.class);
            CaseValueStep<String> choose = DSL.choose(collectionTable.NAME);
            String first = names.remove(0);
            CaseWhenStep when = choose.when(first, context.selectCount().from("`" + first + "`").asField());
            for (String name : names) {
                when = when.when(name, context.selectCount().from("`" + name + "`").asField());
            }
            count = when;
        }
        this.from = collectionTable;
    }

    public Field<Integer> getCount() {
        return this.count;
    }

    public Field<String> getName() {
        return this.collectionTable.NAME;
    }

    public Field<String> getCollectionId() {
        return this.collectionTable.COLLECTION_ID;
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
