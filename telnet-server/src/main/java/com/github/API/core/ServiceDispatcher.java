package com.github.API.core;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * ServiceDispatcher 클래스는 ApiRequestParser 클래스에서 추출한 HTTP 요청 데이터를
 * 기준으로 그에 해당하는 API 서비스 클래스를 생성하여 돌려준다.
 * @author user
 *
 */
@Component
public class ServiceDispatcher {
	// 정적 변수에 스프링 컨텍스트를 할당한다.
	private static ApplicationContext springContext;
	
	// 스프링 컨텍스트는 정적 변수에 직접 할당 할 수 없기 때문에 메서드에 Autowired 어노테이션을 할당하여 간접적으로 할당한다.
	@Autowired
	public void init(ApplicationContext springContext) {
		ServiceDispatcher.springContext = springContext;
	}
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	// HTTP 요청에서 추출한 값을 가진 맵 객체를 인수로 하여 dispatch 메서드를 선언했다.
	public static ApiRequest dispatch(Map<String, String> requestMap) {
		// HTTP 요청의 URI 값을 확인한다. 
		String serviceUri = requestMap.get("REQUEST_URI"); 
		String beanName = null;
		
		// URI 값이 없으면 beanName에 기본 값으로 notFound를 설정한다.
		if( serviceUri == null ) {
			beanName = "notFound";
		}
		
		// HTTP 요청 URI가 /token으로 시작하면 beanName에 토큰을 처리하는 API 서비스 클래스 중에서 하나를 선택한다.
		if(serviceUri.startsWith("/tokens")) {
			String httpMethod = requestMap.get("REQUEST_METHOD");
			
			switch(httpMethod) {
				case "POST" :
					beanName = "tokenIssue";
					break;
				
				case "DELETE" :
					beanName = "tokenExpier";
					break;
				
				case "GET" :
					beanName = "tokenVerify";
					break;
				
				default :
					beanName = "notFound";
					break;
			}
			
			
		}
		// HTTP 요청 URI가 /users 로 시작하면 beanName에 사용자번호 조회 API를 할당한다.
		else if ( serviceUri.startsWith("/users") ) {
			beanName = "users";
		}
		else {
			beanName = "notFound";
		}
		
		ApiRequest service = null;
		try {
			// beanName 값을 사용하여 스프링 컨텍스트에서 API 서비스 클래스 객체를 생성한다.
			service = (ApiRequest) springContext.getBean(beanName, requestMap);
		}
		catch (Exception e ) {
			e.printStackTrace();
			service = (ApiRequest) springContext.getBean("notFound", requestMap);
		}
		
		return service;
	}
}
