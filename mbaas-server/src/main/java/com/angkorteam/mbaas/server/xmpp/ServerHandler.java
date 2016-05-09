package com.angkorteam.mbaas.server.xmpp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by socheat on 5/8/16.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private boolean authenticated;

    private String login;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        for (Channel client : CHANNELS) {
            client.writeAndFlush("[SERVER] - " + channel.remoteAddress() + " has joined");
        }
        channel.writeAndFlush("test").sync().channel();
        System.out.println("sssssssssssss");
        channel.pipeline().get("handler");
        CHANNELS.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("[RELIEVED] - " + msg);
    }

    private ServerHandler getServerHandler(Channel channel) {
        return (ServerHandler) channel.pipeline().get("handler");
    }
}
