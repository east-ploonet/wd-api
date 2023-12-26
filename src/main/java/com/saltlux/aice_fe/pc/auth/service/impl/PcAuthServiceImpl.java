package com.saltlux.aice_fe.pc.auth.service.impl;


import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.AuthTokenVo;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe._baseline.util.IdGenerator;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.admin.vo.AdminVo;
import com.saltlux.aice_fe.pc.auth.dao.PcAuthDao;
import com.saltlux.aice_fe.pc.auth.service.PcAuthService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginAdminInfoVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.new_ojt.service.NewOjtService;
import com.saltlux.aice_fe.pc.staff.dao.AIStaffDao;
import com.saltlux.aice_fe.pc.staff.service.AIStaffService;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PcAuthServiceImpl extends BaseServiceImpl implements PcAuthService {

    @Autowired
    PcAuthDao pcAuthDao;

	@Autowired
	private PloonetApiService ploonetApiService;
	
	@Autowired
	private NewOjtService newOjtService;
	
	@Autowired
	private AIStaffDao aiStaffDao;
	
    @Autowired
    ThrowException throwException;

    @Value("${profile.name}")
    public String profileName;
    
  //로그인
    @Override
    public ResponseVo tokenLogin(CompanyStaffVo reqCompanyStaffVo)  throws Exception {

        HashMap<String, Object> bodyMap = new HashMap<String, Object>();

		CompanyStaffVo getCompanyStaff = new CompanyStaffVo();
		
		try {
			getCompanyStaff = pcAuthDao.getCompanyStaffByPk(reqCompanyStaffVo);
		}catch(Exception e){
			return new ResponseVo(409, "일치하는 계정이 없습니다.");
		}
		System.out.println("getCompanyStaff : " + getCompanyStaff);

	    //해당 id 회원 없음, 직원 계정 상태 : A1101[정상]
	    if( getCompanyStaff == null || !"A1101".equals( getCompanyStaff.getFd_staff_status_code() )) {
	        return new ResponseVo(204, "해당 계정이 없습니다.");
	    }
	    
	    //인증 토큰 세션등록
	    AuthTokenVo authTokenVo = AuthTokenVo.encodeToken( Long.toString(getCompanyStaff.getPk_company_staff()) );
	    bodyMap.put("authToken" , authTokenVo.getToken());

	    // ---------- 사용자 정보 세션등록 ----------
		PcLoginInfoVo pcLoginInfoVo = new PcLoginInfoVo();

		//직원
	    pcLoginInfoVo.setLoginCompanyStaffPk(   getCompanyStaff.getPk_company_staff()); // 직원 pk
	    pcLoginInfoVo.setLoginCompanyStaffId(   getCompanyStaff.getFd_staff_id());      // 직원 id
	    pcLoginInfoVo.setLoginCompanyStaffName( getCompanyStaff.getFd_staff_name());    // 직원 이름
	    pcLoginInfoVo.setFdStaffPhone( getCompanyStaff.getFd_staff_phone());    // 직원 전화번호
	    pcLoginInfoVo.setLoginStaffGender( getCompanyStaff.getFd_staff_gender_mf());    // 직원 성별..
        pcLoginInfoVo.setLoginLevelCode( getCompanyStaff.getFd_staff_level_code() );
        pcLoginInfoVo.setLoginRoleName( getCompanyStaff.getFd_name() );
		pcLoginInfoVo.setLoginUserType(getCompanyStaff.getUser_type());					// 유저 타입
		pcLoginInfoVo.setUuid(getCompanyStaff.getUuid());					// uuid

		//회사
	    pcLoginInfoVo.setLoginCompanyPk(        getCompanyStaff.getPk_company());       // 회사 pk
	    pcLoginInfoVo.setLoginCompanyName(      getCompanyStaff.getFd_company_name());  // 회사 이름
	    pcLoginInfoVo.setLoginCompanyId(        getCompanyStaff.getFd_company_id());    // 회사 ID(이메일 형식, @ploonet.com)

		if ( getCompanyStaff.getFd_company_logo_file_path() != null && !getCompanyStaff.getFd_company_logo_file_path().isEmpty() ) {
			pcLoginInfoVo.setLoginCompanyLogoUrl(pathBrowserStorage + getCompanyStaff.getFd_company_logo_file_path()); // 회사 로고 웹경로
		}

	    //부서
	    pcLoginInfoVo.setLoginCompanyStaffDept( getCompanyStaff.getFd_dept_name());     // 부서 이름

	    bodyMap.put("loginInfo" , pcLoginInfoVo);
	    // ---------- 사용자 정보 세션등록 ----------
	    
	    //---------- 사용자 정보 세션등록 ----------
	    
	    List<Object> getAiConfWork = newOjtService.getAiConfWork(getCompanyStaff.getPk_company());
	    
	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("fkCompany", getCompanyStaff.getPk_company());
	    Map<String, Object> staffMap = aiStaffDao.getAIMainStaff(paramMap);
	    
	    
	    bodyMap.put("getAiConfWork" , getAiConfWork);
	    bodyMap.put("mainAIStaff" , staffMap);
	    bodyMap.put("uuid" , getCompanyStaff.getUuid());
	    
	    return new ResponseVo(200, "로그인 성공", bodyMap);
    }
    
    //로그인
    @Override
    public ResponseVo login(CompanyStaffVo reqCompanyStaffVo, boolean login)  throws Exception {

        HashMap<String, Object> bodyMap = new HashMap<String, Object>();

		CompanyStaffVo getCompanyStaff = new CompanyStaffVo();

		try {
			getCompanyStaff = pcAuthDao.getCompanyStaffById(reqCompanyStaffVo);
		}catch(Exception e){
			System.out.println("e:" + e);
			return new ResponseVo(409, "중복 계정입니다. 관리자에게 문의해주세요.");
		}

	    //해당 id 회원 없음, 직원 계정 상태 : A1101[정상]
	    if( getCompanyStaff == null || !"A1101".equals( getCompanyStaff.getFd_staff_status_code() )) {
	        return new ResponseVo(204, "해당 id의 사용자가 없습니다.");
	    }
	    if(login) {
			Map<String, PasswordEncoder> encoders = new HashMap<>();
			encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
			PasswordEncoder sha256 = new DelegatingPasswordEncoder("sha256", encoders);
	
			//BCRYPT_ENCODER.encode(Common.NVL(reqCompanyStaffVo.getFd_staff_pw(), ""));
			
			//pw 불일치
			if (!BCRYPT_ENCODER.matches(reqCompanyStaffVo.getFd_staff_pw(), getCompanyStaff.getFd_staff_pw())) {
				return new ResponseVo(401, "비밀번호가 일치하지 않습니다.");
			}
	    }
	    //인증 토큰 세션등록
	    AuthTokenVo authTokenVo = AuthTokenVo.encodeToken( Long.toString(getCompanyStaff.getPk_company_staff()) );
	    bodyMap.put("authToken" , authTokenVo.getToken());
	    
	    //로그인 토큰 세션등록
	    
	    //Base64 인코더 Id,Pw
	    //String encoededgetId = Base64.getEncoder().encodeToString(getCompanyStaff.getFd_staff_id().getBytes());
	    //String encoededgetPw = Base64.getEncoder().encodeToString(getCompanyStaff.getFd_staff_pw().getBytes());
	    
	    //AuthTokenVo loginToken = AuthTokenVo.encodeToken(encoededgetId+"/"+encoededgetPw);
	    //bodyMap.put("LoginToken" , encoededgetId+"/"+encoededgetPw);
	    
	    // ---------- 사용자 정보 세션등록 ----------
		PcLoginInfoVo pcLoginInfoVo = new PcLoginInfoVo();

		//직원
	    pcLoginInfoVo.setLoginCompanyStaffPk(   getCompanyStaff.getPk_company_staff()); // 직원 pk
	    pcLoginInfoVo.setLoginCompanyStaffId(   getCompanyStaff.getFd_staff_id());      // 직원 id
	    pcLoginInfoVo.setLoginCompanyStaffName( getCompanyStaff.getFd_staff_name());    // 직원 이름
        pcLoginInfoVo.setLoginLevelCode( getCompanyStaff.getFd_staff_level_code() );
        pcLoginInfoVo.setLoginRoleName( getCompanyStaff.getFd_name() );
		pcLoginInfoVo.setLoginUserType(getCompanyStaff.getUser_type());					// 유저 타입
		pcLoginInfoVo.setFdCompanyMasterYn( getCompanyStaff.getFd_company_master_yn() ); // 회사 대표 직원
		pcLoginInfoVo.setUuid( getCompanyStaff.getUuid() ); // uuid
		
		//회사
	    pcLoginInfoVo.setLoginCompanyPk(        getCompanyStaff.getPk_company());       // 회사 pk
	    pcLoginInfoVo.setLoginCompanyName(      getCompanyStaff.getFd_company_name());  // 회사 이름
	    pcLoginInfoVo.setLoginCompanyId(        getCompanyStaff.getFd_company_id());    // 회사 ID(이메일 형식, @ploonet.com)

		if ( getCompanyStaff.getFd_company_logo_file_path() != null && !getCompanyStaff.getFd_company_logo_file_path().isEmpty() ) {
			pcLoginInfoVo.setLoginCompanyLogoUrl(getCompanyStaff.getFd_company_logo_file_path()); // 회사 로고 웹경로
		}

	    //부서
	    pcLoginInfoVo.setLoginCompanyStaffDept( getCompanyStaff.getFd_dept_name());     // 부서 이름
	    
	    pcLoginInfoVo.setUuid( getCompanyStaff.getUuid() ); 

	    bodyMap.put("loginInfo" , pcLoginInfoVo);
	    // ---------- 사용자 정보 세션등록 ----------
	    
	    List<Object> getAiConfWork = newOjtService.getAiConfWork(getCompanyStaff.getPk_company());
	    
	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("fkCompany", getCompanyStaff.getPk_company());
	    paramMap.put("fkCompanyStaff", getCompanyStaff.getPk_company_staff());
	    Map<String, Object> staffMap = aiStaffDao.getAIMainStaff(paramMap);
	    
	    Map<String, Object> brandTerms = aiStaffDao.getBrandTerms(paramMap);
	    
	    System.out.println(getCompanyStaff.getUuid());
	    bodyMap.put("getAiConfWork" , getAiConfWork);
	    bodyMap.put("mainAIStaff" , staffMap);
	    bodyMap.put("uuid" , getCompanyStaff.getUuid());
	    bodyMap.put("brandTerms" , brandTerms);
	    bodyMap.put("fkCompanyStaff", getCompanyStaff.getPk_company_staff());
	    bodyMap.put("fkCompany", getCompanyStaff.getPk_company());
	    return new ResponseVo(200, "로그인 성공", bodyMap);
    }
    
    @Override
	public Map<String, Object> updateAgree(String fkCompanyStaff, String agreeValue, String fkCompany) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		int result = pcAuthDao.updateAgree(fkCompanyStaff, agreeValue, fkCompany);
		
		resultMap.put("result"    , result);
		return resultMap;
	}
    
    
    
    //카카오 로그인
    @Override
    public ResponseVo kakaoLogin(CompanyStaffVo reqCompanyStaffVo)  throws Exception {

        HashMap<String, Object> bodyMap = new HashMap<String, Object>();

        CompanyStaffVo getCompanyStaff  = pcAuthDao.getCompanyStaffByKakao(reqCompanyStaffVo);

        if( getCompanyStaff == null || !"A1101".equals( getCompanyStaff.getFd_staff_status_code() )) {
            return new ResponseVo(204, "해당 id의 사용자가 없습니다.");
        }

        //인증 토큰 세션등록
        AuthTokenVo authTokenVo = AuthTokenVo.encodeToken( Long.toString(getCompanyStaff.getPk_company_staff()) );
        bodyMap.put("authToken" , authTokenVo.getToken());

        // ---------- 사용자 정보 세션등록 ----------
        PcLoginInfoVo pcLoginInfoVo = new PcLoginInfoVo();

        //직원
        pcLoginInfoVo.setLoginCompanyStaffPk(   getCompanyStaff.getPk_company_staff()); // 직원 pk
        pcLoginInfoVo.setLoginCompanyStaffId(   getCompanyStaff.getFd_staff_id());      // 직원 id
        pcLoginInfoVo.setLoginCompanyStaffName( getCompanyStaff.getFd_staff_name());    // 직원 이름

        //회사
        pcLoginInfoVo.setLoginCompanyPk(        getCompanyStaff.getPk_company());       // 회사 pk
        pcLoginInfoVo.setLoginCompanyName(      getCompanyStaff.getFd_company_name());  // 회사 이름
        pcLoginInfoVo.setLoginCompanyId(        getCompanyStaff.getFd_company_id());    // 회사 ID(이메일 형식, @ploonet.com)

        if ( getCompanyStaff.getFd_company_logo_file_path() != null && !getCompanyStaff.getFd_company_logo_file_path().isEmpty() ) {
            pcLoginInfoVo.setLoginCompanyLogoUrl(pathBrowserStorage + getCompanyStaff.getFd_company_logo_file_path()); // 회사 로고 웹경로
        }

        //부서
        pcLoginInfoVo.setLoginCompanyStaffDept( getCompanyStaff.getFd_dept_name());     // 부서 이름

        bodyMap.put("loginInfo" , pcLoginInfoVo);
        // ---------- 사용자 정보 세션등록 ----------

        return new ResponseVo(200, "로그인 성공", bodyMap);
    }

    @Override
    public ResponseVo loginByTest(CompanyStaffVo reqCompanyStaffVo)  throws Exception {

        HashMap<String, Object> bodyMap = new HashMap<String, Object>();

	    CompanyStaffVo getCompanyStaff  = pcAuthDao.getCompanyStaffByPk(reqCompanyStaffVo);

	    //해당 id 회원 없음, 직원 계정 상태 : A1101[정상]
	    if( getCompanyStaff == null || !"A1101".equals( getCompanyStaff.getFd_staff_status_code() )) {
	        return new ResponseVo(204, "해당 id의 사용자가 없습니다.");
	    }

	    //인증 토큰 세션등록
	    AuthTokenVo authTokenVo = AuthTokenVo.encodeToken( Long.toString(getCompanyStaff.getPk_company_staff()) );
	    bodyMap.put("authToken" , authTokenVo.getToken());

	    // ---------- 사용자 정보 세션등록 ----------
		PcLoginInfoVo pcLoginInfoVo = new PcLoginInfoVo();

		//직원
	    pcLoginInfoVo.setLoginCompanyStaffPk(   getCompanyStaff.getPk_company_staff()); // 직원 pk
	    pcLoginInfoVo.setLoginCompanyStaffId(   getCompanyStaff.getFd_staff_id());      // 직원 id
	    pcLoginInfoVo.setLoginCompanyStaffName( getCompanyStaff.getFd_staff_name());    // 직원 이름

		//회사
	    pcLoginInfoVo.setLoginCompanyPk(        getCompanyStaff.getPk_company());       // 회사 pk
	    pcLoginInfoVo.setLoginCompanyName(      getCompanyStaff.getFd_company_name());  // 회사 이름
	    pcLoginInfoVo.setLoginCompanyId(        getCompanyStaff.getFd_company_id());    // 회사 ID(이메일 형식, @ploonet.com)

		if ( getCompanyStaff.getFd_company_logo_file_path() != null && !getCompanyStaff.getFd_company_logo_file_path().isEmpty() ) {
			pcLoginInfoVo.setLoginCompanyLogoUrl(pathBrowserStorage + getCompanyStaff.getFd_company_logo_file_path()); // 회사 로고 웹경로
		}

	    //부서
	    pcLoginInfoVo.setLoginCompanyStaffDept( getCompanyStaff.getFd_dept_name());     // 부서 이름

	    bodyMap.put("loginInfo" , pcLoginInfoVo);
	    // ---------- 사용자 정보 세션등록 ----------

	    return new ResponseVo(200, "로그인 성공", bodyMap);
    }


	@Override
	public Map<String, Object> resetCompanyStaffTicketCode(CompanyStaffVo reqCompanyStaffVo)  throws Exception {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String ticketCode             = IdGenerator.generateUniqueId("T");
		Date   ticketCodeDate         = new Date();
		
		if(reqCompanyStaffVo.getFd_signup_keycode() == null || "".equals(reqCompanyStaffVo.getFd_signup_keycode())){
			reqCompanyStaffVo.setFd_signup_keycode( ticketCode );
		}

		Map<String, Object> sendMsgResult = null;

		try {
			if(reqCompanyStaffVo.getPk_company_staff() != 0L){
				pcAuthDao.resetCompanyStaffTicketCode(reqCompanyStaffVo);

				//-- 초대코드 발송 (API 연동)
//				CompanyStaffVo companyStaffVo = pcAuthDao.getCompanyStaffByPk(reqCompanyStaffVo);
				
				CompanyStaffVo companyStaffVo = pcAuthDao.getCompanyStaffByPkNoLevelCode(reqCompanyStaffVo); // 부서 정보 input 쓰면서 임시로 바뀐 부분
				companyStaffVo.setFd_signup_keycode(reqCompanyStaffVo.getFd_signup_keycode());
	            companyStaffVo.setFd_signup_keycode(ticketCode);
				sendMsgResult = ploonetApiService.sendMessageApi(companyStaffVo);
				System.out.println("sendMsgResult : " + sendMsgResult);
				/*
				[result body]
				  - msgId 	: 메세지 일련번호
				  - result 	: 처리 결과 구분값(Y:성공, N:실패)
				  - code 	: 처리결과 코드(실패시 실패 코드)
				  - error 	: 에러 메세지
				*/
			}
			resultMap.put("signupCode"    , reqCompanyStaffVo.getFd_signup_keycode());
			resultMap.put("signupCodeDate", FormatUtils.dateToStringCustomize(ticketCodeDate,"yyyy.MM.dd"));
			resultMap.put("sendMsgResult" , sendMsgResult);

		}catch(Exception e){
			System.out.println("세번쨰 에러 !!!!!!! e:" + e);
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> resetCompanyStaffTicketIndividualCode(CompanyStaffVo reqCompanyStaffVo) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String ticketCode             = IdGenerator.generateUniqueId("T");
		Date   ticketCodeDate         = new Date();

		if(reqCompanyStaffVo.getFd_signup_keycode() == null || "".equals(reqCompanyStaffVo.getFd_signup_keycode())){
			reqCompanyStaffVo.setFd_signup_keycode( ticketCode );
		}

		Map<String, Object> sendMsgResult = null;

		if(reqCompanyStaffVo.getPk_company_staff() != 0L){
			pcAuthDao.resetCompanyStaffTicketCode(reqCompanyStaffVo);

			//-- 초대코드 발송 (API 연동)
//			CompanyStaffVo companyStaffVo = pcAuthDao.getCompanyStaffByPk(reqCompanyStaffVo);

			sendMsgResult = ploonetApiService.sendMessageIndividualApi(reqCompanyStaffVo);
			/*
			[result body]
			  - msgId 	: 메세지 일련번호
			  - result 	: 처리 결과 구분값(Y:성공, N:실패)
			  - code 	: 처리결과 코드(실패시 실패 코드)
			  - error 	: 에러 메세지
			*/
		}

		resultMap.put("signupCode"    , reqCompanyStaffVo.getFd_signup_keycode());
		resultMap.put("signupCodeDate", FormatUtils.dateToStringCustomize(ticketCodeDate,"yyyy.MM.dd"));
		resultMap.put("sendMsgResult" , sendMsgResult);

		return resultMap;
	}

	@Override
	public PcLoginInfoVo getUserLoginById(long pk_company_staff) throws Exception {
		CompanyStaffVo companyStaffVo=new CompanyStaffVo();
		companyStaffVo.setPk_company_staff(pk_company_staff);
		CompanyStaffVo getCompanyStaff = pcAuthDao.getCompanyStaffByPk(companyStaffVo);
		PcLoginInfoVo pcLoginInfoVo = new PcLoginInfoVo();
		
		pcLoginInfoVo.setUuid(   getCompanyStaff.getUuid()); // 직원 pk
		pcLoginInfoVo.setLoginCompanyStaffPk(   getCompanyStaff.getPk_company_staff()); // 직원 pk
		pcLoginInfoVo.setLoginCompanyStaffId(   getCompanyStaff.getFd_staff_id());      // 직원 id
		pcLoginInfoVo.setLoginCompanyStaffName( getCompanyStaff.getFd_staff_name());    // 직원 이름
		pcLoginInfoVo.setLoginUserType(getCompanyStaff.getUser_type());
		
		pcLoginInfoVo.setFdStaffMobile(getCompanyStaff.getFd_staff_mobile());
		pcLoginInfoVo.setFdStaffPhone(getCompanyStaff.getFd_staff_phone());

		//회사
		pcLoginInfoVo.setLoginCompanyPk(        getCompanyStaff.getPk_company());       // 회사 pk
		pcLoginInfoVo.setLoginCompanyName(      getCompanyStaff.getFd_company_name());  // 회사 이름
		pcLoginInfoVo.setLoginCompanyId(        getCompanyStaff.getFd_company_id());    // 회사 ID(이메일 형식, @ploonet.com)
		pcLoginInfoVo.setLoginRoleName(getCompanyStaff.getRole_name());
		pcLoginInfoVo.setLoginLevelCode(getCompanyStaff.getFd_staff_level_code());

		if ( getCompanyStaff.getFd_company_logo_file_path() != null && !getCompanyStaff.getFd_company_logo_file_path().isEmpty() ) {
			pcLoginInfoVo.setLoginCompanyLogoUrl(pathBrowserStorage + getCompanyStaff.getFd_company_logo_file_path()); // 회사 로고 웹경로
		}

		//부서
		pcLoginInfoVo.setLoginCompanyStaffDept( getCompanyStaff.getFd_dept_name());     // 부서 이름
		return pcLoginInfoVo;
	}

	// 로그인
    @Override
    public ResponseVo login(CompanyStaffVo reqCompanyStaffVo, String authToken, String ssoIdToken, String uuid, boolean isSns)  throws Exception {


        HashMap<String, Object> bodyMap = new HashMap<String, Object>();

		CompanyStaffVo getCompanyStaff = new CompanyStaffVo();

		try {
			getCompanyStaff = pcAuthDao.getCompanyStaffById(reqCompanyStaffVo);
		}catch(Exception e){
			return new ResponseVo(409, "중복 계정입니다. 관리자에게 문의해주세요.");
		}

	    //해당 id 회원 없음, 직원 계정 상태 : A1101[정상]
	    if( getCompanyStaff == null || !"A1101".equals( getCompanyStaff.getFd_staff_status_code() )) {
	        return new ResponseVo(204, "해당 id의 사용자가 없습니다.");
	    }
	    
	    if(!isSns) {
			Map<String, PasswordEncoder> encoders = new HashMap<>();
			encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
			PasswordEncoder sha256 = new DelegatingPasswordEncoder("sha256", encoders);
	
			//pw 불일치
			if (!BCRYPT_ENCODER.matches(reqCompanyStaffVo.getFd_staff_pw(), getCompanyStaff.getFd_staff_pw())) {
				return new ResponseVo(401, "비밀번호가 일치하지 않습니다.");
			}
	    }
	    //인증 토큰 세션등록
	    AuthTokenVo authTokenVo = AuthTokenVo.encodeToken( Long.toString(getCompanyStaff.getPk_company_staff()) );
	    bodyMap.put("authToken" , authTokenVo.getToken());
	    
	    //로그인 토큰 세션등록
	    
	    //Base64 인코더 Id,Pw
	    //String encoededgetId = Base64.getEncoder().encodeToString(getCompanyStaff.getFd_staff_id().getBytes());
	    //String encoededgetPw = Base64.getEncoder().encodeToString(getCompanyStaff.getFd_staff_pw().getBytes());
	    
	    //AuthTokenVo loginToken = AuthTokenVo.encodeToken(encoededgetId+"/"+encoededgetPw);
	    //bodyMap.put("LoginToken" , encoededgetId+"/"+encoededgetPw);
	    
	    // ---------- 사용자 정보 세션등록 ----------
		PcLoginInfoVo pcLoginInfoVo = new PcLoginInfoVo();
		
		pcLoginInfoVo.setAuthToken(authToken);
		pcLoginInfoVo.setUuid(uuid);
		
		// 마지막 로그인 날짜
		pcLoginInfoVo.setLoginDate(getCompanyStaff.getFd_login_date());
		pcLoginInfoVo.setLoginStaffMobile( getCompanyStaff.getFd_staff_mobile() );
		pcLoginInfoVo.setLoginStaffMobileType ( getCompanyStaff.getFd_staff_mobile_type() );
		pcLoginInfoVo.setLoginStaffBirth( getCompanyStaff.getFd_staff_birth() );
		pcLoginInfoVo.setLoginStaffGender( getCompanyStaff.getFd_staff_gender_mf() );
		
		//직원
	    pcLoginInfoVo.setLoginCompanyStaffPk(   getCompanyStaff.getPk_company_staff()); // 직원 pk
	    pcLoginInfoVo.setLoginCompanyStaffId(   getCompanyStaff.getFd_staff_id());      // 직원 id
	    pcLoginInfoVo.setLoginCompanyStaffName( getCompanyStaff.getFd_staff_name());    // 직원 이름
        pcLoginInfoVo.setLoginLevelCode( getCompanyStaff.getFd_staff_level_code() );
        pcLoginInfoVo.setLoginRoleName( getCompanyStaff.getFd_name() );
		pcLoginInfoVo.setLoginUserType(getCompanyStaff.getUser_type());					// 유저 타입

		//회사
	    pcLoginInfoVo.setLoginCompanyPk(        getCompanyStaff.getPk_company());       // 회사 pk
	    pcLoginInfoVo.setLoginCompanyName(      getCompanyStaff.getFd_company_name());  // 회사 이름
	    pcLoginInfoVo.setLoginCompanyId(        getCompanyStaff.getFd_company_id());    // 회사 ID(이메일 형식, @ploonet.com)

		if ( getCompanyStaff.getFd_company_logo_file_path() != null && !getCompanyStaff.getFd_company_logo_file_path().isEmpty() ) {
			pcLoginInfoVo.setLoginCompanyLogoUrl(pathBrowserStorage + getCompanyStaff.getFd_company_logo_file_path()); // 회사 로고 웹경로
		}

	    //부서
	    pcLoginInfoVo.setLoginCompanyStaffDept( getCompanyStaff.getFd_dept_name());     // 부서 이름
	    
	    // sso 정보
	    pcLoginInfoVo.setSsoIdToken(ssoIdToken);
	    
	    bodyMap.put("loginInfo" , pcLoginInfoVo);
	    // ---------- 사용자 정보 세션등록 ----------
	    
	    List<Object> getAiConfWork = newOjtService.getAiConfWork(getCompanyStaff.getPk_company());
	    bodyMap.put("getAiConfWork" , getAiConfWork);
	    
	    
	    return new ResponseVo(200, "로그인 성공", bodyMap);
    }
}
