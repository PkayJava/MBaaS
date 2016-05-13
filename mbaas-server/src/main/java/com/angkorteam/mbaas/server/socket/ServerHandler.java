package com.angkorteam.mbaas.server.socket;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.records.*;
import com.angkorteam.mbaas.server.service.MessageDTORequest;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public static final String COMMAND_FRIEND_APPROVE = "FRIEND_APPROVE";
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
            String extra = msg.substring(index);
            String[] extras = StringUtils.split(extra, ' ');
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
        } else if (COMMAND_CHAT.equals(command)) {
            String extra = msg.substring(index);
            int i = extra.indexOf(' ');
            String conversationId = extra.substring(0, i);
            String message = extra.substring(i + 1);
            chat(conversationId, message);
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

    protected void friendRequest(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord forward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(this.userId)).and(friendTable.FRIEND_USER_ID.eq(userId)).fetchOneInto(friendTable);
        if (forward == null) {
            forward = this.context.newRecord(friendTable);
            forward.setUserId(this.userId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Sent");
            forward.store();
        } else {
            forward.setUserId(this.userId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Sent");
            forward.update();
        }

        FriendRecord backward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(userId)).and(friendTable.FRIEND_USER_ID.eq(this.userId)).fetchOneInto(friendTable);
        if (backward == null) {
            backward = this.context.newRecord(friendTable);
            backward.setUserId(this.userId);
            backward.setFriendUserId(userId);
            backward.setSubscription("Confirm");
            backward.store();
        } else {
            backward.setUserId(this.userId);
            backward.setFriendUserId(userId);
            backward.setSubscription("Confirm");
            backward.update();
        }
    }

    protected void friendRemove(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        this.context.delete(friendTable).where(friendTable.USER_ID.eq(this.userId)).and(friendTable.FRIEND_USER_ID.eq(userId)).execute();
        this.context.delete(friendTable).where(friendTable.FRIEND_USER_ID.eq(this.userId)).and(friendTable.USER_ID.eq(userId)).execute();
    }

    protected void friendReject(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        this.context.delete(friendTable).where(friendTable.USER_ID.eq(this.userId)).and(friendTable.FRIEND_USER_ID.eq(userId)).execute();
        this.context.delete(friendTable).where(friendTable.FRIEND_USER_ID.eq(this.userId)).and(friendTable.USER_ID.eq(userId)).execute();
    }

    protected void friendApprove(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord forward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(this.userId)).and(friendTable.FRIEND_USER_ID.eq(userId)).fetchOneInto(friendTable);
        if (forward == null) {
            forward = this.context.newRecord(friendTable);
            forward.setUserId(this.userId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Approved");
            forward.store();
        } else {
            forward.setUserId(this.userId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Approved");
            forward.update();
        }

        FriendRecord backward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(userId)).and(friendTable.FRIEND_USER_ID.eq(this.userId)).fetchOneInto(friendTable);
        if (backward == null) {
            backward = this.context.newRecord(friendTable);
            backward.setUserId(this.userId);
            backward.setFriendUserId(userId);
            backward.setSubscription("Approved");
            backward.store();
        } else {
            backward.setUserId(this.userId);
            backward.setFriendUserId(userId);
            backward.setSubscription("Approved");
            backward.update();
        }
    }

    protected void friendBlock(String userId) {
        FriendTable friendTable = Tables.FRIEND.as("friendTable");
        FriendRecord forward = this.context.select(friendTable.fields()).from(friendTable).where(friendTable.USER_ID.eq(this.userId)).and(friendTable.FRIEND_USER_ID.eq(userId)).fetchOneInto(friendTable);
        if (forward == null) {
            forward = this.context.newRecord(friendTable);
            forward.setUserId(this.userId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Blocked");
            forward.store();
        } else {
            forward.setUserId(this.userId);
            forward.setFriendUserId(userId);
            forward.setSubscription("Blocked");
            forward.update();
        }
    }

    protected void chat(String conversationId, String message) {
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
            messageRecord.setSenderUserId(this.userId);
            messageRecord.setReceiverUserId(participantRecord.getUserId());
            messageRecord.store();
        }
    }

    protected void groupInvite(String conversationId, List<String> userIds) {
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        int joined = this.context.selectCount().from(participantTable).where(participantTable.USER_ID.eq(this.userId)).and(participantTable.CONVERSATION_ID.eq(conversationId)).fetchOneInto(int.class);
        if (joined > 0) {
            for (String userId : userIds) {
                groupJoin(userId, conversationId);
            }
        }
    }

    protected void groupLeave(String conversationId) {
        ParticipantTable participantTable = Tables.PARTICIPANT.as("participantTable");
        this.context.delete(participantTable).where(participantTable.CONVERSATION_ID.eq(conversationId)).and(participantTable.USER_ID.eq(this.userId)).execute();
        int joined = this.context.selectCount().from(participantTable).where(participantTable.CONVERSATION_ID.eq(conversationId)).fetchOneInto(int.class);
        if (joined <= 0) {
            ConversationTable conversationTable = Tables.CONVERSATION.as("conversationTable");
            this.context.delete(conversationTable).where(conversationTable.CONVERSATION_ID.eq(conversationId)).execute();
            MessageTable messageTable = Tables.MESSAGE.as("messageTable");
            this.context.delete(messageTable).where(messageTable.CONVERSATION_ID.eq(conversationId)).execute();
        }
    }

    protected void groupJoin(String conversationId) {
        groupJoin(this.userId, conversationId);
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
