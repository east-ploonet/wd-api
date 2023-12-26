package com.saltlux.aice_fe.app.main.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import com.saltlux.aice_fe.app.main.service.AppMainService;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequestMapping("${apiVersionPrefix}/app")
@RestController
public class AppMainController extends BaseController {

	@Autowired
	ThrowException throwException;

	@Autowired
	AppMainService appMainService;

//    @Value("${auth.token.expired}")
//    public int tokenExpired;


    //App 메인화면 출력 정보
    @GetMapping("/mainInfo")
    public Object loginSession(
    ) throws Exception {

	    Map<String, Object> resultMap = appMainService.appMainInfo();

	    return new ResponseVo(200, resultMap);
    }

	//App 하단 네비 > 업무상태 (목록 포함)
	@GetMapping("/getMyBizState")
	public Object getMyBizState(
	) throws Exception {

		Map<String, Object> resultMap = appMainService.getMyBizState();

		return new ResponseVo(200, resultMap);
	}

	//App 하단 네비 > 업무상태 저장
	@PostMapping("/setMyBizState")
	public Object setMyBizState(
			@RequestBody Map<String, Object> bodyMap
	) throws Exception {

		//필수값 체크
		throwException.requestBodyRequied( bodyMap, "fd_staff_response_status_code");
		AppLoginInfoVo appLoginInfoVo   = getAppLoginInfoVo();

		CompanyStaffVo companyStaffVo   = new CompanyStaffVo();
		companyStaffVo.setFd_staff_response_status_code( bodyMap.get("fd_staff_response_status_code").toString() );
		companyStaffVo.setPk_company_staff( appLoginInfoVo.getLoginCompanyStaffPk() );
		companyStaffVo.setFk_modifier(      appLoginInfoVo.getLoginCompanyStaffPk() );

		Map<String, Object> resultMap   = appMainService.setMyBizState( companyStaffVo );

		return new ResponseVo(200, resultMap);
	}

	//App 하단 네비 > 마이페이지
	@PostMapping("/myPage")
	public Object myPage(
	) throws Exception {

		CompanyStaffVo companyStaffVo = appMainService.getMyPage();

		return new ResponseVo(200, companyStaffVo);
	}

	//App 하단 네비 > 상담문의 갯수
	@GetMapping("/myWorkflowCnt")
	public Object myWorkflowCnt(
	) throws Exception {

		AppLoginInfoVo appLoginInfoVo = getAppLoginInfoVo();

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setFk_assign_staff( appLoginInfoVo.getLoginCompanyStaffPk() );

		Map<String, Object> resultMap = appMainService.myWorkflowCnt( issueTicketVo );

		return new ResponseVo(200, resultMap);
	}

	//App 하단 네비 > 마이페이지
	@PostMapping("/pushNotiYn")
	public Object pushNotiYn(
			@RequestBody Map<String, Object> bodyMap
	) throws Exception {

		//필수값 체크
		throwException.requestBodyRequied( bodyMap, "pushNotiYn");
		AppLoginInfoVo appLoginInfoVo   = getAppLoginInfoVo();

		CompanyStaffVo companyStaffVo   = new CompanyStaffVo();
		companyStaffVo.setFd_push_noti_yn( bodyMap.get("pushNotiYn").toString() );
		companyStaffVo.setPk_company_staff( appLoginInfoVo.getLoginCompanyStaffPk() );
		companyStaffVo.setFk_modifier(      appLoginInfoVo.getLoginCompanyStaffPk() );

		appMainService.setStaffPushNotiYn( companyStaffVo );

		return new ResponseVo(200);
	}

}
