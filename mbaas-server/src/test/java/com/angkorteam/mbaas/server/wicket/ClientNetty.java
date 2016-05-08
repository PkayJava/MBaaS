package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.server.xmpp.ClientInitializer;
import com.angkorteam.mbaas.server.xmpp.ServerInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.handler(new ClientInitializer());
            Channel channel = clientBootstrap.connect("localhost", 5222).sync().channel();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                channel.write(reader.readLine() + "\n");
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
