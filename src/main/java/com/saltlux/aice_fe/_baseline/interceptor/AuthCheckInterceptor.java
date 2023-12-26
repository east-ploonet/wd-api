package com.saltlux.aice_fe._baseline.interceptor;

import com.saltlux.aice_fe._baseline.baseVo.AuthTokenVo;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.app.auth.dao.AppAuthDao;
import com.saltlux.aice_fe.app.auth.service.AppAuthService;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.pc.admin.service.AdminService;
import com.saltlux.aice_fe.pc.auth.service.PcAuthService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginAdminInfoVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Aspect
@Component
public class AuthCheckInterceptor implements HandlerInterceptor {

	//-- Global Fields --//
	//
	@Autowired
	private HttpSession session;
	//
	@Autowired
	private ThrowException throwException;
	//
	@Value("${auth.skip.list}")
	private List<String> authSkipList;

	@Value("${apiVersionPrefix}")
	private String workApiPrefix;
	//
	@Autowired
	AppAuthDao appAuthDao;

	@Autowired
	private PcAuthService pcAuthService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private AppAuthService appAuthService;
	//
	//-- Global Fields --//


	private boolean authSkip(final String requestURI) {

		if( StringUtils.isEmpty(requestURI) ) {
			return false;
		}

		// 최상위 경로는 통과
		if( "/".equals(requestURI) ) {
			return true;
		}

		String workApiPrefixSt= "workapi/v1";

		String requestAfterRemovePrefix = requestURI.replace(workApiPrefixSt, "").substring(1);

		for(String authSkipStr : authSkipList){
			if (authSkipStr.endsWith("*")) {
				if (requestAfterRemovePrefix.contains(authSkipStr.substring(0, authSkipStr.length() - 2))) { // remove /* from last
					return true;
				}
			}


			if( requestAfterRemovePrefix.equals(authSkipStr) ){
				return true;
			}
		}
		return false;
	}


	@Around("execution(* *..*controller.*.*(..))")
	public Object handleAccessToken(ProceedingJoinPoint joinPoint) throws Throwable {

		HttpServletRequest request  = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		if(log.isDebugEnabled()) {

			log.debug( "********** Request-IP      : {}", Common.getRealIp(request) );
			log.debug( "********** Request-Referer : {}", request.getHeader("Referer") );
			log.debug( "********** Request-URL     : {}", request.getRequestURL().toString() );
			log.debug( "********** Request-URI     : {}", request.getRequestURI() );
			log.debug( "********** Request-Method  : {}", request.getMethod());


		}

		// 인증이 필요없는 구간은 skip
		if( authSkip(request.getRequestURI()) ) {
			return joinPoint.proceed();
		}
		// 세션 체크 (세션 유무 + 복호화 여부)
		try{
//			AuthTokenVo decodeToken = AuthTokenVo.decodeToken( session.getAttribute("appAuthToken").toString() );

			AuthTokenVo decodeToken = null;
			//App 과 PC 인증을 구분함 ( PC는 "/workStage" 를 무조건 포함)
			log.info("=================== url = {}", request.getRequestURI());
			if( request.getRequestURI().contains("admin") ){
				String tokenFromSession = null;
				String tokenFromHeader = request.getHeader("x-access-token");
				if (null != session.getAttribute("pcAuthToken")) {
					tokenFromSession = session.getAttribute("pcAuthToken").toString();
				}
				if (StringUtils.isNotBlank(tokenFromHeader) && null == tokenFromSession) {
					tokenFromSession = tokenFromHeader;
				}
				if (!StringUtils.isNotBlank(tokenFromSession)) {
					throwException.statusCode(401);
				}
				decodeToken = AuthTokenVo.decodeToken( tokenFromSession );
				PcLoginAdminInfoVo pcLoginAdminInfoVo = adminService.getAdminUserLoginByPk(Long.parseLong(decodeToken.getMemberPk()));
				request.getSession().setAttribute("pcAdminInfo" , pcLoginAdminInfoVo);
			}else if( request.getRequestURI().contains("workStage") || request.getRequestURI().contains("workplace")){
				String tokenFromSession = null;
				String tokenFromHeader = request.getHeader("x-access-token");
				if (null != session.getAttribute("pcAuthToken")) {
					tokenFromSession = session.getAttribute("pcAuthToken").toString();
				}
				if (StringUtils.isNotBlank(tokenFromHeader) && null == tokenFromSession) {
					tokenFromSession = tokenFromHeader;
				}
				if (!StringUtils.isNotBlank(tokenFromSession)) {
					throwException.statusCode(401);
				}
				decodeToken = AuthTokenVo.decodeToken( tokenFromSession );
				PcLoginInfoVo pcLoginInfoVo = pcAuthService.getUserLoginById(Long.parseLong(decodeToken.getMemberPk()));
				request.getSession().setAttribute("pcLoginInfo"   , pcLoginInfoVo);
			}else{
				String tokenFromSession = null;
				if (null != session.getAttribute("appAuthToken")) {
					tokenFromSession = session.getAttribute("appAuthToken").toString();
				}
				if (null != session.getAttribute("pcAuthToken")) {
					tokenFromSession = session.getAttribute("pcAuthToken").toString();
				}
				String tokenFromHeader = request.getHeader("x-access-token");
				if (StringUtils.isNotBlank(tokenFromHeader) && null == tokenFromSession) {
					tokenFromSession = tokenFromHeader;
				}
				decodeToken = AuthTokenVo.decodeToken( tokenFromSession );

				//App 인증의 경우 토큰값 및 회원상태 유효성을 체크한다.
				CompanyStaffVo reqCompanyStaffVo = new CompanyStaffVo();
				reqCompanyStaffVo.setPk_company_staff(     Long.parseLong( decodeToken.getMemberPk().toString() ));

				CompanyStaffVo getCompanyStaffVo = appAuthDao.getCompanyStaffByPk(reqCompanyStaffVo);
				if( getCompanyStaffVo == null
						|| !"A1101".equals( getCompanyStaffVo.getFd_staff_status_code() )
						|| !decodeToken.getSignupCode().equals( getCompanyStaffVo.getFd_signup_keycode() )
				) {
					throwException.statusCode(401);
				}
				if ( null == request.getSession().getAttribute("appLoginInfo") ) {
					request.getSession().setAttribute("appLoginInfo"   , appAuthService.getUserLoginById(Long.parseLong(decodeToken.getMemberPk())));
				}
			}

			if( StringUtils.isEmpty(decodeToken.getToken()) ){

				throwException.statusCode(401);
			}
		}catch (Exception ex) {
			log.error("********** ", ex);

			session.invalidate();
			throwException.statusCode(401);
		}

		return joinPoint.proceed();
	}
}
