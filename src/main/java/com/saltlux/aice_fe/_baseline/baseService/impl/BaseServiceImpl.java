package com.saltlux.aice_fe._baseline.baseService.impl;

import com.google.gson.Gson;
import com.saltlux.aice_fe._baseline.baseService.BaseService;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
@Transactional
public class BaseServiceImpl implements BaseService {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	protected HttpSession session;

	@Autowired
	protected ThrowException throwException;

	@Value("${auth.encrypt.key}")
	protected String authEncryptKey;

	@Value("${auth.encrypt.iv}")
	protected String authEncryptIv;


	protected final String PRODUCES_JSON                = "application/json; charset=UTF-8";

	//단방향 암호화(bcrypt)
	protected final PasswordEncoder BCRYPT_ENCODER    = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	@Value("${path.browser.storage}")
	protected String pathBrowserStorage;

	protected String getClientIp() {

		// proxy 고려한 client ip
		String clientIp = request.getHeader("X-Forwarded-For");

		if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getRemoteAddr();
		}

		String[] ip_arr = clientIp.split(", ");
		if(ip_arr.length > 1){
			clientIp = ip_arr[ip_arr.length-1];
		}
		return clientIp;
	}

	public PcLoginInfoVo getPcLoginInfoVo(){

		PcLoginInfoVo pcLoginInfoVo;

		if( request.getSession().getAttribute("pcLoginInfo") != null ){
			return ( (PcLoginInfoVo) request.getSession().getAttribute("pcLoginInfo") );
		} else {
			return null;
		}
//		컨트롤러에서 다음과 같이 사용 : PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
	}

	public AppLoginInfoVo getAppLoginInfoVo(){

		AppLoginInfoVo appLoginInfoVo;

		if( request.getSession().getAttribute("appLoginInfo") != null ){
			appLoginInfoVo = ( (AppLoginInfoVo) request.getSession().getAttribute("appLoginInfo") );
		} else {
			appLoginInfoVo = null;
		}
		return appLoginInfoVo;
//		컨트롤러에서 다음과 같이 사용 : AppLoginInfoVo appLoginInfoVo = getAppLoginInfoVo();
	}

}
