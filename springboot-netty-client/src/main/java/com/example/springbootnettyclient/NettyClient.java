package com.example.springbootnettyclient;

import com.example.springbootnettyclient.clientEnCode.ClientChannelInitializer;
import com.example.springbootnettyclient.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mr.Deng
 * @date 2019/7/30 11:44
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: mitesofor </p>
 */
public class NettyClient implements Runnable {
    static final String HOST = System.getProperty("host", "192.168.20.206");
    static final int PORT = Integer.parseInt(System.getProperty("port", "7000"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NettyClient(String content) {
        this.content = content;
    }

//    public static void sendMessage(String content) throws InterruptedException {
//        // Configure the client.
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(group)
//                    .channel(NioSocketChannel.class)
//                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        public void initChannel(SocketChannel ch) throws Exception {
//                            ChannelPipeline p = ch.pipeline();
//                            p.addLast("decoder", new StringDecoder());
//                            p.addLast("encoder", new StringEncoder());
//                            p.addLast(new ClientHandler());
//                        }
//                    });
//
//            ChannelFuture future = b.connect(HOST, PORT).sync();
//            future.channel().writeAndFlush(content);
//            future.channel().closeFuture().sync();
//        } finally {
//            group.shutdownGracefully();
//        }
//    }

    @Override
    public void run() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            int num = 0;
            boolean boo = true;
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientChannelInitializer() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new StringDecoder());
                            p.addLast("encoder", new StringEncoder());
                            p.addLast(new ClientHandler());
                        }
                    });
            ChannelFuture future = b.connect(HOST, PORT).sync();
            while (boo) {
                num++;
                future.channel().writeAndFlush(content + "--" + simpleDateFormat.format(new Date()));
                try { //休眠一段时间
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //每一条线程向服务端发送的次数
                if (num == 10) {
                    boo = false;
                }
            }
            System.out.println(content + "-----------------------------" + num);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}
