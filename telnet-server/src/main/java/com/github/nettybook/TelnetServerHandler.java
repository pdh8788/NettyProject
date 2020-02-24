package com.github.nettybook;
import java.net.InetAddress;
import java.util.Date;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class TelnetServerHandler extends SimpleChannelInboundHandler<String>{
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.write("환영합니다. "
				+ InetAddress.getLocalHost().getHostName() + "에 접속하셨습니다!\r\n");
		ctx.write("현재 시간은 " + new Date() + " 입니다");
		ctx.flush();
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
		// TODO Auto-generated method stub
		String response;
		boolean close = false;
		System.out.println(request);
		if ( request.isEmpty()) {
			response = " 명령을 입력해 주세요.\r\n";
		}
		else if ( "bye".equals(request.toLowerCase())) {
			response = " 좋은 하루 되세요!\r\n";
			close = true;
		}
		else {
			response = " 입력하신 명령이" + request + " 입니까?\r\n"; 
		}
		
		ChannelFuture future = ctx.write(response);
		
		if ( close ) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	};
}
