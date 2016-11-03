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
// * Created by socheat on 3/1/16.
// */
//public class SocketProvider extends JooqProvider {
//
//    private final String applicationCode;
//
//    private TableLike<?> from;
//
//    private Table<?> userTable;
//
//    private Table<?> socketTable;
//
//    public SocketProvider(String applicationCode) {
//        this.applicationCode = applicationCode;
//        this.userTable = DSL.table(Jdbc.USER).as("userTable");
//        this.socketTable = DSL.table(Jdbc.SOCKET).as("socketTable");
//        this.from = this.socketTable.leftJoin(this.userTable).on(DSL.field(this.socketTable.getName() + "." + Jdbc.Socket.USER_ID, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class)));
//        setSort("dateCreated", SortOrder.DESCENDING);
//    }
//
//    @Override
//    protected DSLContext getDSLContext() {
//        Application application = ApplicationUtils.getApplication();
//        return application.getDSLContext(this.applicationCode);
//    }
//
//    public Field<String> getLogin() {
//        return DSL.field(this.userTable.getName() + "." + Jdbc.User.LOGIN, String.class);
//    }
//
//    public Field<String> getApplicationUserId() {
//        return DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class);
//    }
//
//    public Field<String> getFullName() {
//        return DSL.field(this.userTable.getName() + "." + Jdbc.User.FULL_NAME, String.class);
//    }
//
//    public Field<String> getSessionId() {
//        return DSL.field(this.socketTable.getName() + "." + Jdbc.Socket.SESSION_ID, String.class);
//    }
//
//    public Field<String> getSocketId() {
//        return DSL.field(this.socketTable.getName() + "." + Jdbc.Socket.SOCKET_ID, String.class);
//    }
//
//    public Field<Date> getDateCreated() {
//        return DSL.field(this.socketTable.getName() + "." + Jdbc.Socket.DATE_CREATED, Date.class);
//    }
//
//    public Field<String> getResourceName() {
//        return DSL.field(this.socketTable.getName() + "." + Jdbc.Socket.RESOURCE_NAME, String.class);
//    }
//
//    public Field<String> getPageKey() {
//        return DSL.field(this.socketTable.getName() + "." + Jdbc.Socket.PAGE_KEY, String.class);
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
