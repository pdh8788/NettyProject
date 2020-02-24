package com.github.API;

public class note {
	/**
	 *  RESTFul 설계 관점에서 URL에 서비스의 행동인 동사를 포함하는  것은 잘못된 접근 방법이다
	 *  HTTP 1.1이 등장하면서 리소스에 대한 행동을 정의하는 GET, POST 이외에 PUT, DELETE, PATCH 메서드가 추가 되었고 이와 더불어
	 *  RESTful 설계가 생겨났다. RESTFul 설계의 근간은 하나의 리소스에는 하나의 URL을 설정하는 것이다.
	 *  
	 *  HTTP 메서드와 행동은 아래와 같은 관습을 가진다.
	 *  GET : 리소스 조회
	 *  POST : 리소스 생성
	 *  PUT : 리소스 생성 또는 수정
	 *  DELETE : 리소스 삭제
	 *  
	 *  API URL 		  HTTP 메서드			기능
	 *  /users			  GET				사용자 정보 조회
	 *  /tokens/<사용자번호> POST				사용자 인증 토큰 발급
	 *  				  DELETE			사용자 인증 토큰 만료
	 *  				  GET				사용자 인증 토큰 검증
	 *  
	 *  API URL 		입출력			필드이름				설명
	 *  /users			입력				email				사용자의 이메일을 HTTP header에 입력한다.
	 *  				출력				resultCode			API 처리 결과 코드를 돌려준다. API 처리 결과가 정상이면 결과 코드는 200이다
	 *  				출력				message				API 처리 결과 메시지를 돌려준다. API의 처리 결과가 정상일 때는 Success 메시지를 돌려주며 나머지 정상이 아닐때는 오류 메시지를 돌려준다.
	 *  				출력				userNo				입력된 이메일에 해당하는 사용자의 사용자번호를 돌려준다.
	 *  
	 *  {
	 *  	"resultCode" : 200,
	 *  	"message"	 : "Success",
	 *  	"userNo"	 : 12
	 *  }
	 *  
	 *  인증 토큰 발급 API
	 *  
	 */
}
