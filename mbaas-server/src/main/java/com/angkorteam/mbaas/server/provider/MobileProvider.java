//package com.angkorteam.mbaas.server.provider;
//
//import com.angkorteam.framework.extension.share.provider.JooqProvider;
//import com.angkorteam.mbaas.server.Jdbc;
//import com.angkorteam.mbaas.server.wicket.Application;
//import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
//import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
//import org.jooq.*;
//import org.jooq.impl.DSL;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by socheat on 3/13/16.
// */
//public class MobileProvider extends JooqProvider {
//
//    private Table<?> mobileTable;
//    private Table<?> clientTable;
//    private Table<?> userTable;
//
//    private TableLike<?> from;
//
//    private final String applicationCode;
//
//    public MobileProvider(String applicationCode) {
//        this.applicationCode = applicationCode;
//        this.mobileTable = DSL.table(Jdbc.MOBILE).as("mobileTable");
//        this.clientTable = DSL.table(Jdbc.CLIENT).as("clientTable");
//        this.userTable = DSL.table(Jdbc.USER).as("userTable");
//        this.from = this.mobileTable.leftJoin(this.userTable).on(DSL.field(this.mobileTable.getName() + "." + Jdbc.Mobile.USER_ID, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class)))
//                .leftJoin(this.clientTable).on(DSL.field(this.mobileTable.getName() + "." + Jdbc.Mobile.CLIENT_ID, String.class).eq(DSL.field(this.clientTable.getName() + "." + Jdbc.Client.CLIENT_ID, String.class)));
//        setSort(Jdbc.Mobile.DATE_SEEN, SortOrder.DESCENDING);
//    }
//
//    public Field<String> getLogin() {
//        return DSL.field(this.userTable.getName() + "." + Jdbc.User.LOGIN, String.class);
//    }
//
//    public Field<String> getMobileId() {
//        return DSL.field(this.mobileTable.getName() + "." + Jdbc.Mobile.MOBILE_ID, String.class);
//    }
//
//    public Field<String> getClient() {
//        return DSL.field(this.clientTable.getName() + "." + Jdbc.Client.NAME, String.class).as("clientName");
//    }
//
//    public Field<String> getClientIp() {
//        return DSL.field(this.mobileTable.getName() + "." + Jdbc.Mobile.CLIENT_IP, String.class);
//    }
//
//    public Field<String> getUserAgent() {
//        return DSL.field(this.mobileTable.getName() + "." + Jdbc.Mobile.USER_AGENT, String.class);
//    }
//
//    public Field<Date> getDateCreated() {
//        return DSL.field(this.mobileTable.getName() + "." + Jdbc.Mobile.DATE_CREATED, Date.class);
//    }
//
//    public Field<Date> getDateSeen() {
//        return DSL.field(this.mobileTable.getName() + "." + Jdbc.Mobile.DATE_SEEN, Date.class);
//    }
//
//    @Override
//    protected DSLContext getDSLContext() {
//        Application application = ApplicationUtils.getApplication();
//        return application.getDSLContext(this.applicationCode);
//    }
//
//    @Override
//    protected TableLike<?> from() {
//        return this.from;
//    }
//
//    @Override
//    protected List<Condition> where() {
//        return null;
//    }
//
//    @Override
//    protected List<Condition> having() {
//        return null;
//    }
//}
