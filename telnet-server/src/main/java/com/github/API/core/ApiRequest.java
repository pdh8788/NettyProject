package com.github.API.core;

import com.github.API.service.RequestParamException;
import com.github.API.service.ServiceException;
import com.google.gson.JsonObject;

public interface ApiRequest {
	// request 파라미터 검증
	public void requestParamValidation() throws RequestParamException;
	
	// 서비스 구현
	public void service() throws ServiceException;
	
	// API 서비스 호출시 실행
	public void executeService();
	
	// API 서비스 수행 결과 조회
	public JsonObject getApiResult();
}
