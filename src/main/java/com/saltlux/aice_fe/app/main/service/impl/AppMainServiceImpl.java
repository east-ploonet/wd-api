package com.saltlux.aice_fe.app.main.service.impl;


import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.app.auth.dao.AppAuthDao;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.app.issue.dao.IssueDao;
import com.saltlux.aice_fe.pc.issue.dao.TicketIssueDao;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import com.saltlux.aice_fe.app.main.dao.AppMainDao;
import com.saltlux.aice_fe.app.main.service.AppMainService;
import com.saltlux.aice_fe.commonCode.vo.CodeVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppMainServiceImpl extends BaseServiceImpl implements AppMainService {

	@Autowired
	AppAuthDao appAuthDao;

	@Autowired
	AppMainDao appMainDao;

	@Autowired
	private TicketIssueDao ticketIssueDao;

	@Autowired
	private IssueDao issueDao;

	@Autowired
    ThrowException throwException;

    @Value("${profile.name}")
    public String profileName;

	//로그인
	@Override
	public Map<String, Object> appMainInfo()  throws Exception {

		AppLoginInfoVo appLoginInfoVo       = getAppLoginInfoVo();

		Map<String, Object> resultMap       = new HashMap<String, Object>();
		resultMap.put("staffName", appLoginInfoVo.getLoginCompanyStaffName());

		IssueTicketVo issueTicketVo         = new IssueTicketVo();
		issueTicketVo.setFk_assign_staff( appLoginInfoVo.getLoginCompanyStaffPk() );

		int totalCnt                        = ticketIssueDao.getTicketAllCnt(issueTicketVo);
		Map<String, Long> workflowCntMap = issueDao.getTicketWorkflowCnt(issueTicketVo);

		resultMap.put("totalCnt"            , totalCnt);
		resultMap.put("ing_cnt"             , workflowCntMap.get("ing_cnt"));
		resultMap.put("end_cnt"             , workflowCntMap.get("end_cnt"));
		resultMap.put("hold_cnt"            , workflowCntMap.get("hold_cnt"));
		resultMap.put("fd_staff_ai_uid"     , workflowCntMap.get("fd_staff_ai_uid"));

		if(totalCnt == 0){
			resultMap.put("ing_progress"    , 0 );
		}else{
			double progress = ((double)workflowCntMap.get("end_cnt") /totalCnt) *100;
			progress = (1 > progress && progress > 0) ? 1 : progress;
			resultMap.put("ing_progress"    , Math.round( progress * 100 ) * 0.01 );
			progress = (100D == progress ) ? 100 : progress;
		}

		return resultMap;
	}

	//로그인 회원의 업무상태 (목록포함)
	@Override
	public Map<String, Object> getMyBizState()  throws Exception {

		AppLoginInfoVo appLoginInfoVo       = getAppLoginInfoVo();
		CompanyStaffVo companyStaffVo       = new CompanyStaffVo();
		Map<String, Object> resultMap       = new HashMap<String, Object>();

		companyStaffVo.setPk_company_staff( appLoginInfoVo.getLoginCompanyStaffPk() );

		List<CodeVo> bizStateCodeList       = appMainDao.getMyBizState(companyStaffVo);

		resultMap.put("bizStateList"        , bizStateCodeList);

		return resultMap;
	}

	//로그인 회원의 업무상태 저장
	@Override
	public Map<String, Object> setMyBizState( CompanyStaffVo reqCompanyStaffVo)  throws Exception {

		int resultCnt   = appMainDao.setMyBizState( reqCompanyStaffVo );

		return getMyBizState();
	}

	@Override
	public CompanyStaffVo getMyPage()  throws Exception {

		AppLoginInfoVo appLoginInfoVo       = getAppLoginInfoVo();
		CompanyStaffVo companyStaffVo       = new CompanyStaffVo();
		Map<String, Object> resultMap       = new HashMap<String, Object>();

		companyStaffVo.setPk_company_staff( appLoginInfoVo.getLoginCompanyStaffPk() );

		companyStaffVo                      = appMainDao.getMyPage(companyStaffVo);

		return companyStaffVo;
	}

	@Override
	public Map<String, Object> myWorkflowCnt(IssueTicketVo reqIssueTicketVo)  throws Exception {


		Map<String, Long> workflowCntMap    = issueDao.getTicketWorkflowCnt(reqIssueTicketVo);
		Map<String, Object> resultMap       = new HashMap<String, Object>();

		resultMap.put("ing_cnt"             , workflowCntMap.get("ing_cnt"));
		resultMap.put("end_cnt"             , workflowCntMap.get("end_cnt"));
		resultMap.put("hold_cnt"            , workflowCntMap.get("hold_cnt"));

		return resultMap;
	}

	@Override
	public void setStaffPushNotiYn( CompanyStaffVo reqCompanyStaffVo)  throws Exception {

		int resultCnt   = appMainDao.setStaffPushNotiYn( reqCompanyStaffVo );
	}

}
