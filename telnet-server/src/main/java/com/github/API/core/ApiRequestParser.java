package com.github.API.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;

public class ApiRequestParser extends SimpleChannelInboundHandler<FullHttpMessage> {
	/**
	 * 클라이언트가 전송한 HTTP 프로토콜 데이터는 채널 파이프라인에 등록된 HTTP 프로토콜 코덱들을 거치고 나면 FullHttpMessage
	 * 객체로 변환되어 마지막 데이터 핸들러인 ApiRequestParser에 인바운드 이벤트로 전달된다. FullHttpMessage는
	 * HttpMessage와 HttpContent 인터페이스를 모두 상속한다. 여기서 HttpMessage 클래스는 HTTP 요청과 응답을
	 * 표현하는 인터페이스이며 HTTP 프로토콜 버전, 요청 URL, HTTP 헤더 정보 등이 포함된다. HttpContent 클래스는 HTTP
	 * 요청 프로토콜에 포함된 본문 데이터가 포함된다. 즉, 클라이언트가 전송한 데이터가 3,4,6 디코더를 거치고 나면
	 * ApiRequestParser 클래스의 인바운드 이벤트에 FullHttpMessage 객체의 값을 확인하여 필요한 로직을 처리하면 된다.
	 * 클라이언트의 HTTP 요청 데이터를 처리하는 ApiRequestParser 클래스의 코드를 살펴보자 ApiRequestParser는 크게
	 * 맴버 변수와 인바운드 핸들러의 이벤트 메서드, 그리고 Http 본문 데이터를 읽는 부분으로 나뉜다.
	 */

	private static final Logger logger = LogManager.getLogger(ApiRequestParser.class);

	private HttpRequest request;

	private JsonObject apiResult;

	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // DISK

	private HttpPostRequestDecoder decoder;

	private Map<String, String> reqData = new HashMap<String, String>();

	private static final Set<String> usingHeader = new HashSet<String>();
	static {
		usingHeader.add("token");
		usingHeader.add("email");
	}

	// 클라이언트가 전송한 데이터가 채널 파이프라인의 모든 디코더를 거치고 난 뒤에 호출된다. 메서드 호출에 입력되는
	// 객체는 FullHttpMessage 인터페이스의 구현체이고 HTTP 프로토콜의 모든 데이터가 포함되어 있다.
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg)  {
		// Request header 처리
		// HttpRequestDecoder는 HTTP 프로토콜의 데이터를 HttpRequest, HttpContent,
		// LastHttpContent의 순서로 디코딩하여 FullHttpMessage 객체를 만들고 인바운드 이벤트를 발생 시킨다.
		// FullHttpMessage 인터페이스는 HttpRequest, HttpMessage, HttpContent의 최상위 인터페이스이므로
		// 이 코드의 instance of 연산자는 참을 돌려준다.
		System.out.println(msg);
		if (msg instanceof HttpRequest) {
			// FullHttpMessage 인터페이스를 HtppRequest 인터페이스로 캐스팅하여 맴버 변수에 저장한다.
			this.request = (HttpRequest) msg;

			if (HttpHeaders.is100ContinueExpected(request)) {
				send100Continue(ctx);
			}
			// HTTP 요청의 해더 정보를 추출한다.
			HttpHeaders headers = request.headers();

			if (!headers.isEmpty()) {
				for (Map.Entry<String, String> h : headers) {
					String key = h.getKey();
					// 추출한 헤더 정보 중에서 usingHeader에 지정된 값만 추출한다.
					if (usingHeader.contains(key)) {
						// usingHeader에 지정된 헤더 정보를 맵 객체에 저장한다.
						// 이 객체는 뒤에서 생성하는 api 업무 처리 인터페이스인 ApiRequest의 구현체를 호출할 때 인수로 전달된다.
						reqData.put(key, h.getValue());
					}
				}
			}
			// 클라이언트의 HTTP 요청 URI을 추출하여 맵에 저장한다.
			reqData.put("REQUEST_URI", request.getUri());
			// 클라이언트의 HTTP 요청 메서드를 추출하여 맵에 저장한다.
			reqData.put("REQUEST_METHOD", request.getMethod().name());
		}

		// Request content 처리.
		if (msg instanceof HttpContent) {

			// HttpContent의 상위 인터페이스인 LastHttpContent는 모든 HTTP 메서드가 디코딩되었고 HTTP 프로토콜의 마지막
			// 데이터임을 알리는 인터페이스다
			if (msg instanceof LastHttpContent) {
				logger.debug("LastHttpContent message received!!" + request.getUri());

				LastHttpContent trailer = (LastHttpContent) msg;
				// HTTP 본문에서 HTTP Post 데이터를 추출한다
				readPostData();
				// HTTP 프로토콜에서 필요한 데이터의 추출이 완료되면 reqData 맵을 ServiceDispatcher 클래스의
				// dispatch 메서드를 호출하여 HTTP 요청에 맞는 API 서비스 클래스를 생성한다.
				ApiRequest service = ServiceDispatcher.dispatch(reqData);

				try {
					// ServiceDispathcer 클래스의 dispatch 메서드로부터 생성된 API 서비스 클래스를 실행한다.
					service.executeService();

					// API 서비스 클래스의 수행 결과를 apiResult 맴버 변수에 할당한다.
					apiResult = service.getApiResult();
					System.out.println("apiResult : " + apiResult);
				} finally {
					reqData.clear();
				}
				// apiResult 맴버 변수에 저장된 API 처리 결과를 클라이언트 채널의 송신 버퍼에 기록한다.
				if (!writeResponse(trailer, ctx)) {
					ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
				}
				reset();
			}
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// channelRead0 이벤트 메서드의 수행이 완료된 이후에 channelReadComplete 메서드가 호출되고
		// 이때 채널 버퍼의 내용을 클라이언트로 전송한다.
		logger.info("요청 처리 완료");
		ctx.flush();
	}

	private void reset() {
		request = null;
	}

	private void readPostData() {
		// TODO Auto-generated method stub
		try {
			// HttpDataFactory 객체와 HttpRequest 객체를 인수로 하여 HttpPostRequestDecoder 객체를
			// 생성한다. 즉 HttpRequest 객체에 포함된 HTTP 본문 중에서 POST 메서드로 수신된 데이터를
			// 추출하기 위한 디코더를 생성한다.
			decoder = new HttpPostRequestDecoder(factory, request);
			for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
				if (HttpDataType.Attribute == data.getHttpDataType()) {
					try {

						// 디코더를 통해서 추출된 데이터 목록을 Attribute 객체로 캐스팅한다.
						Attribute attribute = (Attribute) data;
						// Attribute의 이름과 값을 reqData 맵에 저장한다. 즉 클라이언트가 HTML의 FORM 일레먼트를
						// 사용하여 전송한 데이터를 추출한다. ASP 또는 JSP의 request.queryString 메서드와 동일한
						// 동작을 수행한다.
						System.out.println("attribute.getName()" + attribute.getName() );
						System.out.println("attribute.getValue()" + attribute.getValue() );
						
						reqData.put(attribute.getName(), attribute.getValue());
					} catch (IOException e) {
						logger.error("BODY Attribute : " + data.getHttpDataType().name(), e);
						return;
					}
				} else {
					logger.info("BODY data : " + data.getHttpDataType().name() + ": " + data);
				}
			}
		} catch (ErrorDataDecoderException e) {
			logger.error(e);
		} finally {
			if (decoder != null) {
				decoder.destroy();
			}
		}
	}

	private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
		// Decide whether to close the connection or not.
		boolean keepAlive = HttpHeaders.isKeepAlive(request);
		// build the response object.
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				currentObj.getDecoderResult().isSuccess() ? OK : BAD_REQUEST,
				Unpooled.copiedBuffer(apiResult.toString(), CharsetUtil.UTF_8));

		response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");

		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

			// Add keep alive header as per :
			// -
			// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		// Write the response.
		System.out.println("response : " + response);
		ctx.write(response);
		return keepAlive;
	}

	private static void send100Continue(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
		ctx.write(response);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause);
		ctx.close();
	};
}
