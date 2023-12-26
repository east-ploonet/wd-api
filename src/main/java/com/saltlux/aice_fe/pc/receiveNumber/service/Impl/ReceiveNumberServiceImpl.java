package com.saltlux.aice_fe.pc.receiveNumber.service.Impl;


import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.receiveNumber.dao.ReceiveNumberDao;
import com.saltlux.aice_fe.pc.receiveNumber.service.ReceiveNumberService;
import com.saltlux.aice_fe.pc.staff.dao.AIStaffDao;
import com.saltlux.aice_fe.pc.staff.dao.StaffDao;
import com.saltlux.aice_fe.pc.staff.service.AIStaffService;
import com.saltlux.aice_fe.pc.staff.vo.AIStaffVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReceiveNumberServiceImpl extends BaseServiceImpl implements ReceiveNumberService {
	
	@Autowired
	private ReceiveNumberDao receiveNumberDao;

	//사용가능한 번호 리스트 호출
	@Override
	public Map<String, Object> getNumberStaffList(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> result          = new HashMap<>();
		List<Map<String, Object>> listMap   = new ArrayList<>();

		List<Map<String, Object>> listStaffMap = receiveNumberDao.getNumberStaffList(paramMap);
		
		
		if(listStaffMap == null){

			throwException.statusCode(204);

		} else {

			for(Map<String, Object> map : listStaffMap){

				Map<String, Object> mapAdd = new HashMap<>();

				mapAdd.put("staffPk"       , map.get("pk_company_staff"  ));
				mapAdd.put("companyFk"     , map.get("fk_company"        ));
				mapAdd.put("staffName"     , map.get("fd_staff_name"     ));
				mapAdd.put("staffAIUid"    , map.get("fd_staff_ai_uid"   ));
				mapAdd.put("staffWorkCodeName", map.get("staff_work_code_name"));
				
				mapAdd.put("aiPolicyAvatarImg", map.get("fk_ai_policy_avatar_img"));
				mapAdd.put("dispName", map.get("disp_name"));
				
				
				listMap.add(mapAdd);
			}
		}
		result.put("list", listMap);
		return result;
	}
	
	//설정 완료한번호 리스트 호출
	@Override
	public Map<String, Object> getNumberStaffListComplete(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> result          = new HashMap<>();
		List<Map<String, Object>> listMap   = new ArrayList<>();

		List<Map<String, Object>> listStaffMap = receiveNumberDao.getNumberStaffListComplete(paramMap);
		
		
		if(listStaffMap == null){

			throwException.statusCode(204);

		} else {

			for(Map<String, Object> map : listStaffMap){

				Map<String, Object> mapAdd = new HashMap<>();

				mapAdd.put("staffPk"       , map.get("pk_company_staff"  ));
				mapAdd.put("companyFk"     , map.get("fk_company"        ));
				mapAdd.put("staffName"     , map.get("fd_staff_name"     ));
				mapAdd.put("staffAIUid"    , map.get("fd_staff_ai_uid"   ));
				mapAdd.put("staffWorkCodeName", map.get("staff_work_code_name"));
				mapAdd.put("fkAiPolicyAvatarImg", map.get("fk_ai_policy_avatar_img"));
				mapAdd.put("dispName", map.get("disp_name"));
				mapAdd.put("fullDnis", map.get("full_dnis"));

				listMap.add(mapAdd);
			}
		}
		result.put("list", listMap);
		return result;
	}
	
}
