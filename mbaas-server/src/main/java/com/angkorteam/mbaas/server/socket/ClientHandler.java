package com.angkorteam.mbaas.server.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by socheat on 5/8/16.
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {

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

    @Override
    public void handlerAdded(ChannelHandlerContext context) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String msg) throws Exception {
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        while (i < msg.length()) {
            Character character = msg.charAt(i);
            if (character == ' ') {
                break;
            } else {
                buffer.append(character);
            }
            i++;
        }
        String command = buffer.toString();
        if (COMMAND_OKAY.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else if (COMMAND_ERROR.equals(command)) {
            context.writeAndFlush(COMMAND_OKAY);
        } else {
            context.writeAndFlush(COMMAND_ERROR);
        }
    }
}
