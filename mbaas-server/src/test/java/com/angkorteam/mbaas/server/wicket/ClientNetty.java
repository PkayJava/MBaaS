package com.angkorteam.mbaas.server.wicket;

import java.io.IOException;

/**
 * Created by socheat on 5/8/16.
 */
public class ClientNetty {

    public static void main(String[] args) throws InterruptedException, IOException {

        System.out.println("ក".getBytes().length);
        String pp = "k ស k";
        for (byte b : pp.getBytes()) {
            System.out.println((char) b);
        }
        Character p = 'ក';
        System.out.println(pp.charAt(2));
//
//        EventLoopGroup group = new NioEventLoopGroup();
//
//        try {
//            Bootstrap clientBootstrap = new Bootstrap();
//            clientBootstrap.group(group);
//            clientBootstrap.channel(NioSocketChannel.class);
//            clientBootstrap.handler(new ClientInitializer());
//            Channel channel = clientBootstrap.connect("192.168.1.115", 5222).sync().channel();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            while (true) {
//                channel.writeAndFlush(reader.readLine());
//            }
//        } finally {
//            group.shutdownGracefully();
//        }
    }
}
