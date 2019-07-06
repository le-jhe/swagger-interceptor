package org.jhe.swagger.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableSwagger2
@EnableCaching
public class SwaggerInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerInterceptor.class);

	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[1];
			String arg = "--spring.config.location=src/test/resources/,src/test/resources/security.properties";
			LOGGER.info("no arguments... will add the following args[0]={}", arg);
			args[0] = arg;
		}

		SpringApplication.run(SwaggerInterceptor.class, args);
	}

}
