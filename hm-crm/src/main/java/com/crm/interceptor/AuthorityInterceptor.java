package com.crm.interceptor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.crm.common.util.CookieCompoment;
import com.crm.model.Staff;

public class AuthorityInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("mappingJackson2HttpMessageConverter")
	private HttpMessageConverter<Object> httpMessageConverter;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		Staff staff = CookieCompoment.getLoginUser(request);
		if (null == staff) {
			String url = request.getContextPath();
			response.sendRedirect(url);
			return false;
		}

		request.getSession().setAttribute("staff", staff);

		// int role = staff.getRole();
		// String uri = request.getRequestURI();

		return super.preHandle(request, response, handler);
	}

	@SuppressWarnings("unused")
	private void handleResponse(Object returnValue, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpInputMessage inputMessage = new ServletServerHttpRequest(request);
		List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
		if (acceptedMediaTypes.isEmpty()) {
			acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
		}
		MediaType.sortByQualityValue(acceptedMediaTypes);

		HttpOutputMessage outputMessage = new ServletServerHttpResponse(response);
		Class<?> returnValueType = returnValue.getClass();
		for (MediaType acceptedMediaType : acceptedMediaTypes) {
			if (httpMessageConverter.canWrite(returnValueType, acceptedMediaType)) {
				httpMessageConverter.write(returnValue, acceptedMediaType, outputMessage);
				break;
			}
		}
		if (logger.isWarnEnabled()) {
			logger.warn("Could not find HttpMessageConverter that supports return type [" + returnValueType + "] and "
					+ acceptedMediaTypes);
		}
	}
}
