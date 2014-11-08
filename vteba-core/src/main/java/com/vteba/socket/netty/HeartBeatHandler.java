package com.vteba.socket.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 心跳处理器。客户端和服务端都需要配置一个心跳处理器。
 * <p>配合io.netty.handler.timeout.IdleStateHandler，IdleStateHandler一般配置在客户端。
 * 让客户端主动发起ping检测，Server端响应pong。IdleStateHandler配置在Server端亦可，但是会增加Server端负载。
 * @author yinlei
 * @date 2014-11-8
 */
public class HeartBeatHandler extends ChannelDuplexHandler {
	private static final String PING = "ping";
	private static final String PONG = "pong";
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		String heart = (String) msg;
		if (heart.equals(PING)) {
//			System.out.println("接受到心跳ping消息");
			ctx.write(PONG);
//			ctx.flush();//多余
		} else if (heart.equals(PONG)) {
			// pong消息不处理，等待Netty核心的事件通知，空闲时才发送ping消息，减少负载
//			System.out.println("接受到心跳pong消息");
//			TimeUnit.SECONDS.sleep(6);
//			ctx.write("ping");
//			ctx.flush();
		} else {
			super.channelRead(ctx, msg);// 不是心跳信息，向下传递消息，给下面的handler处理
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
//				System.out.println("读超时");
				ctx.close();// 读超时关闭
//			} else if (event.state() == IdleState.WRITER_IDLE) {
//				System.out.println("写超时");
//				// 写超时，不做处理
			} else if (event.state() == IdleState.ALL_IDLE) {
				// 读写都超时
//				System.out.println("读写超时");
				ctx.write("ping");
				ctx.flush();
			}
		}
		super.userEventTriggered(ctx, evt);// 将事件向下传递
	}

}
