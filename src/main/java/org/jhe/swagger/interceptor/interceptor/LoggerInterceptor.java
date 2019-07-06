package org.jhe.swagger.interceptor.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoggerInterceptor implements HandlerInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerInterceptor.class);
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LOGGER.error("[preHandle][" + request + "]" + "[" + request.getMethod() + "]" + request.getRequestURI());
		try {
			MDC.put("catngUser", "myUser");
		} catch( Exception e) {
			LOGGER.error("error with MDC.put",e);
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception {
		LOGGER.debug("[postHandle][" + request + "]");
		try {
			MDC.remove("catngUser");
		} catch(Exception e) {
			LOGGER.error("error with MDC.remove",e);
		}
	}
}