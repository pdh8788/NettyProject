package com.github.nettybook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TelnetServer {
	private static final int PORT = 8023;
	
	public static void main(String[] args) throws Exception {
		EventLoopGroup boosGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try { 
			ServerBootstrap b = new ServerBootstrap();
			b.group(boosGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new TelnetServerInitializer());
			
			ChannelFuture future = b.bind(PORT).sync();
			future.channel().closeFuture().sync();
		}
		finally {
			boosGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			
		}
	}
}