package com.github.nettybook;
import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * 
 * @author user
 * 털넷 서버를 스프링 애플리케이션으로 변경하는 첫 번째 작업은 스프링 컨텍스트 생성이다.
 * 스프링 컨택스트 생성은 xml을 사용하는 방법과 어노테이션을 사용한 방법이 있는데
 * 이 책에서는 어노테이션을 사용한 설정 방법을 살펴본다.
 * 먼저 어노테이션을 이용한 스프링 애플리케이션을 작성하려면 AnnotationConfigApplicationContext 클래스의 객체를 생성한다.
 * AnnotationConfigApplicationContext 클래스의 생성자에 입력되는 클래스는 스프링 애플리케이션읠 설정 정보를 포함한다.
 * 스프링 설정을 포함한 TelnetServerConfig 클래스를 작성하고 스프링 애플리케이션 설정을 기술하자.
 */

// Configuration 어노테이션은 지정된 클래스가 스프링의 설정 정보를 포함한 클래스임을 표시한다.
// TelnetServerConfig 클래스에는 Bean 설정 정보와, ComponetScan, PropertySource 정보가 포함되어 있다.
@Configuration
/**
 * ComponentScan 어노테이션은 스프링의 컨텍스트가 클래스를 동적으로 찾을수 있도록 한다는 의미며 입력되는 패키지명을
 * 포함한 하위 패키지를 대상으로 검색한다는 의미다.
 */
@ComponentScan("com.github.nettybook")
/**
 * PropertySource 어노테이션은 설정 정보를 가진 파일의 위치에서 파일을 읽어서 Environment 객체로 자동 저장한다.
 * 
 */
@PropertySource("classpath:telnet-server.properties")
public class TelnetServerConfig {
	@Value("${boss.thread.count}")
	private int bossCount;
	
	@Value("${worker.thread.count}")
	private int workerCount;
	
	@Value("${tcp.port}")
	private int tcpPort;
	
	public int getBossCount() {
		return bossCount;
	}
	
	public int getWorkerCount() {
		return workerCount;
	}
	
	public int getTcpPort() {
		return tcpPort;
	}
	/**
	 * 설정 파일에서 읽어들인 tcp.port 정보로부터 InetSocketAddress 객체를 생성하고 객체 이름을
	 * tcpSocketAddress로 지정한다. 이 설정은 스프링 컨텍스트에 tcpSocketAddress라는 이름으로 추가되며
	 * 다른 Bean에서 사용할 수 있다.
	 */
	@Bean(name = "tcpSocketAddress")
	public InetSocketAddress tcpPort() {
		return new InetSocketAddress(tcpPort);
	}
	/**
	 * PropertySource 어노테이션에서 사용할 Environment 객체를 생성하는 Bean을 생성한다
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
