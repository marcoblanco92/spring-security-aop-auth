package com.marbl.spring_security_aop_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class SpringSecurityAopAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAopAuthApplication.class, args);
	}

}
