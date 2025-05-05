package com.example.auctionmarket.common.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class MdcFilter implements Filter {

	private static final String REQUEST_ID_KEY = "requestId";
	private static final String REQUEST_URI_KEY = "requestUri";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String requestId = UUID.randomUUID().toString().substring(0, 8);

		MDC.put(REQUEST_ID_KEY, requestId);
		MDC.put(REQUEST_URI_KEY, httpRequest.getRequestURI());

		try {
			// 서블릿으로 요청 전달
			chain.doFilter(request, response);
		} finally {
			// 요청 처리가 끝나면 반드시 MDC 에서 추가했던 값들을 제거
			MDC.remove(REQUEST_ID_KEY);
			MDC.remove(REQUEST_URI_KEY);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
