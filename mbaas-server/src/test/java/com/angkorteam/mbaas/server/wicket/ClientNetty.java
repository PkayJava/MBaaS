package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.server.socket.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by socheat on 5/8/16.
 */
public class ClientNetty {

    public static void main(String[] args) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            String accessToken = "e53f285c-644b-446f-a2cb-92b1cd81a446";
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.handler(new ClientInitializer());
            Channel channel = clientBootstrap.connect("192.168.1.103", 5222).sync().channel();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                channel.writeAndFlush(reader.readLine());
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
