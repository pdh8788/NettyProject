package com.github.API;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpMessage;

public class ApiRequestParser extends SimpleChannelInboundHandler<FullHttpMessage>{
	/**
	 * 클라이언트가 전송한 HTTP 프로토콜 데이터는 채널 파이프라인에 등록된 HTTP 프로토콜 코덱들을 거치고 나면
	 * FullHttpMessage 객체로 변환되어 마지막 데이터 핸들러인 ApiRequestParser에 인바운드 이벤트로 전달된다.
	 * FullHttpMessage는 HttpMessage와 HttpContent 인터페이스를 모두 상속한다. 여기서 HttpMessage 클래스는
	 * HTTP 요청과 응답을 표현하는 인터페이스이며 HTTP 프로토콜 버전, 요청 URL, HTTP 헤더 정보 등이 포함된다.
	 * HttpContent 클래스는 HTTP 요청 프로토콜에 포함된 본문 데이터가 포함된다. 
	 * 즉, 클라이언트가 전송한 데이터가 3,4,6 디코더를 거치고 나면 ApiRequestParser 클래스의 인바운드 이벤트에 FullHttpMessage
	 * 객체의 값을 확인하여 필요한 로직을 처리하면 된다. 클라이언트의 HTTP 요청 데이터를 처리하는 ApiRequestParser 클래스의 코드를 살펴보자
	 * ApiRequestParser는 크게 맴버 변수와 인바운드 핸들러의 이벤트 메서드, 그리고 Http 본문 데이터를 읽는 부분으로 나뉜다.
	 */
//	private static final Logger logger = LogManager.getLogger(ApiRequestParser.class);
}
