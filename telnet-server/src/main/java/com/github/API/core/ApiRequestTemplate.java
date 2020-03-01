package com.github.API.core;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.API.service.RequestParamException;
import com.github.API.service.ServiceException;
import com.google.gson.JsonObject;

public abstract class ApiRequestTemplate implements ApiRequest{
	protected Logger logger;
	
	/**
	 * API 요청 data
	 */
	protected Map<String, String> reqData;
	
	/**
	 * API 처리 결과
	 */
	protected JsonObject apiResult;
	
	// HTTP 요청에서 추출한 필드의 이름과 값을 API 클래스의 생성자로 전달한다.
	public ApiRequestTemplate(Map<String, String> reqData) {
		// TODO Auto-generated constructor stub
		this.logger = LogManager.getLogger(this.getClass());
		this.apiResult = new JsonObject();
		this.reqData = reqData;
		
		System.out.println("request data : " + this.reqData);
	}
	
	public void executeService() {
		try {
			// requestParamValidation 메서드는 API 서비스 클래스의 인수로 입력된 HTTP 요청 맵의 정합성을
			// 검증한다. 이 메서드는 ApiRequestTemplate 추상 클래스를 상속받은 클래스에서 구현해야 한다.
			this.requestParamValidation();
			
			this.service();
		} catch ( RequestParamException e ) {
			logger.error(e);
			this.apiResult.addProperty("resultCode", "405");
		} catch ( ServiceException e ) {
			logger.error(e);
			this.apiResult.addProperty("resultCode", "501");
		}
	}
	
	@Override
	public JsonObject getApiResult() {
		// executeService 메서드가 호출된 이후에 service 메서드에서 할당된 API 처리 결과를 돌려준다.
		return this.apiResult;
	}
	
	@Override
	public void requestParamValidation() throws RequestParamException {
		// TODO Auto-generated method stub
		if( getClass().getClasses().length == 0) {
			return;
		}
	}
	
	public final <T extends Enum<T>> T fromValue(Class<T> paramClass, String paramValue) {
		if ( paramValue == null  || paramClass == null ) {
			throw new IllegalArgumentException("There is no value with name " + paramValue + " in Enum"
					+ paramClass.getClass().getName());
		}
		
		for ( T param : paramClass.getEnumConstants()) {
			if(paramValue.equals(param.toString())) {
				return param;
			}
		}
		
		throw new IllegalArgumentException("There is no value with name " + paramValue + " in Enum"
				+ paramClass.getClass().getName());
	}
}
