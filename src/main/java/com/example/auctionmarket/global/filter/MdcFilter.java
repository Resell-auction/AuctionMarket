package com.example.auctionmarket.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;


// 웹 요청마다 고유한 ID를 생성하여 MDC(Mapped Diagnostic Context)에 추가하는 필터
// MDC 에 추가된 값은 Logback 설정에 따라 로그 메시지에 포함

@Component
public class MdcFilter implements Filter {

	// MDC 에 저장될 요청 ID의 키 이름
	private static final String REQUEST_ID_KEY = "requestId";
	// MDC 에 저장될 요청 URI의 키 이름
	private static final String REQUEST_URI_KEY = "requestUri";

	private static final String CLIENT_IP_KEY = "client_ip";


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestId = UUID.randomUUID().toString().substring(0, 8);

		String clientIp = getClientIp(httpRequest);

		// MDC에 요청 ID, URI, Client IP 추가
		MDC.put(REQUEST_ID_KEY, requestId);
		MDC.put(REQUEST_URI_KEY, httpRequest.getRequestURI());

		// *** 추가된 부분 1: clientIp 값을 MDC에 넣기 ***
		// IP 주소가 비어있지 않은 경우에만 추가
		if (StringUtils.hasText(clientIp)) {
			MDC.put(CLIENT_IP_KEY, clientIp);
		}

		try {
			chain.doFilter(request, response);
		} finally {
			// 요청 처리 후 MDC 값 제거
			MDC.remove(REQUEST_ID_KEY);
			MDC.remove(REQUEST_URI_KEY);
			// *** 추가된 부분 2: 추가했던 Client IP 키도 제거 ***
			MDC.remove(CLIENT_IP_KEY);
		}
	}

	// @Override
	// public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	// 	throws IOException, ServletException {
	//
	// 	// doFilter 는 ServletRequest 를 받음 => 하지만, 웹 애플리케이션은 HTTP 프로토콜을 사용
	// 	// => HttpServletRequest 타입으로 취급해서 그 안에 있는 메서드를 사용하도록 형변환
	// 	HttpServletRequest httpRequest = (HttpServletRequest) request;
	//
	// 	// 고유한 요청 ID 생성 (UUID 사용)
	// 	String requestId = UUID.randomUUID().toString().substring(0, 8); // 8자리로 축약
	//
	//
	// 	String clientIp = getClientIp(httpRequest);
	//
	//
	// 	// MDC 에 요청 ID와 URI 추가
	// 	// MDC 에 저장된 값들은 현재 요청을 처리하고 있는 스레드에 연결됨
	// 	// => 스레드에서 발생하는 모든 로그 메시지는 Logback 설정에 따라 자동으로 이 requestId와 requestUri 값을 포함
	// 	MDC.put(REQUEST_ID_KEY, requestId);
	// 	MDC.put(REQUEST_URI_KEY, httpRequest.getRequestURI());
	//
	// 	try {
	// 		// 서블릿으로 요청 전달
	// 		chain.doFilter(request, response);
	// 	} finally {
	// 		// 요청 처리가 끝나면 반드시 MDC 에서 추가했던 값들을 제거
	// 		// => 그렇지 않으면 다른 요청 처리 시 잘못된 값이 로그에 남을 수 있음 (Thread Pool 환경)
	// 		MDC.remove(REQUEST_ID_KEY);
	// 		MDC.remove(REQUEST_URI_KEY);
	// 		// MDC.clear(); // 또는 MDC.clear(); 사용하여 모든 값을 제거 가능
	// 	}
	// }

	private String getClientIp(HttpServletRequest request) {
		// 프록시 서버(Nginx 등)를 사용하는 경우 X-Forwarded-For 헤더 확인
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP"); // 웹로직
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			// 프록시 헤더에도 없으면 직접 접속 IP 사용
			ip = request.getRemoteAddr();
		}

		// 여러 IP가 전달된 경우 첫 번째 IP만 사용 (X-Forwarded-For)
		if (ip != null && ip.contains(",")) {
			ip = ip.split(",")[0];
		}

		return ip;
	}

	// init() 및 destroy() 메서드는 필요에 따라 구현 가능
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// init: 필터가 웹 서버에 의해 처음 생성되고 초기화될 때 딱 한 번 호출되는 메서드
		// => 필터 설정 정보를 읽어오거나 필요한 자원을 미리 준비하는 로직을 넣을 수 있음
	}

	@Override
	public void destroy() {
		// destroy: 필터가 서비스에서 제거될 때 (보통 웹 애플리케이션이 종료될 때) 딱 한 번 호출되는 메서드
		// => 필터가 사용하던 자원을 해제하는 로직을 넣을 수 있음
	}


}
