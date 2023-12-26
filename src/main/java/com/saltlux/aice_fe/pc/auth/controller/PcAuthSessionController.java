package com.saltlux.aice_fe.pc.auth.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.service.PcAuthService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("${apiVersionPrefix}/workStage/auth")
@RestController
public class PcAuthSessionController extends BaseController {

    @Autowired
    PcAuthService pcAuthService;
    
    @Autowired
    ThrowException throwException;
    
    @Value("${ploonet.api.base.billing.url}")
    public String billingUrl;
    
    @Value("${ploonet.sso.url.host}")
    public String ssoHost;

	@Value("${ploonet.sso.url.api.login}")
    public String ssoLginApiUrl;

	@Value("${ploonet.sso.url.api.logout}")
    public String ssoLogoutApiUrl;

	@Value("${ploonet.sso.url.api.apply}")
    public String ssoApplyApiUrl;
	
	@Value("${ploonet.sso.url.api.token}")
   public String ssoTokenApiUrl;

	@Value("${ploonet.sso.client.id}")
    public String ssoClientId;
	
	@Value("${ploonet.sso.client.secret}")
    public String ssoClientSecret;
	
	@Value("${ploonet.api.union.api.url}")
    public String unionApi;
	
    //토큰 로그인 처리 세션
    @GetMapping("/Tokenlogin")
    public Object Tokenlogin(HttpServletRequest request) throws Exception {
    	
    	String pcloginToken = Base64.getEncoder().encodeToString(("anonymous_hot2590@naver.com").getBytes());
    	String token = request.getParameter("token"); // 342
        //필수값 체크
    	if(Common.isBlank(token) || token.equals("null")) return new ResponseVo(400, "토큰이 없습니다.");
    	
    	// 토큰 검증시 검증 안된 토큰값은 실패처리로 줘야함
    	//String apiResult = Common.getRestDataApi("https://dev-backdoor.ploonet.com/account/svc/authentication", token, "POST");
    	//String apiResult = Common.getRestDataApi("http://10.0.131.55:8989/account/svc/authentication", token, "PUT");
    	String apiResult = Common.getRestDataApi(billingUrl + "/account/svc/authentication", token, "PUT");
    	
    	if(apiResult == null) return new ResponseVo(400, "토큰검증에 실패했습니다. 관리자에게 문의해주세요.");
    	
    	JSONParser jsonParser = new JSONParser();

    	org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject)jsonParser.parse(apiResult);
    	int tokenResult = Common.parseInt(jsonObj.get("result"));
    	int apiMemberSeq  = Common.parseInt(jsonObj.get("memberSeq"));
    	
    	if(tokenResult < 1) {
    		return new ResponseVo(400, "잘못된 토큰입니다.");
    	}
    	int pkComStaff = Common.parseInt(apiMemberSeq); // token -> apiMemberSeq로 변경
    
    	
    	//int pkComStaff = 710;
        CompanyStaffVo reqCompanyStaffVo = new CompanyStaffVo();
        
        reqCompanyStaffVo.setPk_company_staff(pkComStaff);
        
        ResponseVo responseVo = null;
        responseVo = pcAuthService.tokenLogin(reqCompanyStaffVo);
        
        Object getAiConfWork = responseVo.getBody().get("getAiConfWork");
	    Object mainAIStaff = responseVo.getBody().get("mainAIStaff");

	    
	    // 여기서 추가되는 플래그 가져올 예정 (quick)
	    String uuid = null;
        if(responseVo.getBody().get("uuid") != null) uuid = responseVo.getBody().get("uuid").toString();
	    
    	// quick
	    
        
        //로그인 실패
        if( 200 != responseVo.getStatus() ){
	        return responseVo;
        }


        //세션 생성
	    request.getSession().invalidate(); //모든 세션 제거
	    request.getSession().removeAttribute("pcAuthToken");
	    request.getSession().removeAttribute("pcLoginInfo");

	    String pcAuthToken = responseVo.getBody().get("authToken").toString();
	    Object pcLoginInfo = responseVo.getBody().get("loginInfo");

	    request.getSession().setAttribute("pcAuthToken"   , pcAuthToken);
	    request.getSession().setAttribute("pcLoginInfo"   , pcLoginInfo);
	    
	    
        String quickUrl = "/interface-system/" + uuid;//2023 11 07 변경됨
        String quickStartStatus = "-1";
        Long pkCompanyStaff = null;
        JSONParser jsonParserquick = new JSONParser();
        Map<String, Object> results = new HashMap<>();
        try {
        	String apiResultQuick = Common.getRestDataUnionApiGet(unionApi + quickUrl, pcAuthToken, billingUrl); //2023 11 07 변경됨
        	
        	System.out.println("apiResult ::::: " + apiResult);
        	
        	org.json.simple.JSONObject jsonObjQuick = (org.json.simple.JSONObject) jsonParserquick.parse(apiResultQuick);
			
        	Map<String, Object> resultMapList1 = (Map<String, Object>) jsonObjQuick.get("data");
			org.json.simple.JSONArray array = (org.json.simple.JSONArray) resultMapList1.get("ais");
			
			
			//1.변경staff 값
			Long mainAistaffSeq = null;
			Long quickAistaffSeq = null;

			//기존AI
			boolean mainAi = false;
			//퀵스타트AI
			boolean quickAi = false;
			
	
			for(int i=0; i<array.size(); i++) {
				org.json.simple.JSONObject staffList = (org.json.simple.JSONObject) array.get(i);
				if(staffList.get("botDisplay").equals("Y")) {
					quickStartStatus = staffList.get("quickStartStatus").toString();
				}
				if((Long)staffList.get("quickStartStatus") == -1) {
					mainAi = true;
					mainAistaffSeq = (Long)staffList.get("memberSeq");
				}else if(((Long)staffList.get("quickStartStatus") > 0)){
					quickAi = true;
					quickAistaffSeq = (Long)staffList.get("memberSeq");
				}
				if(mainAi && quickAi) {
					pkCompanyStaff = mainAistaffSeq;
				}else {
					if(mainAi) {
						pkCompanyStaff = mainAistaffSeq;
					}else {
						pkCompanyStaff = quickAistaffSeq;
					}
				}
				
				
			} 
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Exception" + e);
		}

        results.put("pcAuthToken", pcAuthToken);
        results.put("pcLoginInfo", pcLoginInfo);
        results.put("getAiConfWork", getAiConfWork);
        results.put("mainAIStaff", mainAIStaff);
        results.put("quickStartStatus", quickStartStatus);
        results.put("pkCompanyStaff", pkCompanyStaff);
	    // 여기서 추가되는 플래그 가져올 예정
        
        return new ResponseVo(200, results);
    }
    
    
    @PostMapping("/updateAgree")
    public Object updateAgree(@RequestBody Map<String, Object> bodyMap) throws Exception {
    	
    	String fkCompanyStaff = bodyMap.get("fkCompanyStaff").toString();
    	String agreeValue = bodyMap.get("agreeValue").toString();
    	String fkCompany = bodyMap.get("fkCompany").toString();
    	
    	Map<String, Object> result = pcAuthService.updateAgree(fkCompanyStaff, agreeValue, fkCompany);
    	
    	return new ResponseVo(200, result);
    }
    
    //로그인 처리 (세션용)
    @PostMapping("/login")
    public Object loginSession(@RequestBody Map<String, Object> bodyMap) throws Exception {
        //필수값 체크
        throwException.requestBodyRequied( bodyMap, "memberId", "memberPw");

        //관리자 정보 조회
	    CompanyStaffVo companyStaffVo   = new CompanyStaffVo();
	    
	    companyStaffVo.setFd_staff_id( bodyMap.get("memberId").toString() );
	    companyStaffVo.setFd_staff_pw( bodyMap.get("memberPw").toString() );

        ResponseVo responseVo = null;

        // TODO : 임시 테스트 계정처리(tbl_company_staff : pk 1)
        if( "test".equalsIgnoreCase( bodyMap.get("memberId").toString() )){

            companyStaffVo.setPk_company_staff( 1 );
            responseVo = pcAuthService.loginByTest(companyStaffVo);

        } else {
            responseVo = pcAuthService.login(companyStaffVo, true);
        }
        
        //로그인 실패
        if( 200 != responseVo.getStatus() ){
	        return responseVo;
        }
        
        if(responseVo.getBody().get("brandTerms") == null && responseVo.getBody().get("uuid") == null) {
        	Object brandTerms = responseVo.getBody().get("brandTerms");
    	    
    	    request.getSession().setAttribute("brandTerms"   , brandTerms);
    	    
    	    Map<String, Object> results = new HashMap<>();
    	    Object fkCompanyStaff = responseVo.getBody().get("fkCompanyStaff");
    	    Object fkCompany = responseVo.getBody().get("fkCompany");
    	    
    	    results.put("brandTerms", null);
    	    results.put("uuid", responseVo.getBody().get("uuid"));
            results.put("fkCompanyStaff", fkCompanyStaff);
            results.put("fkCompany", fkCompany);
        	return new ResponseVo(200, results);
        }
        
        String uuid = null;
        if(responseVo.getBody().get("uuid") != null) uuid = responseVo.getBody().get("uuid").toString();
        
        // quick
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", uuid);
        
        //여기에 성공하면 저장.
        String encodedgetId = Base64.getEncoder().encodeToString(bodyMap.get("memberId").toString().getBytes());
        String encodedgetPw = Base64.getEncoder().encodeToString(bodyMap.get("memberPw").toString().getBytes());
        
        String pcloginToken = encodedgetId + "/" + encodedgetPw;
        
        
        //세션 생성
	    request.getSession().invalidate(); //모든 세션 제거
	    request.getSession().removeAttribute("pcAuthToken");
	    request.getSession().removeAttribute("pcLoginInfo");
	    

	    String pcAuthToken = responseVo.getBody().get("authToken").toString();
	    Object pcLoginInfo = responseVo.getBody().get("loginInfo");
	    Object getAiConfWork = responseVo.getBody().get("getAiConfWork");
	    Object mainAIStaff = responseVo.getBody().get("mainAIStaff");
	    
	    
	    request.getSession().setAttribute("pcAuthToken"   , pcAuthToken);
	    request.getSession().setAttribute("pcLoginInfo"   , pcLoginInfo);
	    request.getSession().setAttribute("pcLoginToken", pcloginToken);
	    
//	    request.getSession().setMaxInactiveInterval(tokenExpired);
	    
        String quickUrl = "/interface-system/" + uuid;//2023 11 07 변경됨
        String quickStartStatus = "-1";
        JSONParser jsonParser = new JSONParser();
        Map<String, Object> results = new HashMap<>();
        try {
        	String apiResult = Common.getRestDataUnionApiGet(unionApi + quickUrl, pcAuthToken, billingUrl); //2023 11 07 변경됨
        	
        	System.out.println("apiResult ::::: " + apiResult);
        	
        	org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject) jsonParser.parse(apiResult);
			
        	Map<String, Object> resultMapList1 = (Map<String, Object>) jsonObj.get("data");
			org.json.simple.JSONArray array = (org.json.simple.JSONArray) resultMapList1.get("ais");
			
			
	
			for(int i=0; i<array.size(); i++) {
				org.json.simple.JSONObject staffList = (org.json.simple.JSONObject) array.get(i);
				if(staffList.get("botDisplay").equals("Y")) {
					quickStartStatus = staffList.get("quickStartStatus").toString();
					//results.put("data", staffList);
				}
			} 
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Exception" + e);
		}
	    
        results.put("pcAuthToken", pcAuthToken);
        results.put("pcLoginInfo", pcLoginInfo);
        results.put("pcLoginToken", pcloginToken);
        results.put("getAiConfWork", getAiConfWork);
        results.put("mainAIStaff", mainAIStaff);
        results.put("brandTerms", responseVo.getBody().get("brandTerms"));
        results.put("uuid", uuid);
        results.put("quickStartStatus", quickStartStatus);
        
        return new ResponseVo(200, results);
    }
    
    @GetMapping("/loginGetToken")
    public Object loginGetTokenSession() throws Exception {
    	
    	
    	PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
    	
    	
    	Map<String, Object> results = new HashMap<>();
    	
    	if(pcLoginInfoVo == null) return new ResponseVo(200, results);
    	String memberId = pcLoginInfoVo.getLoginCompanyStaffId();
    	String pcloginToken = Base64.getEncoder().encodeToString(("anonymous_"+memberId).getBytes());
    	
        
        //String apiResultLogin = Common.getRestDataApiGet("https://dev-backdoor.ploonet.com/account/anon/svc/temp/authentication/"+pcloginToken, pcloginToken, "GET");
    	//String apiResultLogin = Common.getRestDataApiGet("http://10.0.131.55:8989/account/anon/svc/temp/authentication/"+pcloginToken, pcloginToken, "GET");
    	String apiResultLogin = Common.getRestDataApiGet(billingUrl + ":8989/account/anon/svc/temp/authentication/"+pcloginToken, pcloginToken, "GET");
      
        JSONParser jsonParser = new JSONParser();
        
    	org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject)jsonParser.parse(apiResultLogin);
    	String tokenResult = jsonObj.get("authToken").toString();
    	
        results.put("authToken", tokenResult);
        
        return new ResponseVo(200, results);
    }

    @PostMapping("/kakaoLogin")
    public Object kakaoLoginSession(
		    @RequestBody Map<String, Object> bodyMap
    ) throws Exception {


        //필수값 체크
        throwException.requestBodyRequied( bodyMap, "kakaoCi");

        //관리자 정보 조회
	    CompanyStaffVo companyStaffVo   = new CompanyStaffVo();
	    companyStaffVo.setFd_staff_ci( bodyMap.get("kakaoCi").toString() );

        ResponseVo responseVo = null;

        // TODO : 임시 테스트 계정처리(tbl_company_staff : pk 1)
        responseVo = pcAuthService.kakaoLogin(companyStaffVo);

        //계정이 없는경우
        if( 200 != responseVo.getStatus() ){
	        return responseVo;
        }

        PcLoginInfoVo pcLoginInfoVo = new PcLoginInfoVo();
        pcLoginInfoVo = (PcLoginInfoVo) responseVo.getBody().get("loginInfo");

        //Map<String, Object> resultMap = (Map<String, Object>) responseVo.getBody().get("loginInfo");


        //세션 생성
        request.getSession().invalidate(); //모든 세션 제거
	    request.getSession().removeAttribute("pcAuthToken");
	    request.getSession().removeAttribute("pcLoginInfo");
	    request.getSession().setAttribute("pcAuthToken"   , responseVo.getBody().get("authToken").toString());
	    request.getSession().setAttribute("pcLoginInfo"   , responseVo.getBody().get("loginInfo"));
//	    request.getSession().setMaxInactiveInterval(tokenExpired);

	    Map<String, Object> resultMap = new HashMap<>();
	    resultMap.put("workStageLoginId", pcLoginInfoVo.getLoginCompanyStaffId());
	    resultMap.put("pcAuthToken", responseVo.getBody().get("authToken").toString());
	    resultMap.put("pcLoginInfo", responseVo.getBody().get("loginInfo"));

		return new ResponseVo(200, resultMap);

    }

  //로그아웃
    @GetMapping("/logout")
    public Object logout() {

        //세션 체크
        if(request.getSession().getAttribute("pcAuthToken") != null){
//	        session.invalidate(); //모든 세션 제거
	        request.getSession().removeAttribute("pcAuthToken");
        }
	    if(request.getSession().getAttribute("pcLoginInfo") != null){
		    request.getSession().removeAttribute("pcLoginInfo");
	    }
	    return new ResponseVo(200);
    }

	//초대코드 생성
	@PostMapping("/signUpKeyCode")
	public Object signUpKeyCode(
			@RequestBody Map<String, Object> bodyMap
	) throws Exception {

		//필수값 체크
		throwException.requestBodyRequied( bodyMap, "staffPk");

		CompanyStaffVo reqCompanyStaffVo = new CompanyStaffVo();
		reqCompanyStaffVo.setPk_company_staff( Long.parseLong( bodyMap.get("staffPk").toString() ) );

		if(Long.parseLong(bodyMap.get("staffPk").toString()) != 0L){

			reqCompanyStaffVo.setFd_signup_keycode(Common.NVL(bodyMap.get("signupKeycode"), ""));
		}

		Map<String, Object> resultMap = pcAuthService.resetCompanyStaffTicketCode( reqCompanyStaffVo);

		return new ResponseVo(200, resultMap);
	}

    // 로그인 정보 조회 (Get Session)
    @GetMapping("/login/info")
    public Object loginInfo() {

        Map<String, Object> resultMap = new HashMap<>();

        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        
        if ( pcLoginInfoVo == null ) {

            return new ResponseVo(204);

        } else {

            resultMap.put( "loginCompanyPk"         , pcLoginInfoVo.getLoginCompanyPk()         );
            resultMap.put( "loginCompanyName"       , pcLoginInfoVo.getLoginCompanyName()       );
            resultMap.put( "loginCompanyId"         , pcLoginInfoVo.getLoginCompanyId()         );
            resultMap.put( "loginCompanyLogoUrl"    , pcLoginInfoVo.getLoginCompanyLogoUrl()    );
            //
            resultMap.put( "loginCompanyStaffPk"    , pcLoginInfoVo.getLoginCompanyStaffPk()    );
            resultMap.put( "loginUserType"          , pcLoginInfoVo.getLoginUserType()          );
            resultMap.put( "loginCompanyStaffId"    , pcLoginInfoVo.getLoginCompanyStaffId()    );
            resultMap.put( "loginCompanyStaffName"  , pcLoginInfoVo.getLoginCompanyStaffName()  );
            resultMap.put( "loginCompanyStaffDept"  , pcLoginInfoVo.getLoginCompanyStaffDept()  );
            
            resultMap.put( "loginFdStaffMobile"  , pcLoginInfoVo.getFdStaffMobile()  );
            resultMap.put( "loginFdStaffPhone"  , pcLoginInfoVo.getFdStaffPhone()  );
            
        }

        return new ResponseVo(200, resultMap);
    }
    
    
    
}
