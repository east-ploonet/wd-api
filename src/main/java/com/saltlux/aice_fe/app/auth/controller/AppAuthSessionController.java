package com.saltlux.aice_fe.app.auth.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.app.auth.service.AppAuthService;
import com.saltlux.aice_fe.member.dao.MemberDao;
import com.saltlux.aice_fe.member.service.MemberService;
import com.saltlux.aice_fe.member.vo.MemberVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("${apiVersionPrefix}/auth")
@RestController
public class AppAuthSessionController extends BaseController {

    @Autowired
    AppAuthService appAuthService;

    @Autowired
    ThrowException throwException;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberDao memberDao;

//    @Value("${auth.token.expired}")
//    public int tokenExpired;


    //로그인 처리 (세션용)
    @PostMapping("/login")
    public Object loginSession(
		    @RequestBody Map<String, Object> bodyMap
    ) throws Exception {

        //필수값 체크
        throwException.requestBodyRequied( bodyMap, "signupCode", "staffName", "staffMobile");

	    //관리자 정보 조회
	    CompanyStaffVo companyStaffVo   = new CompanyStaffVo();
	    companyStaffVo.setFd_signup_keycode(    bodyMap.get("signupCode").toString() );
	    companyStaffVo.setFd_staff_name(        bodyMap.get("staffName").toString() );
	    companyStaffVo.setFd_staff_mobile(      bodyMap.get("staffMobile").toString() );
	    companyStaffVo.setFd_push_token(        bodyMap.get("pushToken") != null ? bodyMap.get("pushToken").toString() : "" );
	    companyStaffVo.setFd_app_version(       bodyMap.get("appVersion") != null ? bodyMap.get("appVersion").toString() : "" );

		if(bodyMap.get("appPlatform") == null || "".equals(bodyMap.get("appPlatform").toString()) || "Browser".equals(bodyMap.get("appPlatform").toString())){
			companyStaffVo.setFd_os_code( "A1803" );

		}else if( "Android".equals(bodyMap.get("appPlatform").toString()) ) {
			companyStaffVo.setFd_os_code( "A1801" );

		}else if( "iOS".equals(bodyMap.get("appPlatform").toString()) ) {
			companyStaffVo.setFd_os_code( "A1802" );
		}

	    //테스트 계정
	    if( "test".equals( bodyMap.get("staffName").toString() )){
		    companyStaffVo.setPk_company_staff( 11 );
	    }

	    ResponseVo responseVo = appAuthService.login( companyStaffVo);

	    //로그인 실패
	    if( 200 != responseVo.getStatus() ){
		    return responseVo;
	    }

	    //세션 생성
//	    request.getSession().invalidate(); //모든 세션 제거
	    request.getSession().removeAttribute("appAuthToken");
	    request.getSession().removeAttribute("appLoginInfo");

		Object appLoginInfo = responseVo.getBody().get("loginInfo");

	    request.getSession().setAttribute("appAuthToken"   , responseVo.getBody().get("authToken").toString());
	    request.getSession().setAttribute("appLoginInfo"   , appLoginInfo);

	    Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("pkCompanyStaff", responseVo.getBody().get("pkCompanyStaff"));
	    resultMap.put("appAuthToken", responseVo.getBody().get("authToken").toString());

	    return new ResponseVo(200, resultMap);
    }

	//로그아웃
	@GetMapping("/logout")
	public Object logout() {

		//세션 체크
		if(request.getSession().getAttribute("appAuthToken") != null){
//	        session.invalidate(); //모든 세션 제거
			request.getSession().removeAttribute("appAuthToken");
		}
		if(request.getSession().getAttribute("appLoginInfo") != null){
			request.getSession().removeAttribute("appLoginInfo");
		}
		return new ResponseVo(200);
	}

	//App 버전체크
	@GetMapping("/appVersion")
	public Object appVersion() throws Exception {

		Map<String, Object> bodyMap = appAuthService.appVersion();

		return new ResponseVo(200, bodyMap);
	}

}
