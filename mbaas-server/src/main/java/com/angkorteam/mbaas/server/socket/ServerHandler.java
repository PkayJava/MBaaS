package com.angkorteam.mbaas.server.socket;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.server.socket.command.*;
import com.google.gson.Gson;
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

    private final Gson gson;

    public ServerHandler(final DSLContext context, final JdbcTemplate jdbcTemplate, final Gson gson) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.gson = gson;
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
        String json = msg.substring(index);
        if (COMMAND_GROUP_INITIATE.equals(command)) {
            GroupInitiate groupInitiate = this.gson.fromJson(json, GroupInitiate.class);
            groupInitiate(groupInitiate);
            Command socketResponse = new Command();
            socketResponse.setUuid(groupInitiate.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_GROUP_INVITE.equals(command)) {
            GroupInvite groupInvite = this.gson.fromJson(json, GroupInvite.class);
            groupInvite(groupInvite);
            Command socketResponse = new Command();
            socketResponse.setUuid(groupInvite.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_GROUP_JOIN.equals(command)) {
            GroupJoin groupJoin = this.gson.fromJson(json, GroupJoin.class);
            groupJoin(groupJoin);
            Command socketResponse = new Command();
            socketResponse.setUuid(groupJoin.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_GROUP_LEAVE.equals(command)) {
            GroupLeave groupLeave = this.gson.fromJson(json, GroupLeave.class);
            groupLeave(groupLeave);
            Command socketResponse = new Command();
            socketResponse.setUuid(groupLeave.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_FRIEND_REQUEST.equals(command)) {
            FriendRequest friendRequest = this.gson.fromJson(json, FriendRequest.class);
            friendRequest(friendRequest);
            Command socketResponse = new Command();
            socketResponse.setUuid(friendRequest.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_FRIEND_REMOVE.equals(command)) {
            FriendRemove friendRemove = this.gson.fromJson(json, FriendRemove.class);
            friendRemove(friendRemove);
            Command socketResponse = new Command();
            socketResponse.setUuid(friendRemove.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_FRIEND_REJECT.equals(command)) {
            FriendReject friendReject = this.gson.fromJson(json, FriendReject.class);
            friendReject(friendReject);
            Command socketResponse = new Command();
            socketResponse.setUuid(friendReject.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_FRIEND_BLOCK.equals(command)) {
            FriendBlock friendBlock = this.gson.fromJson(json, FriendBlock.class);
            friendBlock(friendBlock);
            Command socketResponse = new Command();
            socketResponse.setUuid(friendBlock.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_FRIEND_APPROVE.equals(command)) {
            FriendApprove friendApprove = this.gson.fromJson(json, FriendApprove.class);
            friendApprove(friendApprove);
            Command socketResponse = new Command();
            socketResponse.setUuid(friendApprove.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_MESSAGE_GROUP.equals(command)) {
            MessageGroup messageGroup = this.gson.fromJson(json, MessageGroup.class);
            messageGroup(messageGroup);
            Command socketResponse = new Command();
            socketResponse.setUuid(messageGroup.getUuid());
            context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
        } else if (COMMAND_MESSAGE_PRIVATE.equals(command)) {
            MessagePrivate messagePrivate = this.gson.fromJson(json, MessagePrivate.class);
            if (!messagePrivate.getUserId().equals(this.ownerUserId)) {
                messagePrivate(messagePrivate);
                Command socketResponse = new Command();
                socketResponse.setUuid(messagePrivate.getUuid());
                context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
            }
        } else if (COMMAND_AUTHENTICATE.equals(command)) {
            Authenticate authenticate = this.gson.fromJson(json, Authenticate.class);
            String accessToken = authenticate.getAccessToken();
            MobileRecord mobileRecord = null;
            if (accessToken != null && !"".equals(accessToken)) {
                MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
                mobileRecord = this.context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(mobileTable);
            }
            if (mobileRecord == null) {
                Command socketResponse = new Command();
                socketResponse.setUuid(authenticate.getUuid());
                context.writeAndFlush(COMMAND_ERROR + SEPARATOR + this.gson.toJson(socketResponse)).addListener(ChannelFutureListener.CLOSE);
            } else {
                UserTable userTable = Tables.USER.as("userTable");
                UserRecord userRecord = this.context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
                this.authenticated = true;
                this.mobileId = mobileRecord.getMobileId();
                this.ownerUserId = mobileRecord.getOwnerUserId();
                this.fullName = userRecord.getFullName();
                Command socketResponse = new Command();
                socketResponse.setUuid(authenticate.getUuid());
                context.writeAndFlush(COMMAND_OKAY + SEPARATOR + this.gson.toJson(socketResponse));
            }
        } else {
            context.writeAndFlush(COMMAND_ERROR);
        }
    }

    protected String groupInitiate(GroupInitiate groupInitiate) {
        ConversationTable conversationTable = Tables.CONVERSATION.as("conversationTable");
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        List<String> tempConversionIds = this.context.select(DSL.max(participantTable.CONVERSATION_ID)).from(participantTable).where(participantTable.USER_ID.in(this.ownerUserId, groupInitiate.getUserId())).groupBy(participantTable.CONVERSATION_ID).having(DSL.count(participantTable.CONVERSATION_ID).greaterOrEqual(2)).fetchInto(String.class);
        String conversationId = this.context.select(participantTable.CONVERSATION_ID).from(participantTable).where(participantTable.CONVERSATION_ID.in(tempConversionIds)).groupBy(participantTable.CONVERSATION_ID).having(DSL.count(conversationTable.CONVERSATION_ID).eq(2)).fetchOneInto(String.class);
        ConversationRecord conversationRecord = null;
        if (conversationId != null && !"".equals(conversationId)) {
            conversationRecord = this.context.select(conversationTable.fields()).from(conversationTable).where(conversationTable.CONVERSATION_ID.eq(conversationId)).fetchOneInto(conversationTable);
        }
        if (conversationRecord == null) {
            conversationId = UUID.randomUUID().toString();
            conversationRecord = this.context.newRecord(conversationTable);
            conversationRecord.setDateCreated(new Date());
            conversationRecord.setConversationId(conversationId);
            conversationRecord.store();
            List<String> userIds = Arrays.asList(this.ownerUserId, groupInitiate.getUserId());
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


    protected void friendRequest(FriendRequest friendRequest) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord forward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(friendRequest.getUserId())).fetchOneInto(friendTable);
        if (forward == null) {
            forward = this.context.newRecord(friendTable);
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(friendRequest.getUserId());
            forward.setSubscription("Sent");
            forward.store();
        } else {
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(friendRequest.getUserId());
            forward.setSubscription("Sent");
            forward.update();
        }

        FriendRecord backward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(friendRequest.getUserId())).and(friendTable.FRIEND_USER_ID.eq(this.ownerUserId)).fetchOneInto(friendTable);
        if (backward == null) {
            backward = this.context.newRecord(friendTable);
            backward.setUserId(this.ownerUserId);
            backward.setFriendUserId(friendRequest.getUserId());
            backward.setSubscription("Confirm");
            backward.store();
        } else {
            backward.setUserId(this.ownerUserId);
            backward.setFriendUserId(friendRequest.getUserId());
            backward.setSubscription("Confirm");
            backward.update();
        }
    }

    protected void friendRemove(FriendRemove friendRemove) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        this.context.delete(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(friendRemove.getUserId())).execute();
        this.context.delete(friendTable).where(friendTable.FRIEND_USER_ID.eq(this.ownerUserId)).and(friendTable.USER_ID.eq(friendRemove.getUserId())).execute();
    }

    protected void friendReject(FriendReject friendReject) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        this.context.delete(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(friendReject.getUserId())).execute();
        this.context.delete(friendTable).where(friendTable.FRIEND_USER_ID.eq(this.ownerUserId)).and(friendTable.USER_ID.eq(friendReject.getUserId())).execute();
    }

    protected void friendApprove(FriendApprove friendApprove) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord forward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(friendApprove.getUserId())).fetchOneInto(friendTable);
        if (forward == null) {
            forward = this.context.newRecord(friendTable);
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(friendApprove.getUserId());
            forward.setSubscription("Approved");
            forward.store();
        } else {
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(friendApprove.getUserId());
            forward.setSubscription("Approved");
            forward.update();
        }

        FriendRecord backward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(friendApprove.getUserId())).and(friendTable.FRIEND_USER_ID.eq(this.ownerUserId)).fetchOneInto(friendTable);
        if (backward == null) {
            backward = this.context.newRecord(friendTable);
            backward.setUserId(this.ownerUserId);
            backward.setFriendUserId(friendApprove.getUserId());
            backward.setSubscription("Approved");
            backward.store();
        } else {
            backward.setUserId(this.ownerUserId);
            backward.setFriendUserId(friendApprove.getUserId());
            backward.setSubscription("Approved");
            backward.update();
        }
    }

    protected void friendBlock(FriendBlock friendBlock) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord forward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(this.ownerUserId)).and(friendTable.FRIEND_USER_ID.eq(friendBlock.getUserId())).fetchOneInto(friendTable);
        if (forward == null) {
            forward = this.context.newRecord(friendTable);
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(friendBlock.getUserId());
            forward.setSubscription("Blocked");
            forward.store();
        } else {
            forward.setUserId(this.ownerUserId);
            forward.setFriendUserId(friendBlock.getUserId());
            forward.setSubscription("Blocked");
            forward.update();
        }
    }

    protected void messageGroup(MessageGroup messageGroup) {
        message(messageGroup.getUuid(), messageGroup.getConversationId(), messageGroup.getMessage());
    }

    protected void message(String uuid, String conversationId, String message) {
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
                    MessageNotification messageNotification = new MessageNotification();
                    messageNotification.setUserId(this.ownerUserId);
                    messageNotification.setMessage(message);
                    messageNotification.setUuid(uuid);
                    channel.writeAndFlush(COMMAND_MESSAGE_NOTIFICATION + SEPARATOR + this.gson.toJson(messageNotification));
                }
            }
        }
    }

    protected void messagePrivate(MessagePrivate messagePrivate) {
        GroupInitiate groupInitiate = new GroupInitiate();
        groupInitiate.setUserId(messagePrivate.getUserId());
        String conversationId = groupInitiate(groupInitiate);
        message(messagePrivate.getUuid(), conversationId, messagePrivate.getMessage());
    }

    protected void groupInvite(GroupInvite groupInvite) {
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        int joined = this.context.selectCount().from(participantTable).where(participantTable.USER_ID.eq(this.ownerUserId)).and(participantTable.CONVERSATION_ID.eq(groupInvite.getConversationId())).fetchOneInto(int.class);
        if (joined > 0) {
            for (String userId : groupInvite.getUserId()) {
                groupJoin(userId, groupInvite.getConversationId());
            }
        }
    }

    protected void groupLeave(GroupLeave groupLeave) {
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        this.context.delete(participantTable).where(participantTable.CONVERSATION_ID.eq(groupLeave.getConversationId())).and(participantTable.USER_ID.eq(this.ownerUserId)).execute();
        int joined = this.context.selectCount().from(participantTable).where(participantTable.CONVERSATION_ID.eq(groupLeave.getConversationId())).fetchOneInto(int.class);
        if (joined <= 0) {
            ConversationTable conversationTable = Tables.CONVERSATION.as("conversationTable");
            this.context.delete(conversationTable).where(conversationTable.CONVERSATION_ID.eq(groupLeave.getConversationId())).execute();
            MessageTable messageTable = Tables.MESSAGE.as("messageTable");
            this.context.delete(messageTable).where(messageTable.CONVERSATION_ID.eq(groupLeave.getConversationId())).execute();
        }
    }

    protected void groupJoin(GroupJoin groupJoin) {
        groupJoin(this.ownerUserId, groupJoin.getConversationId());
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
