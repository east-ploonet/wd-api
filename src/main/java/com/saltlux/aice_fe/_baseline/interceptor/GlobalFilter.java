package com.saltlux.aice_fe._baseline.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class GlobalFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpServletRequest   = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

//		if( httpServletRequest.getServletPath().matches("/api/v(.*)/(.*)|/static/(.*)|/storage/(.*)|/(.*)\\.(.*)") ){
		if( httpServletRequest.getServletPath().matches("/workapi/v(.*)/(.*)|/(.*)\\.(.*)") ){

			chain.doFilter(httpServletRequest, httpServletResponse);

		}else{
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/");
			requestDispatcher.forward(request, response);
		}
	}
}