package com.example.springbootnettyclient.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Deng
 * @date 2019/7/30 11:49
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: mitesofor </p>
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 计算有多少客户端接入，第一个string为客户端ip
     */
    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> CLIENT_MAP = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        CLIENT_MAP.put(ctx.channel().id(), ctx);
        System.out.println("ClientHandler Active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        System.out.println("服务端终止了服务");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("--------");
        System.out.println("回写数据:" + msg);
        ChannelId id = ctx.channel().id();
        this.readMsg(id, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        System.out.println("服务端发生异常【" + cause.getMessage() + "】");
        ctx.close();
    }

    public void readMsg(ChannelId channelId, Object msg) {
        System.out.println("进入readmsg");
        System.out.println("............获取到了信息channelID" + channelId + "-->msg" + msg);
    }

    /**
     * @param msg       需要发送的消息内容
     * @param channelId 连接通道唯一id
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 服务端给客户端发送消息
     * @return: void
     */
    public void channelWrite(ChannelId channelId, Object msg) throws Exception {
        ChannelHandlerContext ctx = CLIENT_MAP.get(channelId);
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

}

