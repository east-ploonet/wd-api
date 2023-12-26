package com.saltlux.aice_fe.app.auth.service.impl;


import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.AuthTokenVo;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.app.auth.dao.AppAuthDao;
import com.saltlux.aice_fe.app.auth.service.AppAuthService;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.app.auth.vo.AppVersionVo;
import com.saltlux.aice_fe.pc.auth.dao.PcAuthDao;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AppAuthServiceImpl extends BaseServiceImpl implements AppAuthService {

    @Autowired
    AppAuthDao appAuthDao;

    @Autowired
    ThrowException throwException;

    @Value("${profile.name}")
    public String profileName;

	@Autowired
	PcAuthDao pcAuthDao;

    //로그인
    @Override
    public ResponseVo login(CompanyStaffVo reqCompanyStaffVo)  throws Exception {

	    HashMap<String, Object> bodyMap = new HashMap<String, Object>();
	    CompanyStaffVo getCompanyStaff = null;

	    // find staff by name when login -> if not exist, throw exception
	    CompanyStaffVo companyStaffVoWithStaffName = new CompanyStaffVo();
		BeanUtils.copyProperties(reqCompanyStaffVo, companyStaffVoWithStaffName, "fd_staff_mobile", "fd_signup_keycode");
	    if (null == appAuthDao.getCompanyStaffBySignupCode(companyStaffVoWithStaffName) || appAuthDao.getCompanyStaffBySignupCode(companyStaffVoWithStaffName).size() == 0) {
			return new ResponseVo(204, "해당 이름의 사용자가 없습니다.");
		}

		// find staff by mobile when login -> if not exist, throw exception
		CompanyStaffVo companyStaffVoWithStaffMobile = new CompanyStaffVo();
		BeanUtils.copyProperties(reqCompanyStaffVo, companyStaffVoWithStaffMobile, "fd_staff_name", "fd_signup_keycode");
		if (null == appAuthDao.getCompanyStaffBySignupCode(companyStaffVoWithStaffMobile) || appAuthDao.getCompanyStaffBySignupCode(companyStaffVoWithStaffMobile).size() == 0) {
			return new ResponseVo(204, "해당 전화번호의 사용자가 없습니다.");
		}

		// find staff by signup code when login -> if not exist, throw exception
		CompanyStaffVo companyStaffVoWithStaffSignUpCode = new CompanyStaffVo();
		BeanUtils.copyProperties(reqCompanyStaffVo, companyStaffVoWithStaffSignUpCode, "fd_staff_name", "fd_staff_mobile");
		if (null == appAuthDao.getCompanyStaffBySignupCode(companyStaffVoWithStaffSignUpCode) || appAuthDao.getCompanyStaffBySignupCode(companyStaffVoWithStaffSignUpCode).size() == 0) {
			return new ResponseVo(204, "해당 코드의 사용자가 없습니다.");
		}

		if (appAuthDao.getCompanyStaffBySignupCode(companyStaffVoWithStaffSignUpCode).size() > 1) {
			return new ResponseVo(204, "중복 계정입니다. 관리자에게 문의해주세요.");
		}

	    if(reqCompanyStaffVo.getPk_company_staff() > 0){
			getCompanyStaff  = appAuthDao.getCompanyStaffByPk(reqCompanyStaffVo);
		} else {
	    	if (null != appAuthDao.getCompanyStaffBySignupCode(reqCompanyStaffVo) && appAuthDao.getCompanyStaffBySignupCode(reqCompanyStaffVo).size() > 0) {
				getCompanyStaff = appAuthDao.getCompanyStaffBySignupCode(reqCompanyStaffVo).get(0);
			}
		}

		//해당 id 회원 없음, 직원 계정 상태 : A1101[정상]
		if( getCompanyStaff == null || !"A1101".equals( getCompanyStaff.getFd_staff_status_code() )) {
			return new ResponseVo(204, "일치하는 사용자가 없습니다.");
		}
	    reqCompanyStaffVo.setPk_company_staff( getCompanyStaff.getPk_company_staff());

	    //인증 토큰 세션등록
	    AuthTokenVo authTokenVo = AuthTokenVo.encodeToken(
			    Long.toString(getCompanyStaff.getPk_company_staff()),
			    getCompanyStaff.getFd_signup_keycode()
	    );
	    bodyMap.put("authToken" , authTokenVo.getToken());

	    // ---------- 사용자 정보 세션등록 ----------
	    AppLoginInfoVo appLoginInfoVo = new AppLoginInfoVo();

	    //직원
	    appLoginInfoVo.setLoginCompanyStaffPk(   getCompanyStaff.getPk_company_staff()); // 직원 pk
	    appLoginInfoVo.setLoginCompanyStaffId(   getCompanyStaff.getFd_staff_id());      // 직원 id
	    appLoginInfoVo.setLoginCompanyStaffName( getCompanyStaff.getFd_staff_name());    // 직원 이름

	    //회사
	    appLoginInfoVo.setLoginCompanyPk(        getCompanyStaff.getPk_company());       // 회사 pk
	    appLoginInfoVo.setLoginCompanyName(      getCompanyStaff.getFd_company_name());  // 회사 이름
	    appLoginInfoVo.setLoginCompanyId(        getCompanyStaff.getFd_company_id());    // 회사 ID(이메일 형식, @ploonet.com)

	    if ( getCompanyStaff.getFd_company_logo_file_path() != null && !getCompanyStaff.getFd_company_logo_file_path().isEmpty() ) {
		    appLoginInfoVo.setLoginCompanyLogoUrl(pathBrowserStorage + getCompanyStaff.getFd_company_logo_file_path()); // 회사 로고 웹경로
	    }

	    //부서
	    appLoginInfoVo.setLoginCompanyStaffDept( getCompanyStaff.getFd_dept_name());     // 부서 이름

		bodyMap.put("pkCompanyStaff", getCompanyStaff.getPk_company_staff());
	    bodyMap.put("loginInfo" , appLoginInfoVo);
	    // ---------- 사용자 정보 세션등록 ----------

	    // 최근 로그인 일시, 푸시 토큰 업데이트
	    int rltUpdateCnt = appAuthDao.updateCompanyStaffLogin(reqCompanyStaffVo);
	    int rltInsertCnt = appAuthDao.insertCompanyStaffLogin(reqCompanyStaffVo);


	    return new ResponseVo(200, "로그인 성공", bodyMap);
    }


	@Override
	public Map<String, Object> appVersion()  throws Exception {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		String appVersion   = "";

		List<AppVersionVo> pppVersionVoList = appAuthDao.getAppVersion();
		for(AppVersionVo appVersionVo : pppVersionVoList){

			appVersion  = Integer.toString(appVersionVo.getFd_version_major());
			appVersion += Integer.toString(appVersionVo.getFd_version_major());

			resultMap.put( appVersionVo.getFd_os_code(), appVersionVo.getFd_version_major() +"."+ appVersionVo.getFd_version_minor() +"."+ appVersionVo.getFd_version_patch());
		}

		return resultMap;
	}

	@Override
	public AppLoginInfoVo getUserLoginById(long pk_company_staff) throws Exception {
		CompanyStaffVo getCompanyStaff = pcAuthDao.getCompanyStaffByPk(new CompanyStaffVo(pk_company_staff));
		AppLoginInfoVo appLoginInfoVo = new AppLoginInfoVo();

		appLoginInfoVo.setLoginCompanyStaffPk(   getCompanyStaff.getPk_company_staff()); // 직원 pk
		appLoginInfoVo.setLoginCompanyStaffId(   getCompanyStaff.getFd_staff_id());      // 직원 id
		appLoginInfoVo.setLoginCompanyStaffName( getCompanyStaff.getFd_staff_name());    // 직원 이름

		//회사
		appLoginInfoVo.setLoginCompanyPk(        getCompanyStaff.getPk_company());       // 회사 pk
		appLoginInfoVo.setLoginCompanyName(      getCompanyStaff.getFd_company_name());  // 회사 이름
		appLoginInfoVo.setLoginCompanyId(        getCompanyStaff.getFd_company_id());    // 회사 ID(이메일 형식, @ploonet.com)
		appLoginInfoVo.setLoginRoleName(getCompanyStaff.getRole_name());
		appLoginInfoVo.setLoginLevelCode(getCompanyStaff.getFd_staff_level_code());

//		if ( getCompanyStaff.getFd_company_logo_file_path() != null && !getCompanyStaff.getFd_company_logo_file_path().isEmpty() ) {
//			appLoginInfoVo.setLoginCompanyLogoUrl(pathBrowserStorage + getCompanyStaff.getFd_company_logo_file_path()); // 회사 로고 웹경로
//		}

		//부서
		appLoginInfoVo.setLoginCompanyStaffDept( getCompanyStaff.getFd_dept_name());     // 부서 이름
		return appLoginInfoVo;
	}
}
