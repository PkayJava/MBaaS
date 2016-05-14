package com.angkorteam.mbaas.server.socket;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * Created by socheat on 5/8/16.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    public static final char SEPARATOR = ' ';

    public static final String COMMAND_MESSAGE_PRIVATE = "MESSAGE_PRIVATE";
    public static final String COMMAND_MESSAGE_GROUP = "MESSAGE_GROUP";
    public static final String COMMAND_MESSAGE_NOTIFICATION = "MESSAGE_NOTIFICATION";

    public static final String COMMAND_AUTHENTICATE = "AUTHENTICATE";

    public static final String COMMAND_GROUP_INITIATE = "GROUP_INITIATE";
    public static final String COMMAND_GROUP_INVITE = "GROUP_INVITE";
    public static final String COMMAND_GROUP_JOIN = "GROUP_JOIN";
    public static final String COMMAND_GROUP_LEAVE = "GROUP_LEAVE";

    public static final String COMMAND_FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String COMMAND_FRIEND_APPROVE = "FRIEND_APPROVE";
    public static final String COMMAND_FRIEND_REMOVE = "FRIEND_REMOVE";
    public static final String COMMAND_FRIEND_REJECT = "FRIEND_REJECT";
    public static final String COMMAND_FRIEND_BLOCK = "FRIEND_BLOCK";

    public static final String COMMAND_OKAY = "OK";
    public static final String COMMAND_ERROR = "ERROR";

    public static ChannelGroup CLIENTS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private boolean authenticated;

    private String fullName;

    private String ownerUserId;

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
        LOGGER.info("remote address {} msg {}", context.channel().remoteAddress(), msg);
        StringBuffer buffer = new StringBuffer();
        int index = 0;
        while (index < msg.length()) {
            Character character = msg.charAt(index);
            index++;
            if (character == SEPARATOR) {
                break;
            } else {
                buffer.append(character);
            }
        }
        String command = buffer.toString();
        if (COMMAND_GROUP_INITIATE.equals(command)) {
            String userId = msg.substring(index);
            groupInitiate(userId);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_GROUP_INVITE.equals(command)) {
            String extra = msg.substring(index);
            String[] extras = StringUtils.split(extra, SEPARATOR);
            String conversationId = extras[0];
            List<String> userIds = new ArrayList<>();
            for (int i = 1; i < extras.length; i++) {
                userIds.add(extras[i]);
            }
            groupInvite(conversationId, userIds);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_GROUP_JOIN.equals(command)) {
            String conversationId = msg.substring(index);
            groupJoin(conversationId);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_GROUP_LEAVE.equals(command)) {
            String conversationId = msg.substring(index);
            groupLeave(conversationId);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_FRIEND_REQUEST.equals(command)) {
            String userId = msg.substring(index);
            friendRequest(userId);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_FRIEND_REMOVE.equals(command)) {
            String userId = msg.substring(index);
            friendRemove(userId);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_FRIEND_REJECT.equals(command)) {
            String userId = msg.substring(index);
            friendReject(userId);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_FRIEND_BLOCK.equals(command)) {
            String userId = msg.substring(index);
            friendBlock(userId);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_FRIEND_APPROVE.equals(command)) {
            String userId = msg.substring(index);
            friendApprove(userId);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_MESSAGE_GROUP.equals(command)) {
            String extra = msg.substring(index);
            int i = extra.indexOf(SEPARATOR);
            String conversationId = extra.substring(0, i);
            String message = extra.substring(i + 1);
            messageGroup(conversationId, message);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_MESSAGE_PRIVATE.equals(command)) {
            String extra = msg.substring(index);
            int i = extra.indexOf(SEPARATOR);
            String userId = extra.substring(0, i);
            String message = extra.substring(i + 1);
            messagePrivate(userId, message);
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_AUTHENTICATE.equals(command)) {
            String accessToken = msg.substring(index);
            MobileRecord mobileRecord = null;
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
                this.ownerUserId = mobileRecord.getOwnerUserId();
                this.fullName = userRecord.getFullName();
                context.writeAndFlush(COMMAND_OKAY);
            }
        } else {
            context.writeAndFlush(COMMAND_ERROR);
        }
    }

    protected String groupInitiate(String userId) {
        ConversationTable conversationTable = Tables.CONVERSATION.as("conversationTable");
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        List<String> tempConversionIds = this.context.select(DSL.max(participantTable.CONVERSATION_ID)).from(participantTable).where(participantTable.USER_ID.in(this.ownerUserId, userId)).groupBy(participantTable.CONVERSATION_ID).having(DSL.count(participantTable.CONVERSATION_ID).greaterOrEqual(2)).fetchInto(String.class);
        ConversationRecord conversationRecord = this.context.select(conversationTable.fields()).from(conversationTable).where(conversationTable.CONVERSATION_ID.in(tempConversionIds)).groupBy(conversationTable.CONVERSATION_ID).having(DSL.count(conversationTable.CONVERSATION_ID).eq(2)).fetchOneInto(conversationTable);
        if (conversationRecord == null) {
            String conversationId = UUID.randomUUID().toString();
            conversationRecord = this.context.newRecord(conversationTable);
            conversationRecord.setDateCreated(new Date());
            conversationRecord.setConversationId(conversationId);
            conversationRecord.store();
            List<String> userIds = Arrays.asList(this.ownerUserId, userId);
            for (String id : userIds) {
                ParticipantRecord participantRecord = this.context.newRecord(participantTable);
                participantRecord.setParticipantId(UUID.randomUUID().toString());
                participantRecord.setConversationId(conversationId);
                participantRecord.setDateCreated(new Date());
                participantRecord.setUserId(id);
                participantRecord.store();
            }
            return conversationId;
        } else {
            return conversationRecord.getConversationId();
        }
    }


    protected void friendRequest(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord forward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(userId)).fetchOneInto(friendTable);
        if (forward == null) {
            forward = this.context.newRecord(friendTable);
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Sent");
            forward.store();
        } else {
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Sent");
            forward.update();
        }

        FriendRecord backward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(userId)).and(friendTable.FRIEND_USER_ID.eq(this.ownerUserId)).fetchOneInto(friendTable);
        if (backward == null) {
            backward = this.context.newRecord(friendTable);
            backward.setUserId(this.ownerUserId);
            backward.setFriendUserId(userId);
            backward.setSubscription("Confirm");
            backward.store();
        } else {
            backward.setUserId(this.ownerUserId);
            backward.setFriendUserId(userId);
            backward.setSubscription("Confirm");
            backward.update();
        }
    }

    protected void friendRemove(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        this.context.delete(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(userId)).execute();
        this.context.delete(friendTable).where(friendTable.FRIEND_USER_ID.eq(this.ownerUserId)).and(friendTable.USER_ID.eq(userId)).execute();
    }

    protected void friendReject(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        this.context.delete(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(userId)).execute();
        this.context.delete(friendTable).where(friendTable.FRIEND_USER_ID.eq(this.ownerUserId)).and(friendTable.USER_ID.eq(userId)).execute();
    }

    protected void friendApprove(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord forward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(userId)).fetchOneInto(friendTable);
        if (forward == null) {
            forward = this.context.newRecord(friendTable);
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Approved");
            forward.store();
        } else {
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Approved");
            forward.update();
        }

        FriendRecord backward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(userId)).and(friendTable.FRIEND_USER_ID.eq(this.ownerUserId)).fetchOneInto(friendTable);
        if (backward == null) {
            backward = this.context.newRecord(friendTable);
            backward.setUserId(this.ownerUserId);
            backward.setFriendUserId(userId);
            backward.setSubscription("Approved");
            backward.store();
        } else {
            backward.setUserId(this.ownerUserId);
            backward.setFriendUserId(userId);
            backward.setSubscription("Approved");
            backward.update();
        }
    }

    protected void friendBlock(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord forward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(userId)).fetchOneInto(friendTable);
        if (forward == null) {
            forward = this.context.newRecord(friendTable);
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Blocked");
            forward.store();
        } else {
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Blocked");
            forward.update();
        }
    }

    protected void messageGroup(String conversationId, String message) {
        message(conversationId, message);
    }

    protected void message(String conversationId, String message) {
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        List<ParticipantRecord> participantRecords = this.context.select(participantTable.fields()).from(participantTable).where(participantTable.CONVERSATION_ID.eq(conversationId)).fetchInto(participantTable);
        MessageTable messageTable = Tables.MESSAGE.as("messageTable");
        for (ParticipantRecord participantRecord : participantRecords) {
            MessageRecord messageRecord = this.context.newRecord(messageTable);
            messageRecord.setMessageId(UUID.randomUUID().toString());
            messageRecord.setDateCreated(new Date());
            messageRecord.setRead(false);
            messageRecord.setBody(message);
            messageRecord.setConversationId(conversationId);
            messageRecord.setSenderUserId(this.ownerUserId);
            messageRecord.setReceiverUserId(participantRecord.getUserId());
            messageRecord.store();
            for (Channel channel : CLIENTS) {
                ServerHandler handler = getServerHandler(channel);
                if (handler.ownerUserId.equals(participantRecord.getUserId())) {
                    channel.writeAndFlush(COMMAND_MESSAGE_NOTIFICATION + SEPARATOR + this.ownerUserId + SEPARATOR + message);
                }
            }
        }
    }

    protected void messagePrivate(String userId, String message) {
        String conversationId = groupInitiate(userId);
        message(conversationId, message);
    }

    protected void groupInvite(String conversationId, List<String> userIds) {
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        int joined = this.context.selectCount().from(participantTable).where(participantTable.USER_ID.eq(this.ownerUserId)).and(participantTable.CONVERSATION_ID.eq(conversationId)).fetchOneInto(int.class);
        if (joined > 0) {
            for (String userId : userIds) {
                groupJoin(userId, conversationId);
            }
        }
    }

    protected void groupLeave(String conversationId) {
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        this.context.delete(participantTable).where(participantTable.CONVERSATION_ID.eq(conversationId)).and(participantTable.USER_ID.eq(this.ownerUserId)).execute();
        int joined = this.context.selectCount().from(participantTable).where(participantTable.CONVERSATION_ID.eq(conversationId)).fetchOneInto(int.class);
        if (joined <= 0) {
            ConversationTable conversationTable = Tables.CONVERSATION.as("conversationTable");
            this.context.delete(conversationTable).where(conversationTable.CONVERSATION_ID.eq(conversationId)).execute();
            MessageTable messageTable = Tables.MESSAGE.as("messageTable");
            this.context.delete(messageTable).where(messageTable.CONVERSATION_ID.eq(conversationId)).execute();
        }
    }

    protected void groupJoin(String conversationId) {
        groupJoin(this.ownerUserId, conversationId);
    }

    protected void groupJoin(String userId, String conversationId) {
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        int joined = this.context.selectCount().from(participantTable).where(participantTable.USER_ID.eq(userId)).and(participantTable.CONVERSATION_ID.eq(conversationId)).fetchOneInto(int.class);
        if (joined <= 0) {
            ParticipantRecord participantRecord = this.context.newRecord(participantTable);
            participantRecord.setParticipantId(UUID.randomUUID().toString());
            participantRecord.setConversationId(conversationId);
            participantRecord.setUserId(userId);
            participantRecord.setDateCreated(new Date());
            participantRecord.store();
        }
    }

    private ServerHandler getServerHandler(Channel channel) {
        return (ServerHandler) channel.pipeline().get("handler");
    }
}
