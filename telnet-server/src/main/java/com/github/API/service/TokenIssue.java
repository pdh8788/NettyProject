package com.github.API.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.API.core.ApiRequestTemplate;
import com.github.API.core.JedisHelper;
import com.github.API.core.KeyMaker;
import com.github.API.service.dao.TokenKey;
import com.google.gson.JsonObject;

import redis.clients.jedis.Jedis;
// 문자열로 지정된 tokenIssue는 스프링 컨텍스트에서 클래스의 객체를 생성할 때 사용할 이름이다.
// 즉 getBean 메서드 호출의 인자로 사용된다.
@Service("tokenIssue")
@Scope("prototype")
public class TokenIssue extends ApiRequestTemplate{
	// 레디스에 접근하기 위한 제디스 핼퍼 클래스다.
	private static final JedisHelper helper = JedisHelper.getInstance();
	
	@Autowired
	SqlSession sqlSession;
	
	public TokenIssue(Map<String, String> reqData) {
		super(reqData);
	}
	// 인증 토큰 발급 API의 입력 파라미터를 검증한다.
	@Override
	public void requestParamValidation() throws RequestParamException {
		if( StringUtils.isEmpty(this.reqData.get("userNo"))) {
			throw new RequestParamException("userNo이 없습니다.");
		}
		if (StringUtils.isEmpty(this.reqData.get("password"))){
			throw new RequestParamException("password가 없습니다.");
		}
	}
	
	@Override
	public void service() throws ServiceException {
		// TODO Auto-generated method stub
		Jedis jedis = null;
		
		try {
			Map<String, Object> result = sqlSession.selectOne("users.userInfoByPassword", this.reqData);
			
			if( result != null ) {
				final int threeHour = 60 * 60 * 3;
				long issueDate = System.currentTimeMillis() / 1000;
				String email = String.valueOf(result.get("USERID"));
				
				JsonObject token = new JsonObject();
				token.addProperty("issueDate", issueDate);
				token.addProperty("expireDate", issueDate + threeHour);
				token.addProperty("email", email);
				token.addProperty("userNo", reqData.get("userNo"));

				// token 저장
				// 발급된 토큰을 레디스에 저장하고 조회하고자 KeyMaker 인터페이스를 사용했다.
				KeyMaker tokenKey = new TokenKey(email, issueDate);
				jedis = helper.getConnection();
				// 제디스를 사용하여 레디스의 settex 명령을 수행한다. settex 명령은 지정된 시간 이후에 데이터를 자동으로 삭제하는 명령어다.
				jedis.setex(tokenKey.getKey(), threeHour, token.toString());
				
				// helper
				this.apiResult.addProperty("resultCode", "200");
				this.apiResult.addProperty("message", "Success");
				this.apiResult.addProperty("token", tokenKey.getKey());
				
			} else {
				// 토큰 없음
				this.apiResult.addProperty("resultCode", "404");
			}
			
			helper.returnResource(jedis);
			
		} catch (Exception e) {
			// TODO: handle exception
			helper.returnResource(jedis);
		}
		
	}
	
	
}
