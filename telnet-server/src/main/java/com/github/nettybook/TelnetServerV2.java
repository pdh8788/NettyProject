package com.github.nettybook;
import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 클래스에 Componet 어노테이션을 지정하여 TelnetServerV2 클래스의 객체가 스프링 컨텍스트에 
 * 등록되게 한다
 */
@Component
public class TelnetServerV2 {
	/**
	 * port 필드값이 자동 할당
	 * 스프링 컨텍스트에 지정된 객체 이름 중에 tcpSocketAddress에 해당하는 객체를 할당하도록 지정
	 */
	@Autowired
	@Qualifier("tcpSocketAddress")
	private InetSocketAddress port;
	
	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new TelnetServerInitializer());
			
			ChannelFuture future = b.bind(port).sync();
			
			future.channel().closeFuture().sync();
		}
		catch (InterruptedException e ) { 
			e.printStackTrace();
		}
		finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	
}
