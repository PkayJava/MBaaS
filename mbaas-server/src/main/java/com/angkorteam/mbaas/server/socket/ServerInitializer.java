package com.angkorteam.mbaas.server.socket;

import com.google.gson.Gson;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.Charset;

/**
 * Created by socheat on 5/8/16.
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final DSLContext context;

    private final JdbcTemplate jdbcTemplate;

    private final Gson gson;

    public ServerInitializer(final DSLContext context, final JdbcTemplate jdbcTemplate, final Gson gson) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.gson = gson;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new StringDecoder(Charset.forName("UTF-8")));
        pipeline.addLast("encoder", new StringEncoder(Charset.forName("UTF-8")));
        pipeline.addLast("handler", new ServerHandler(this.context, this.jdbcTemplate, this.gson));
    }
}
