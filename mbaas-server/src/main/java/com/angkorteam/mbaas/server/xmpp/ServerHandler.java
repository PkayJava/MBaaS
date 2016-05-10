package com.angkorteam.mbaas.server.xmpp;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.MobileRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.UUID;

/**
 * Created by socheat on 5/8/16.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    public static final char SEPARATOR = ' ';

    public static final String COMMAND_CHAT = "CHAT";

    public static final String COMMAND_AUTHENTICATE_REQUEST = "AUTHENTICATE_REQUEST";
    public static final String COMMAND_AUTHENTICATE_RESPONSE = "AUTHENTICATE_RESPONSE";

    public static final String COMMAND_GROUP_INITIATE = "GROUP_INITIATE";
    public static final String COMMAND_GROUP_INVITE = "GROUP_INVITE";
    public static final String COMMAND_GROUP_JOIN = "GROUP_JOIN";
    public static final String COMMAND_GROUP_LEAVE = "GROUP_LEAVE";

    public static final String COMMAND_FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String COMMAND_FRIEND_REMOVE = "FRIEND_REMOVE";
    public static final String COMMAND_FRIEND_REJECT = "FRIEND_REJECT";
    public static final String COMMAND_FRIEND_BLOCK = "FRIEND_BLOCK";

    public static ChannelGroup CLIENTS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private boolean authenticated;

    private String login;

    private String userId;

    private String mobileId;

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    public ServerHandler(final DSLContext context, final JdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) throws Exception {
        Channel channel = context.channel();
        CLIENTS.add(channel);
        context.writeAndFlush(COMMAND_AUTHENTICATE_REQUEST);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String msg) throws Exception {
        if (!authenticated) {
            String accessToken = null;
            MobileRecord mobileRecord = null;
            if (msg.length() > (COMMAND_AUTHENTICATE_RESPONSE.length() + 1) && msg.startsWith(COMMAND_AUTHENTICATE_RESPONSE + " ")) {
                accessToken = msg.substring(COMMAND_AUTHENTICATE_RESPONSE.length() + 1);
            }
            if (accessToken != null && !"".equals(accessToken)) {
                MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
                mobileRecord = this.context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(mobileTable);
            }
            if (mobileRecord == mobileRecord) {
                context.close();
            }
            UserTable userTable = Tables.USER.as("userTable");
            UserRecord userRecord = this.context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
            this.authenticated = true;
            this.mobileId = mobileRecord.getMobileId();
            this.userId = mobileRecord.getOwnerUserId();
            this.login = userRecord.getLogin();
        }
    }

    private String[] parseCommand(String msg) {
        return null;
    }

    private ServerHandler getServerHandler(Channel channel) {
        return (ServerHandler) channel.pipeline().get("handler");
    }
}
