package com.saltlux.aice_fe.pc.staff.service.Impl;


import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.staff.dao.AIStaffDao;
import com.saltlux.aice_fe.pc.staff.dao.StaffDao;
import com.saltlux.aice_fe.pc.staff.service.AIStaffService;
import com.saltlux.aice_fe.pc.staff.vo.AIStaffVo;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AIStaffServiceImpl extends BaseServiceImpl implements AIStaffService {

	@Autowired
	private AIStaffDao aiStaffDao;
	
	@Value("${ploonet.api.base.billing.url}")
    public String billingUrl;

	@Value("${ploonet.api.union.api.url}")
    public String unionApi;

	@Override
	public Map<String, Object> selectAIstaffList(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> result          = new HashMap<>();
		List<Map<String, Object>> listMap   = new ArrayList<>();

		int totalCnt = aiStaffDao.selectAIstaffCount(paramMap);

		//총 게시물수
		result.put("totalCnt", totalCnt);

		if(totalCnt > 0){

			List<Map<String, Object>> listStaffMap = aiStaffDao.selectAIstaffList(paramMap);

			if(listStaffMap == null){

				throwException.statusCode(204);

			} else {

				for(Map<String, Object> map : listStaffMap){

					Map<String, Object> mapAdd = new HashMap<>();

					mapAdd.put("staffPk"       , map.get("pk_company_staff"  ));
					mapAdd.put("companyFk"     , map.get("fk_company"        ));
					mapAdd.put("staffName"     , map.get("fd_staff_name"     ));
					mapAdd.put("staffWorkPk"   , map.get("pk_staff_work"     ));
					mapAdd.put("staffWorkName" , map.get("fd_staff_work_name"));
					mapAdd.put("staffAIUid"    , map.get("fd_staff_ai_uid"   ));
					mapAdd.put("newAI"         , map.get("fd_ai_new_tag"     ));
					mapAdd.put("mainAi"         , map.get("main_ai"     ));
					mapAdd.put("staffWorkCodeName"         , map.get("staff_work_code_name"     ));
					mapAdd.put("staffPersona"         , map.get("fd_staff_persona"     ));
					mapAdd.put("dnisNum"         , map.get("dnis_num"     ));	
					mapAdd.put("dispName"         , map.get("disp_name"     ));
					mapAdd.put("aiPolicyAvatarImg"         , map.get("fk_ai_policy_avatar_img"     ));
					
					
					mapAdd.put("frontStatus"         , map.get("front_status"     ));
					mapAdd.put("fdName"         , map.get("fd_name"     ));
					mapAdd.put("fdStaffStatusCode"         , map.get("fd_staff_status_code"     ));
					
					listMap.add(mapAdd);
				}
			}
		}
		result.put("list", listMap);

		return result;
	}


	@Override
	public Map<String, Object> getAIstaff(Map<String, Object> paramMap) throws Exception {

		log.debug( "********** Transaction name : {}", TransactionSynchronizationManager.getCurrentTransactionName() );
		Map<String, Object> result = new HashMap<>();

		Map<String, Object> staffMap = aiStaffDao.getAIstaff(paramMap);

		if(staffMap == null){

			throwException.statusCode(204);

		} else {

			result.put("pkCompanyStaff"    , staffMap.get("pk_company_staff"     ));
			result.put("staffId"           , staffMap.get("fd_staff_id"          ));
			result.put("staffName"         , staffMap.get("fd_staff_name"        ));
			result.put("aiUID"             , staffMap.get("fd_staff_ai_uid"      ));
			result.put("pkStaffWork"       , staffMap.get("pk_staff_work"        ));
			result.put("staffWorkName"     , staffMap.get("fd_staff_work_name"   ));
			result.put("fkWriter"          , staffMap.get("fk_writer"            ));
			result.put("writerName"        , staffMap.get("fd_writer_name"       ));
			result.put("writerDeptName"    , staffMap.get("fd_writer_dept_name"  ));
			result.put("regDate"           , staffMap.get("fd_regdate"           ));
			result.put("fkModifier"        , staffMap.get("fk_modifier"          ));
			result.put("modifierName"      , staffMap.get("fd_modifier_name"     ));
			result.put("modifierDeptName"  , staffMap.get("fd_modifier_dept_name"));
			result.put("modDate"           , staffMap.get("fd_moddate"           ));
			result.put("fdStaffPersona"           , staffMap.get("fd_staff_persona"           ));
			result.put("fkStaffWorkCode"           , staffMap.get("fk_staff_work_code"           ));
			result.put("fdName"           , staffMap.get("fd_name"           ));
			result.put("pkCode"           , staffMap.get("pk_code"));
			result.put("dispName"         , staffMap.get("disp_name"     ));
			result.put("aiPolicyAvatarImg"         , staffMap.get("fk_ai_policy_avatar_img"     ));
			
		}

		return result;
	}

	@Override
	public Map<String, Object> getAIMainStaff(Map<String, Object> paramMap) throws Exception {

		log.debug( "********** Transaction name : {}", TransactionSynchronizationManager.getCurrentTransactionName() );
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		
		Map<String, Object> staffMap = aiStaffDao.getAIMainStaff(paramMap);
		if(staffMap == null){
			throwException.statusCode(204);
		}
		return staffMap;
	}
	
	

	@Override
	public void updateAIStaff(Map<String, Object> paramMap) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();
	
		companyStaffVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		companyStaffVo.setPk_company_staff( Common.parseLong(paramMap.get("pkCompanyStaff"	)) );
		companyStaffVo.setFd_staff_name   ( Common.NVL		(paramMap.get("staffName"), ""	)  );
		companyStaffVo.setFd_staff_ai_uid ( Common.NVL		(paramMap.get("aiUID"	 ), ""	)  );
		companyStaffVo.setFd_staff_persona( Common.NVL		(paramMap.get("fdStaffPersona"	 ), ""	)  );
		companyStaffVo.setFk_modifier     ( loginInfoVo.getLoginCompanyStaffPk()               );

		try {
			//System.out.println("companyStaffVo 값 : "+companyStaffVo);
			int result = aiStaffDao.updateAIstaff(companyStaffVo);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}

	}
	
	@Override
	public void updateAIStaffStatus(Map<String, Object> paramMap) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		
		try {
			//System.out.println("companyStaffVo 값 : "+companyStaffVo);
			int result = aiStaffDao.updateAIStaffStatus(paramMap);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}

	}
	
	

	@Override
	public void workCodeUpdate(Map<String, Object> paramMap) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();


		companyStaffVo.setPk_company_staff( Common.parseLong(paramMap.get("pkCompanyStaff"	)) );
		companyStaffVo.setFk_staff_work_code(Common.NVL(paramMap.get("fkStaffWorkCode"), ""));

		companyStaffVo.setFk_modifier     ( loginInfoVo.getLoginCompanyStaffPk()               );
		companyStaffVo.setFk_company(loginInfoVo.getLoginCompanyPk());


		try {
			int result = aiStaffDao.workCodeUpdate(companyStaffVo);


			if ( result <= 0 ) {
				throwException.statusCode(500);
			}


			// 메인 AI직원 업데이트
			Map<String, Object> mainParamMap = new HashMap<>();
			mainParamMap.put("fd_default_ai", "N");
			mainParamMap.put("fk_company", loginInfoVo.getLoginCompanyPk());

			//회사에 있는 직무별 AI 모두 메인여부 N 업데이트
			int mainUpdate = aiStaffDao.allMainAiUpdate(mainParamMap);

			//회사 각 직무별 등록순 메인 AI 업데이트
			int newMainUpdate = aiStaffDao.newMainUpdate(mainParamMap);

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}

	}


	@Override
	public Map<String, Object> getQuickStart(String uuid) throws Exception {
		// TODO Auto-generated method stub
		
		// api 날려서 quickStart 인지 받아옴
		
		// quick
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", uuid);
        
        String quickUrl = "/authentication/issue";
        
        JSONParser jsonParser = new JSONParser();
    	String apiResult = Common.getRestDataUnionApiPost(unionApi + quickUrl, jsonObject, billingUrl);
    	JSONObject quickObj = (JSONObject) jsonParser.parse(apiResult);
    	String quickStartStatus = (String) quickObj.get("quickStartStatus");
    	
    	// quick
    	
    	Map<String, Object> results = new HashMap<>();
    	results.put("quickStartStatus", quickStartStatus);
		
		return results;
	}


}
