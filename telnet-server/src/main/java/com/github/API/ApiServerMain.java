package com.github.API;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class ApiServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AbstractApplicationContext springContext = null;
		try {
			springContext = new AnnotationConfigApplicationContext(ApiServerConfig.class);
			
		}
		finally {
			springContext.close();
		}
	}

}
