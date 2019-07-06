package org.jhe.swagger.interceptor.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class Config {
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	@Bean
	public ServletContextInitializer servletContextInitializer() {
		return servletContext -> {
			servletContext.getSessionCookieConfig().setName("SWAGGER-INTERCEPTOR-SID");
			servletContext.getSessionCookieConfig().setPath("/");
			servletContext.getSessionCookieConfig().setDomain("");
		};
	}

}
