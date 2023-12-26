package com.saltlux.aice_fe.commonCode.service.impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.commonCode.dao.CodeDao;
import com.saltlux.aice_fe.commonCode.service.CodeService;
import com.saltlux.aice_fe.commonCode.vo.CodeVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CodeServiceImpl extends BaseServiceImpl implements CodeService {
	
	@Autowired
	private CodeDao codeDao;

	@Override
	public Map<String, Object> selectCodeList(CodeVo codeVo) throws Exception {

		Map<String, Object> result          = new HashMap<>();
		List<Map<String, Object>> listMap   = new ArrayList<>();

		List<CodeVo> listCode = codeDao.selectCodeList( codeVo );

		if ( listCode == null || listCode.isEmpty() ) {

			throwException.statusCode(204);

		} else {

			for ( CodeVo vo : listCode ) {

				Map<String, Object> obj = new HashMap<>();
				//
				obj.put( "pkCode"	, vo.getPk_code() 		);
				obj.put( "fkUpCode"	, vo.getFk_up_code() 	);
				obj.put( "name"		, vo.getFd_name() 		);
				obj.put( "useYn"	, vo.getFd_use_yn() 	);
				obj.put( "memo"		, vo.getFd_memo() 		);
				obj.put( "nameEn"	, vo.getFd_name_en());

				listMap.add(obj);

			}

			result.put( "listCode", listMap );

		}

		return result;

	}
	
	@Override
	public Map<String, Object> selectStaffWorkCodeList(Map<String, Object> reqJsonObj) throws Exception {
		Map<String, Object> result          = new HashMap<>();
		List<Map<String, Object>> listMap   = new ArrayList<>();

		List<Object> list = codeDao.staffWorkCodeList( reqJsonObj );
		

		if ( list == null || list.isEmpty() ) {

			throwException.statusCode(204);

		} else {
			result.put("listCode", list);
		}

		return result;

	}
	
	@Override
	public Map<String, Object> selectUseStaffWorkCodeList(Map<String, Object> reqJsonObj) throws Exception {

		Map<String, Object> result          = new HashMap<>();

		List<Object> list = codeDao.staffWorkCodeList( reqJsonObj );
		
		result.put("list", list);

		return result;

	}
	
	
	
	@Override
	public Map<String, Object> staffWorkCtgrCodeList(String fkStaffWorkCode, long fkCompany) throws Exception {
		
		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		reqJsonObj.put("fkStaffWorkCode", fkStaffWorkCode);
		
		reqJsonObj.put("fkCompany", fkCompany);
		
		Map<String, Object> result          = new HashMap<>();

		List<Object> list =  codeDao.staffWorkCtgrCodeList(reqJsonObj);
		result.put("list", list);

		return result;

	}
	
	
	
}
