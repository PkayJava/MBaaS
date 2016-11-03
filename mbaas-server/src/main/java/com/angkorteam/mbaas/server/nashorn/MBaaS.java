//package com.angkorteam.mbaas.server.nashorn;
//
//import com.angkorteam.mbaas.plain.Identity;
//import org.jooq.DSLContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.authentication.BadCredentialsException;
//
///**
// * Created by socheat on 3/12/16.
// */
//public class MBaaS {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);
//
//    public final Console Console;
//
//    public final Database Database;
//
////    public final Permission Permission;
//
//    private final Request request;
//
//    public final Identity Identity;
//
//    public final Http Http;
//
//    public MBaaS(DSLContext context, Identity identity, JdbcTemplate jdbcTemplate, Request request) {
//        this.Console = new Console(LOGGER);
//        this.Identity = identity;
//        this.Database = new Database(context, identity, jdbcTemplate, this);
////        this.Permission = new Permission(this, identity, context, jdbcTemplate);
//        this.request = request;
//        this.Http = new Http();
//    }
//
//    public void promptLogin() {
//        throw new BadCredentialsException("authentication is need");
//    }
//
//}
