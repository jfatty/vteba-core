package com.vteba.socket.netty;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class ReconnectHandler extends ChannelDuplexHandler {
	private final Bootstrap bootstrap;
	private ScheduledExecutorService scheduler;
	
	public ReconnectHandler(Bootstrap bootstrap) {
		super();
		this.bootstrap = bootstrap;
	}
	
	public ReconnectHandler(Bootstrap bootstrap, ScheduledExecutorService scheduler) {
		this.bootstrap = bootstrap;
		this.scheduler = scheduler;
	}
	
	public ScheduledExecutorService getScheduler() {
		if (scheduler == null) {
			scheduler = Executors.newScheduledThreadPool(1);
		}
		return scheduler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		getScheduler().scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				try {
					bootstrap.connect("127.0.0.1", 8080).sync();
				} catch (InterruptedException e) {
					
				}
			}
		}, 1, 15, TimeUnit.SECONDS);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}

}
