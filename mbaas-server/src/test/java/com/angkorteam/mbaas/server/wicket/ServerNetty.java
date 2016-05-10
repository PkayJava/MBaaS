package com.angkorteam.mbaas.server.wicket;

import com.angkorteam.mbaas.server.xmpp.ServerHandler;
import com.angkorteam.mbaas.server.xmpp.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Scanner;

/**
 * Created by socheat on 5/8/16.
 */
public class ServerNetty {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());
            b.bind(5222).sync();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                ServerHandler.CLIENTS.writeAndFlush(message);
            }
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
