package com.github.API.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.API.core.ApiRequestTemplate;

// 스프링의 Service 어노테이션은 스프링 컨텍스트가 UserInfo 클래스를 생성할 수 있도록 하며 문자열로 지정된 users는 
// 스프링 컨텍스트에서 클래스의 객체를 생성할 때 사용할 이름이다. 
// 즉 getBean 메서드 호출의 인자로 사용된다.
@Service("users")
// 스프링의 Scope 어노테이션은 스프링 컨텍스트가 객체를 생성할 때 싱글톤으로 생성할 것인지 아니면
// 객체를 요청할 때마다 새로 생성할 것인지를 설정한다. 여기에 설정된 prototype 값은 요청할 때마다 새로
// 생성한다는 의미며 이 어노테이션을 지정하지 않으면 싱글톤으로 생성된다.
@Scope("prototype")
public class UserInfo extends ApiRequestTemplate{
	@Autowired
	private SqlSession sqlSession;
	
	public UserInfo(Map<String, String> reqData) {
		super(reqData);
	}
	// 파라미터가 정상적으로 입력됬는지 확인
	@Override
	public void requestParamValidation() throws RequestParamException {
		if( StringUtils.isEmpty(this.reqData.get("email"))) {
			throw new RequestParamException("email이 없습니다.");
		}
	}
	
	@Override
	public void service() throws ServiceException {
		//users.xml에 정의된 userInfoByEmail 쿼리를 수행한다.
		// 쿼리에 입력되는 파라미터는 HTTP 요청에서 입력된 필드와 매칭된다.
		Map<String, Object> result = 
				sqlSession.selectOne("users.userInfoByEmail", this.reqData);
				
		if( result != null ) {
			String userNo = String.valueOf(result.get("USERNO"));
			
			this.apiResult.addProperty("resultCode", "200");
			this.apiResult.addProperty("message", "Success");
			this.apiResult.addProperty("userNo", userNo);
		}
		else {
			this.apiResult.addProperty("resultCode", "404");
			this.apiResult.addProperty("message", "Fail");
		}
	}
}
