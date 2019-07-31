package com.example.springbootnettyserver.hander;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Deng
 * @date 2019/7/30 11:28
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: mitesofor </p>
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 管理一个全局map，保存连接进服务端的通道数量
     */
    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 有客户端连接服务器会触发此函数
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive----->");
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();

        //获取连接通道唯一标识
        ChannelId channelId = ctx.channel().id();
        System.out.println();
        //如果map中不包含此连接，就保存连接
        if (CHANNEL_MAP.containsKey(channelId)) {
            System.out.println("客户端【" + channelId + "】是连接状态，连接通道数量：" + CHANNEL_MAP.size());
        } else {
            //保存连接
            CHANNEL_MAP.put(channelId, ctx);
            System.out.println("客户端【" + channelId + "】连接netty服务器【IP：" + clientIP + "--->port:" + clientPort);
            System.out.println("连接通道数量：" + CHANNEL_MAP.size());
        }

    }

    /**
     * 有客户端终止连接服务器会触发此函数
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        ChannelId channelId = ctx.channel().id();
        if (CHANNEL_MAP.containsKey(channelId)) {
            //删除连接
            CHANNEL_MAP.remove(channelId);
            System.out.println("客户端【" + channelId + "】退出netty服务器【IP：" + clientIP + "】");
            System.out.println("连接通道数量：" + CHANNEL_MAP.size());
        }
    }

    /**
     * 有客户端发消息会触发此函数
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server channelRead......");
        System.out.println(ctx.channel().remoteAddress() + "----->Server :" + msg.toString());
        //相应客户端
        this.channelWrite(ctx.channel().id(), msg);
    }

    /**
     * @param msg       需要发送的消息内容
     * @param channelId 连接通道唯一id
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 服务端给客户端发送消息
     * @return: void
     */
    public void channelWrite(ChannelId channelId, Object msg) throws Exception {
        ChannelHandlerContext ctx = CHANNEL_MAP.get(channelId);
        if (ctx == null) {
            System.out.println("通道【" + channelId + "】不存在");
            return;
        }
        if (null == msg && "".equals(msg)) {
            System.out.println("服务端响应空的消息");
            return;
        }
        //将客户端的信息直接返回写入ctx
        ctx.write(msg);
        //刷新缓存区
        ctx.flush();
    }

    /**
     * 发生异常会触发此函数
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        System.out.println(ctx.channel().id() + "发生错误，此连接被关闭此时连接数量" + CHANNEL_MAP.size());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String socketString = ctx.channel().remoteAddress().toString();

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("client:" + socketString + "READER_IDLE 读超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("Client: " + socketString + " WRITER_IDLE 写超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("Client: " + socketString + " ALL_IDLE 总超时");
                ctx.disconnect();
            }
        }
    }
}
