package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/11/16.
 */
public class AssetProvider extends JooqProvider {

    private Table<?> assetTable;
    private Table<?> userTable;
    private TableLike<?> from;

    private final String applicationCode;

    private String applicationUserId;

    public AssetProvider(String applicationCode) {
        this(applicationCode, null);
    }

    public AssetProvider(String applicationCode, String applicationUserId) {
        this.applicationCode = applicationCode;
        this.applicationUserId = applicationUserId;
        this.assetTable = DSL.table(Jdbc.ASSET).as("assetTable");
        this.userTable = DSL.table(Jdbc.USER).as("userTable");
        this.from = this.assetTable.join(this.userTable).on(DSL.field(this.assetTable.getName() + "." + Jdbc.Asset.USER_ID, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class)));
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }

    public Field<String> getAssetId() {
        return DSL.field(this.assetTable.getName() + "." + Jdbc.Asset.ASSET_ID, String.class);
    }

    public Field<String> getApplicationUser() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.User.LOGIN, String.class);
    }

    public Field<Integer> getLength() {
        return DSL.field(this.assetTable.getName() + "." + Jdbc.Asset.LENGTH, Integer.class);
    }

    public Field<String> getMime() {
        return DSL.field(this.assetTable.getName() + "." + Jdbc.Asset.MIME, String.class);
    }

    public Field<String> getName() {
        return DSL.field(this.assetTable.getName() + "." + Jdbc.Asset.LABEL, String.class);
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        if (this.applicationUserId != null && !"".equals(this.applicationUserId)) {
            where.add(DSL.field(userTable.getName() + "." + Jdbc.User.USER_ID, String.class).eq(this.applicationUserId));
        }
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
