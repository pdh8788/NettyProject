package com.github.API;

import com.github.API.core.ApiRequestParser;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

public class ApiServerInitializer extends ChannelInitializer<SocketChannel>{
	private final SslContext sslCtx;
	
	public ApiServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// 클라이언트 채널로 수신된 HTTP 데이터를 처리하기 위한 채널 파이프라인 객체를 생성한다.
		ChannelPipeline p = ch.pipeline();
		if ( sslCtx != null ) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		// HttpRequestDecoder는 HTTP 요청을 처리하는 디코더다. 즉 클라이언트가 전송한 HTTP 프로토콜을 네티의 바이트
		// 버퍼로 변환하는 작업을 수행한다.
		p.addLast(new HttpRequestDecoder());
		/**
		 * HttpObjectAggregator는 HTTP 프로토콜에서 발생하는 메시지 파편화를 처리하는 디코더다.
		 * HTTP 프로토콜을 구성하는 데이터가 나뉘어서 수신되었을 때 데이터를 하나로 합쳐주는 역할을 수행한다.
		 * 인자로 입력된 65536은 한꺼번에 처리 가능한 최대 데이터 크기다. 65Kbyte 이상의 데이터가 하나의
		 * HTTP 요청으로 수신되면 TooLongFrameException 예외가 발생한다
		 */
		p.addLast(new HttpObjectAggregator(65536));
		// HttpResponseEncoder는 수신된 Http 요청의 처리 결과를 클라이언트로 전송할 때 Http 프로토콜로 변환해주는 인코더다.
		p.addLast(new HttpResponseEncoder());
		// HttpContentCompressorsms Http 프로토콜로 송수신되는 HTTP의 본문 데이터를 gzip 압축 알고리즘을 사용하여 
		// 압축과 압축 해제를 수행한다. HttpContentCompressor는 ChannelDuplexHandler 클래스를 상속받기 때문에
		// 인바운드와 아웃바운드에서 모두 호출된다.
		p.addLast(new HttpContentCompressor());
		// ApiRequestParser는 클라이언트로부터 수신된 HTTP 데이터에서 헤더와 데이터 값을 추출하여 토큰 발급과
		// 같은 업무 처리 클래스로 분기하는 클래스로써 API 서버의 컨트롤러 역할을 수행한다.
		p.addLast(new ApiRequestParser());
		
		// 클라이언트로부터 데이터를 수신했을 때 데이터 핸들러는 3, 4, 6, 7 순서로 호출되며,
		// ApiRequestParser의 처리가 완료됭 채널로 데이터를 기록할 때 호출되는 데이터 핸들러는 7, 6, 5 순서로 호출된다.
		
	}

}
