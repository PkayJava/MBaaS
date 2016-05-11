package com.angkorteam.mbaas.server.socket;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ConversationTable;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.ParticipantTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.MobileRecord;
import com.angkorteam.mbaas.model.entity.tables.records.ParticipantRecord;
import com.angkorteam.mbaas.model.entity.tables.records.UserRecord;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.UUID;

/**
 * Created by socheat on 5/8/16.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    public static final char SEPARATOR = ' ';

    public static final String COMMAND_CHAT = "CHAT";

    public static final String COMMAND_AUTHENTICATE = "AUTHENTICATE";

    public static final String COMMAND_GROUP_INITIATE = "GROUP_INITIATE";
    public static final String COMMAND_GROUP_INVITE = "GROUP_INVITE";
    public static final String COMMAND_GROUP_JOIN = "GROUP_JOIN";
    public static final String COMMAND_GROUP_LEAVE = "GROUP_LEAVE";

    public static final String COMMAND_FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String COMMAND_FRIEND_REMOVE = "FRIEND_REMOVE";
    public static final String COMMAND_FRIEND_REJECT = "FRIEND_REJECT";
    public static final String COMMAND_FRIEND_BLOCK = "FRIEND_BLOCK";

    public static final String COMMAND_OKAY = "OK";
    public static final String COMMAND_ERROR = "ERROR";

    public static ChannelGroup CLIENTS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private boolean authenticated;

    private String fullName;

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
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String msg) throws Exception {
        StringBuffer buffer = new StringBuffer();
        int index = 0;
        while (index < msg.length()) {
            Character character = msg.charAt(index);
            index++;
            if (character == ' ') {
                break;
            } else {
                buffer.append(character);
            }
        }
        String command = buffer.toString();
        if (COMMAND_GROUP_INITIATE.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_GROUP_INVITE.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_GROUP_JOIN.equals(command)) {
            groupJoin(this.userId, msg.substring(index));
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_GROUP_LEAVE.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_FRIEND_REQUEST.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_FRIEND_REMOVE.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_FRIEND_REJECT.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_FRIEND_BLOCK.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_CHAT.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_AUTHENTICATE.equals(command)) {
            String accessToken = null;
            MobileRecord mobileRecord = null;
            if (msg.length() > (COMMAND_AUTHENTICATE.length() + 1) && msg.startsWith(COMMAND_AUTHENTICATE + " ")) {
                accessToken = msg.substring(COMMAND_AUTHENTICATE.length() + 1);
            }
            if (accessToken != null && !"".equals(accessToken)) {
                MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
                mobileRecord = this.context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(mobileTable);
            }
            if (mobileRecord == null) {
                context.writeAndFlush(COMMAND_ERROR).addListener(ChannelFutureListener.CLOSE);
            } else {
                UserTable userTable = Tables.USER.as("userTable");
                UserRecord userRecord = this.context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
                this.authenticated = true;
                this.mobileId = mobileRecord.getMobileId();
                this.userId = mobileRecord.getOwnerUserId();
                this.fullName = userRecord.getFullName();
                context.writeAndFlush(COMMAND_OKAY);
            }
        } else {
            context.writeAndFlush(COMMAND_ERROR);
        }
    }

    protected void groupJoin(String userId, String conversionId) {
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        int joined = this.context.selectCount().from(participantTable).where(participantTable.USER_ID.eq(userId)).and(participantTable.CONVERSATION_ID.eq(conversionId)).fetchOneInto(int.class);
        if (joined <= 0) {
            ParticipantRecord participantRecord = this.context.newRecord(participantTable);
            participantRecord.setParticipantId(UUID.randomUUID().toString());
            participantRecord.setConversationId(conversionId);
            participantRecord.setUserId(userId);
            participantRecord.setDateCreated(new Date());
            participantRecord.store();
        }
    }

    private ServerHandler getServerHandler(Channel channel) {
        return (ServerHandler) channel.pipeline().get("handler");
    }
}
