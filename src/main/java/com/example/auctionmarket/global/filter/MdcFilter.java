package com.example.auctionmarket.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

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

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		// doFilter 는 ServletRequest 를 받음 => 하지만, 웹 애플리케이션은 HTTP 프로토콜을 사용
		// => HttpServletRequest 타입으로 취급해서 그 안에 있는 메서드를 사용하도록 형변환
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		// 고유한 요청 ID 생성 (UUID 사용)
		String requestId = UUID.randomUUID().toString().substring(0, 8); // 8자리로 축약

		// MDC 에 요청 ID와 URI 추가
		// MDC 에 저장된 값들은 현재 요청을 처리하고 있는 스레드에 연결됨
		// => 스레드에서 발생하는 모든 로그 메시지는 Logback 설정에 따라 자동으로 이 requestId와 requestUri 값을 포함
		MDC.put(REQUEST_ID_KEY, requestId);
		MDC.put(REQUEST_URI_KEY, httpRequest.getRequestURI());

		try {
			// 서블릿으로 요청 전달
			chain.doFilter(request, response);
		} finally {
			// 요청 처리가 끝나면 반드시 MDC 에서 추가했던 값들을 제거
			// => 그렇지 않으면 다른 요청 처리 시 잘못된 값이 로그에 남을 수 있음 (Thread Pool 환경)
			MDC.remove(REQUEST_ID_KEY);
			MDC.remove(REQUEST_URI_KEY);
			// MDC.clear(); // 또는 MDC.clear(); 사용하여 모든 값을 제거 가능
		}
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