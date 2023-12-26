package com.saltlux.aice_fe.pc.staff.service.Impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.service.PcAuthService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptTagVo;
import com.saltlux.aice_fe.pc.staff.dao.DeptDao;
import com.saltlux.aice_fe.pc.staff.dao.StaffDao;
import com.saltlux.aice_fe.pc.staff.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

@Slf4j
@Service
public class StaffServiceImpl extends BaseServiceImpl implements StaffService {

	@Autowired
	private StaffDao staffDao;
	//
	@Autowired
	PcAuthService pcAuthService;

	@Autowired
	PloonetApiService ploonetApiService;

	@Autowired
	private DeptDao deptDao;

	@Override
	public Map<String, Object> selectCompanyDeptList(CompanyStaffVo paramVo) throws Exception {

		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> listMap = new ArrayList<>();

		List<CompanyDeptVo> listData = staffDao.selectCompanyDeptList(paramVo);

		if (listData == null || listData.isEmpty()) {

			throwException.statusCode(204);

		} else {

			for (CompanyDeptVo vo : listData) {

				Map<String, Object> obj = new HashMap<>();
				//
				obj.put( "pkCompanyDept", vo.getPk_company_dept());
				obj.put( "fkCompany", vo.getFk_company());
				obj.put( "deptName", vo.getFd_dept_name());
				obj.put( "deptCode", vo.getFd_dept_code());
				obj.put( "deptRole", vo.getFd_dept_role());
				obj.put( "useYn", vo.getFd_use_yn());
				obj.put( "defaultYn", vo.getFd_default_yn());
				obj.put( "masterStaffName", vo.getMaster_staff_name());
				obj.put( "deptAiYn", vo.getFd_dept_ai_yn());

				listMap.add(obj);

			}

			result.put("listData", listMap);

		}

		return result;

	}

	@Override
	public Map<String, Object> selectCompanyDeptListDefault(CompanyStaffVo paramVo) throws Exception {
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> listMap = new ArrayList<>();

		List<CompanyDeptVo> listData = deptDao.getDefaultDeptMaster(paramVo);

		if (listData == null || listData.isEmpty()) {

			throwException.statusCode(204);

		} else {

			for (CompanyDeptVo vo : listData) {

				Map<String, Object> obj = new HashMap<>();
				//
				obj.put("pkCompanyDept", vo.getPk_company_dept());
				obj.put("fkCompany", vo.getFk_company());
				obj.put("deptName", vo.getFd_dept_name());
				obj.put("deptCode", vo.getFd_dept_code());
				obj.put("deptRole", vo.getFd_dept_role());
				obj.put("useYn", vo.getFd_use_yn());
				obj.put("defaultYn", vo.getFd_default_yn());
				obj.put("masterStaffName", vo.getMaster_staff_name());
				obj.put("deptAiYn", vo.getFd_dept_ai_yn());

				listMap.add(obj);

			}

			result.put("listData", listMap);

		}

		return result;
	}

	@Override
	public void checkDeptName(CompanyDeptVo paramVo) throws Exception {

		CompanyDeptVo CompanyDeptVo = staffDao.checkDeptName(paramVo);
		//
		if (CompanyDeptVo != null && !CompanyDeptVo.getFd_dept_name().isEmpty()) {

			throwException.statusCode(409);

		}

	}

	@Override
	public void registDept(Map<String, Object> paramMap) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyDeptVo companyDeptVo = new CompanyDeptVo();
		companyDeptVo.setFd_dept_name(Common.NVL(paramMap.get("deptName"), ""));
		companyDeptVo.setFd_dept_code(Common.NVL(paramMap.get("deptCode"), ""));
		companyDeptVo.setFd_dept_role(Common.NVL(paramMap.get("deptRole"), ""));
		companyDeptVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		companyDeptVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());

		CompanyDeptTagVo companyDeptTagVo = new CompanyDeptTagVo();

		companyDeptTagVo.setFd_dept_tag(Common.NVL(paramMap.get("deptName"), ""));
		companyDeptTagVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		companyDeptTagVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());

		try {
			long result = staffDao.insertCompanyDept(companyDeptVo);
			companyDeptTagVo.setFk_company_dept(companyDeptVo.getPk_company_dept());
			long resultTag = staffDao.insertCompanyDeptTag(companyDeptTagVo);
		} catch (Exception ex) {
			log.error("********** paramMap : {}", ex.getMessage());
			throwException.statusCode(500);
		}
	}

	@Override
	public void updateDept(Map<String, Object> paramMap) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> listData = objectMapper.convertValue(paramMap.get("listData"), List.class);
		//
		if (listData != null && listData.size() > 0) {

			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

			for (Map<String, Object> obj : listData) {
				int result = 0;
				CompanyDeptVo companyDeptVo = new CompanyDeptVo();
				companyDeptVo.setFd_dept_code(Common.NVL(obj.get("deptCode"), ""));
				companyDeptVo.setFd_dept_role(Common.NVL(obj.get("deptRole"), ""));
				companyDeptVo.setFd_use_yn(Common.NVL(obj.get("useYn"), "Y"));
				companyDeptVo.setFd_dept_name(Common.NVL(obj.get("deptName"), ""));
				companyDeptVo.setFk_modifier(loginInfoVo.getLoginCompanyStaffPk());
				if (null != obj.get("pkCompanyDept")) {
					companyDeptVo.setPk_company_dept(Common.parseLong(obj.get("pkCompanyDept")));
					result = staffDao.updateCompanyDept(companyDeptVo);
				} else {
					companyDeptVo.setFk_company(loginInfoVo.getLoginCompanyPk());
					companyDeptVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
					result = staffDao.insertCompanyDept(companyDeptVo);
					// create new relation
					if (companyDeptVo.getPk_company_dept() != 0 && null != obj.get("pkCompanyStaff")) {
						CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();
						companyDeptStaffVo.setFk_company_staff(Common.parseLong(obj.get("pkCompanyStaff")));
						companyDeptStaffVo.setFk_company_dept(companyDeptVo.getPk_company_dept());
						companyDeptStaffVo.setFd_dept_master_yn("Y");
						companyDeptStaffVo.setFd_regdate(new Date());
						result = deptDao.insertCompanyDeptStaff(companyDeptStaffVo);
					}
				}

				try {
					//
					if (result <= 0) {
						throwException.statusCode(500);
					}

				} catch (Exception ex) {
					log.error("********** paramMap : {}", paramMap.toString());
					throwException.statusCode(500);
				}
			}
		}
	}

	@Override
	public void deleteDept(Map<String, Object> paramMap) throws Exception {

		CompanyDeptVo companyDeptVo = new CompanyDeptVo();
		//
		companyDeptVo.setPk_company_dept(Common.parseLong(paramMap.get("pkCompanyDept")));

		try {

			int result = staffDao.deleteCompanyDept(companyDeptVo);
			//
			if (result <= 0) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}

	}

	@Override
	public Map<String, Object> staffCountDept(CompanyDeptStaffVo paramVo) throws Exception {

		Map<String, Object> result = new HashMap<>();

		int staffCount = staffDao.staffCountDept(paramVo);
		result.put("staffCount", staffCount);

		return result;

	}

	@Override
	public Map<String, Object> selectStaffDeptList(CompanyDeptVo paramVo) throws Exception {

		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> listMap = new ArrayList<>();

		List<CompanyDeptVo> listData = staffDao.selectStaffDeptList(paramVo);

		if (listData == null || listData.isEmpty()) {

			throwException.statusCode(204);

		} else {

			for (CompanyDeptVo vo : listData) {

				Map<String, Object> obj = new HashMap<>();
				//
				obj.put("pkCompanyDept", vo.getPk_company_dept());
				obj.put("fkCompany", vo.getFk_company());
				obj.put("deptName", vo.getFd_dept_name());
				obj.put("depCode", vo.getFd_dept_code());
				obj.put("deptRole", vo.getFd_dept_role());
				obj.put("useYn", vo.getFd_use_yn());
				obj.put("defaultYn", vo.getFd_default_yn());
				obj.put("deptAiYn", vo.getFd_dept_ai_yn());

				listMap.add(obj);

			}

			result.put("listData", listMap);

		}

		return result;

	}
	@Override
	public Map<String, Object> getStaffListPaging(CompanyStaffVo reqStaffVo) throws Exception {

		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> listMap = new ArrayList<>();
		List<Map<String, Object>> deptList = new ArrayList<>();
		
		Map<String, Object> deptAdd = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		deptAdd.put("fk_company", loginInfoVo.getLoginCompanyPk());
		List<Object> listData = staffDao.getDeptDispName(deptAdd);
		
		int totalCnt = staffDao.getStaffAllCnt(reqStaffVo);
		//int totalListCnt = staffDao.getStaffAllListCnt(reqStaffVo);
		//총 게시물수
		result.put("totalCnt", totalCnt);
		//result.put("totalCnt", totalListCnt);

		if (totalCnt > 0) {
			//부서리스트
			List<CompanyStaffVo> listStaffVo = staffDao.getStaffListPaging(reqStaffVo);
			//List<CompanyStaffVo> listStaffVoList = staffDao.getStaffList(reqStaffVo);
			//부서리스트 총 게시물수
			int totalListCnt = staffDao.getStaffAllListCnt(reqStaffVo);
			
			result.put("totalListCnt", totalListCnt);
			System.out.println("총 개수 : "+totalListCnt);
			
			if (listStaffVo == null) {

				throwException.statusCode(204);

			} else {

				for (CompanyStaffVo companyStaffVo : listStaffVo) {

					Map<String, Object> mapAdd = new HashMap<>();

					mapAdd.put("staffPk", Long.toString(companyStaffVo.getPk_company_staff()));
					mapAdd.put("masterYn", companyStaffVo.getFd_company_master_yn());
					mapAdd.put("staffId", companyStaffVo.getFd_staff_id());
					mapAdd.put("staffName", companyStaffVo.getFd_staff_name());
					mapAdd.put("staffDuty", companyStaffVo.getFd_staff_duty());
					mapAdd.put("staffDept", companyStaffVo.getFd_dept_name());
					mapAdd.put("deptDispName", companyStaffVo.getDept_disp_name());
					mapAdd.put("staffStatusCode", companyStaffVo.getFd_staff_status_code());
					mapAdd.put("staffStatus", companyStaffVo.getFd_staff_status());
					mapAdd.put("staffLevel", companyStaffVo.getFd_staff_level());
					mapAdd.put("staffPhone", companyStaffVo.getFd_staff_phone());
					mapAdd.put("staffMobile", companyStaffVo.getFd_staff_mobile());
					mapAdd.put("staffEmail", companyStaffVo.getFd_staff_email());
					mapAdd.put("staffResponseStatus", companyStaffVo.getFd_staff_response_status());
					mapAdd.put("writerName", companyStaffVo.getFd_writer_name());
					mapAdd.put("fdSignupKeycode", companyStaffVo.getFd_signup_keycode());
					mapAdd.put("regDate",
							FormatUtils.dateToStringCustomize(companyStaffVo.getFd_regdate(), "yyyy.MM.dd"));
					mapAdd.put("modDate",
							FormatUtils.dateToStringCustomize(companyStaffVo.getFd_moddate(), "yyyy.MM.dd"));

					listMap.add(mapAdd);
				}
				
			}
		}
		//result.put("list", listMap);
		result.put("list", listMap);
		result.put("dept", listData);
		return result;
	}

	@Override
	public Map<String, Object> getStaffList(CompanyStaffVo reqStaffVo) throws Exception {

		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> listMap = new ArrayList<>();

		int totalCnt = staffDao.getStaffAllCnt(reqStaffVo);

		//총 게시물수
		result.put("totalCnt", totalCnt);

		if (totalCnt > 0) {

			List<CompanyStaffVo> listStaffVo = staffDao.getStaffList(reqStaffVo);

			if (listStaffVo == null) {

				throwException.statusCode(204);

			} else {

				for (CompanyStaffVo companyStaffVo : listStaffVo) {

					Map<String, Object> mapAdd = new HashMap<>();

					mapAdd.put("staffPk", Long.toString(companyStaffVo.getPk_company_staff()));
					mapAdd.put("masterYn", companyStaffVo.getFd_company_master_yn());
					mapAdd.put("staffId", companyStaffVo.getFd_staff_id());
					mapAdd.put("staffName", companyStaffVo.getFd_staff_name());
					mapAdd.put("staffDuty", companyStaffVo.getFd_staff_duty());
					mapAdd.put("staffDept", companyStaffVo.getFd_dept_name());
					mapAdd.put("deptDispName", companyStaffVo.getDept_disp_name());
					mapAdd.put("staffStatusCode", companyStaffVo.getFd_staff_status_code());
					mapAdd.put("staffStatus", companyStaffVo.getFd_staff_status());
					mapAdd.put("staffLevel", companyStaffVo.getFd_staff_level());
					mapAdd.put("staffPhone", companyStaffVo.getFd_staff_phone());
					mapAdd.put("staffMobile", companyStaffVo.getFd_staff_mobile());
					mapAdd.put("staffEmail", companyStaffVo.getFd_staff_email());
					mapAdd.put("staffResponseStatus", companyStaffVo.getFd_staff_response_status());
					mapAdd.put("writerName", companyStaffVo.getFd_writer_name());
					mapAdd.put("fdSignupKeycode", companyStaffVo.getFd_signup_keycode());
					mapAdd.put("regDate",
							FormatUtils.dateToStringCustomize(companyStaffVo.getFd_regdate(), "yyyy.MM.dd"));
					mapAdd.put("modDate",
							FormatUtils.dateToStringCustomize(companyStaffVo.getFd_moddate(), "yyyy.MM.dd"));

					listMap.add(mapAdd);
				}
			}
		}
		result.put("list", listMap);

		return result;
	}

	@Override
	public Map<String, Object> getStaff(CompanyStaffVo reqStaffVo, PcLoginInfoVo pcLoginInfoVo) throws Exception {

		log.debug("********** Transaction name : {}", TransactionSynchronizationManager.getCurrentTransactionName());
		Map<String, Object> result = new HashMap<>();

		CompanyStaffVo companyStaffVo = staffDao.getStaff(reqStaffVo);

		if (companyStaffVo == null) {

			throwException.statusCode(204);

		} else {

			result.put("staffPk", Long.toString(companyStaffVo.getPk_company_staff()));
			result.put("fkCompany", Long.toString(companyStaffVo.getFk_company()));
			result.put("staffId", companyStaffVo.getFd_staff_id());
			result.put("staffName", companyStaffVo.getFd_staff_name());
			result.put("staffDuty", companyStaffVo.getFd_staff_duty());
			result.put("staffDi", companyStaffVo.getFd_staff_di());
			result.put("staffCi", companyStaffVo.getFd_staff_ci());
			result.put("pkCompanyDept", Long.toString(companyStaffVo.getPk_company_dept()));
			result.put("deptName", companyStaffVo.getFd_dept_name());
			result.put("deptMasterYn", companyStaffVo.getFd_dept_master_yn());
			result.put("companyMasterYn", companyStaffVo.getFd_company_master_yn());
			result.put("statusCode", companyStaffVo.getFd_staff_status_code());
			result.put("staffStatus", companyStaffVo.getFd_staff_status_code());
			result.put("levelCode", companyStaffVo.getFd_staff_level_code());
			result.put("staffPhone", companyStaffVo.getFd_staff_phone());
			result.put("staffMobile", companyStaffVo.getFd_staff_mobile());
			result.put("staffEmail", companyStaffVo.getFd_staff_email());
			result.put("responseStatusCode", companyStaffVo.getFd_staff_response_status_code());
			result.put("signupKeycode", companyStaffVo.getFd_signup_keycode());
			result.put("signupKeycodeDate",
					FormatUtils.dateToStringCustomize(companyStaffVo.getFd_signup_keycode_date(), "yyyy.MM.dd"));
			result.put("aiYn", companyStaffVo.getFd_staff_ai_yn());
			result.put("fkWriter", Long.toString(companyStaffVo.getFk_writer()));
			result.put("writerName", companyStaffVo.getFd_writer_name());
			result.put("writerDeptName", companyStaffVo.getFd_writer_dept_name());
			result.put("regDate", FormatUtils.dateToStringCustomize(companyStaffVo.getFd_regdate(), "yyyy.MM.dd"));
			result.put("fkModifier", Long.toString(companyStaffVo.getFk_modifier()));
			result.put("modifierName", companyStaffVo.getFd_modifier_name());
			result.put("modifierDeptName", companyStaffVo.getFd_modifier_dept_name());
			result.put("modDate", FormatUtils.dateToStringCustomize(companyStaffVo.getFd_moddate(), "yyyy.MM.dd"));
			result.put("staffPw", companyStaffVo.getFd_staff_pw());

		}

		return result;
	}

	@Override
	public void registStaff(Map<String, Object> paramMap) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		try {
			System.out.println("paramMap:" + paramMap);
			CompanyStaffVo companyStaffVo = new CompanyStaffVo();
			//
			companyStaffVo.setFd_company_master_yn((Common.NVL(paramMap.get("companyMasterYn"), "N")));
			companyStaffVo.setFd_staff_name((Common.NVL(paramMap.get("staffName"), "")));
			companyStaffVo.setDept_disp_name(Common.NVL(paramMap.get("deptDispName"), ""));
			companyStaffVo.setFd_staff_duty((Common.NVL(paramMap.get("staffDuty"), "")));
			companyStaffVo.setFd_staff_status_code("A1101");
			companyStaffVo.setFd_default_ai("N");
			companyStaffVo.setFd_staff_mobile((Common.NVL(paramMap.get("staffMobile"), "")));
			companyStaffVo.setFd_staff_email((Common.NVL(paramMap.get("staffEmail"), "")));
			companyStaffVo.setFd_staff_response_status_code((Common.NVL(paramMap.get("fdStaffResponseStatusCode"), "A1201")));
			companyStaffVo.setFd_staff_id((Common.NVL(paramMap.get("staffAccountID"), "")));
			companyStaffVo.setFd_staff_level_code((Common.NVL(paramMap.get("levelCode"), "A1003")));
			companyStaffVo.setFd_sign_up_path_code("A3013");
			companyStaffVo.setFd_staff_phone((Common.NVL(paramMap.get("staffPhone"), "")));
			companyStaffVo.setSolution_type((Common.NVL(paramMap.get("solution_type"), "B11")));
			companyStaffVo.setUser_type(loginInfoVo.getLoginUserType());
////			companyStaffVo.setPk_company_dept(Common.parseLong(paramMap.get("pkCompanyDept")));
//			companyStaffVo.setFd_dept_master_yn((Common.NVL(paramMap.get("deptMasterYn"), "N")));
			System.out.println("!!!!!!!@@@@@@@staffPw!!#!@!@#!:" + paramMap.get("staffPw"));
			
			companyStaffVo.setFd_staff_pw(BCRYPT_ENCODER.encode(Common.NVL(paramMap.get("staffPw"), "")));
			companyStaffVo.setFd_staff_persona((Common.NVL(paramMap.get("fdStaffPersona"), "")));
			CompanyStaffVo companyStaffVo1 = registerStaffInCompany(companyStaffVo);
//			CompanyStaffVo companyStaffVo2 = registerDeptStaff(companyStaffVo1);
			System.out.println("companyStaff : " +  companyStaffVo1);
			updateRegisterStaff(companyStaffVo1);	
		} catch (Exception ex) {
			log.error("담당자관리 에러!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + ex);
			System.out.println("담당자관리 에러!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + ex);
		}
		
		
	}

	@Override
	public CompanyStaffVo registerStaffInCompany(CompanyStaffVo companyStaffVo) {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		System.out.println("companyStaffVo:" + companyStaffVo);
		companyStaffVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		companyStaffVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());

		try {
//			if ("Y".equals(companyStaffVo.getFd_company_master_yn())) {
//
//				staffDao.updateCompanyMasterN(companyStaffVo);
//				companyStaffVo.setFd_company_master_yn("N");
//			}

			staffDao.insertCompanyStaff(companyStaffVo);

		} catch (Exception ex) {
			ex.fillInStackTrace();
		}
		return companyStaffVo;
	}

	@Override
	public CompanyStaffVo registerDeptStaff(CompanyStaffVo companyStaffVo) {
		try {
			CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();

			companyDeptStaffVo.setFk_company_dept(companyStaffVo.getPk_company_dept());
			companyDeptStaffVo.setFd_dept_master_yn(companyStaffVo.getFd_dept_master_yn());

			if ("Y".equals(companyDeptStaffVo.getFd_dept_master_yn())) {

				deptDao.updateDeptStaffDeptMasterN(companyDeptStaffVo);
				companyDeptStaffVo.setFd_dept_master_yn("N");
			}

			companyDeptStaffVo.setFk_company_staff(companyStaffVo.getPk_company_staff());

			deptDao.insertCompanyDeptStaff(companyDeptStaffVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return companyStaffVo;
	}

	@Override
	public int updateRegisterStaff(CompanyStaffVo companyStaffVo) {
		try {
			// 초대 코드 발급
			//			CompanyStaffVo reqCompanyStaffVo = new CompanyStaffVo();
			//			long temp = companyStaffVo.getPk_company_staff();
			//			reqCompanyStaffVo.setPk_company_staff(companyStaffVo.getPk_company_staff());
			//			long temp1 = reqCompanyStaffVo.getPk_company_staff();
			//			pcAuthService.resetCompanyStaffTicketCode(companyStaffVo);
			System.out.println("companyStaffVo : " + companyStaffVo);
			pcAuthService.resetCompanyStaffTicketCode(companyStaffVo);
			return 1;
		} catch (Exception e) {
			System.out.println("에러2번쨰!!!!!!!! e: " + e);
			return 0;
		}
	}

	@Override
	public void updateStaff(Map<String, Object> paramMap) throws Exception {

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();
		//
		companyStaffVo.setPk_company_staff(Common.parseLong(paramMap.get("staffPk")));
//		companyStaffVo.setFd_company_master_yn((Common.NVL(paramMap.get("companyMasterYn"), "N")));

		String tempPw = Common.NVL(paramMap.get("staffPw"), "");
		if (tempPw != "") {
			companyStaffVo.setFd_staff_pw(BCRYPT_ENCODER.encode(tempPw));
		}

		companyStaffVo.setDept_disp_name((Common.NVL(paramMap.get("deptDispName"), "")));
		companyStaffVo.setFd_signup_keycode((Common.NVL(paramMap.get("fdSignupKeycode"), "")));
		companyStaffVo.setFd_staff_id((Common.NVL(paramMap.get("staffAccountID"), "")));
		companyStaffVo.setFd_staff_duty((Common.NVL(paramMap.get("staffDuty"), "")));
//		companyStaffVo.setFd_staff_id((Common.NVL(paramMap.get("staffEmail"), "")));
		companyStaffVo.setFd_staff_status_code((Common.NVL(paramMap.get("statusCode"), "A1101")));
//		companyStaffVo.setFd_staff_level_code((Common.NVL(paramMap.get("levelCode"), "A1003")));
		companyStaffVo.setFd_staff_phone((Common.NVL(paramMap.get("staffPhone"), "")));
		companyStaffVo.setFd_staff_email((Common.NVL(paramMap.get("staffEmail"), "")));
		companyStaffVo.setFd_staff_mobile((Common.NVL(paramMap.get("staffMobile"), "")));
		companyStaffVo.setFd_staff_name((Common.NVL(paramMap.get("staffName"), "")));
		companyStaffVo.setSolution_type((Common.NVL(paramMap.get("solution_type"), "B11")));
		companyStaffVo.setUser_type((Common.NVL(paramMap.get("user_type"), "B2001")));
//		companyStaffVo.setFd_staff_response_status_code((Common.NVL(paramMap.get("responseStatusCode"), "A1201"))); // 수정은 안받으면 될듯?
//		companyStaffVo.setFd_dept_master_yn((Common.NVL(paramMap.get("deptMasterYn"), "N")));
		/*companyStaffVo.setFd_signup_keycode             ( (Common.NVL(paramMap.get("signupKeycode"      ), "")) );*/

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		//
		companyStaffVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		companyStaffVo.setFk_modifier(loginInfoVo.getLoginCompanyStaffPk());

		CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();
		//
		Long pkDeptStaffOld = Common.parseLong(paramMap.get("pkDeptStaffOld"));
		Long pkDeptStaffNew = Common.parseLong(paramMap.get("pkCompanyDept"));
		companyDeptStaffVo.setFk_company_dept(pkDeptStaffNew);
		companyDeptStaffVo.setFk_company_staff(Common.parseLong(paramMap.get("staffPk")));
		companyDeptStaffVo.setFd_dept_master_yn((Common.NVL(paramMap.get("deptMasterYn"), "N")));

		try {

//			if ("Y".equals(companyStaffVo.getFd_company_master_yn())) {
//
//				staffDao.updateCompanyMasterN(companyStaffVo);
//			}
//
			int result = staffDao.updateStaff(companyStaffVo);

//			if (1 > 0) {
//
//				try {
//					if (pkDeptStaffOld != pkDeptStaffNew) {
//						CompanyDeptStaffVo companyDeptStaffVoOld = new CompanyDeptStaffVo();
//						BeanUtils.copyProperties(companyDeptStaffVo, companyDeptStaffVoOld);
//						companyStaffVo.setPk_company_dept(pkDeptStaffNew);
//						companyDeptStaffVoOld.setFk_company_dept(pkDeptStaffOld);
//						deptDao.deleteStaffDept(companyDeptStaffVoOld);
//						registerDeptStaff(companyStaffVo);
//					} else {
//						if ("Y".equals(companyDeptStaffVo.getFd_dept_master_yn())) {
//							deptDao.updateDeptStaffDeptMasterN(companyDeptStaffVo);
//						}
//					}
//					deptDao.updateDeptStaff(companyDeptStaffVo);
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			} else {
//
//				throwException.statusCode(500);
//
//			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", ex.getMessage());
			throwException.statusCode(500);
		}

	}

	@Override
	public void deleteStaff(Map<String, Object> paramMap) throws Exception {

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();
		//
		companyStaffVo.setPk_company_staff(Common.parseLong(paramMap.get("staffPk")));

		CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();
		//
		companyDeptStaffVo.setFk_company_staff(Common.parseLong(paramMap.get("staffPk")));

		try {

			int result = staffDao.deleteStaff(companyStaffVo);
			//
			if (result > 0) {

				staffDao.deleteDeptStaff(companyDeptStaffVo);

			} else {

				throwException.statusCode(500);

			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}

	}

	@Override
	public void deleteStaffList(Map<String, Object> paramMap) throws Exception {

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();
		//
		companyStaffVo.setPk_list((List<Long>) paramMap.get("listPk"));

		CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();
		//
		companyDeptStaffVo.setPk_list((List<Long>) paramMap.get("listPk"));

		List<Map<String, Object>> listPk = (List<Map<String, Object>>) paramMap.get("listPk");

		for (int i = 0; i < listPk.size(); i++) {

			paramMap.put("pk_company_staff", listPk.get(i));


			if(staffDao.checkCompanyMaster(paramMap) != null ) {
				CompanyStaffVo companyMasterVo = staffDao.checkCompanyMaster(paramMap);
				if ("Y".equals(companyMasterVo.getFd_company_master_yn())) {
					
					throwException.statusCode(409);
					
				}
				
			}

		}

		try {

			int result = staffDao.deleteStaffList(companyStaffVo);
			//
			if (result > 0) {

				staffDao.deleteDeptStaffList(companyDeptStaffVo);

			} else {

				throwException.statusCode(500);

			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}

	@Override
	public int checkStaffEmail(Map<String, Object> paramMap) throws Exception {

		CompanyStaffVo companyStaffVo = staffDao.checkStaffEmail(paramMap);
		//
		if (companyStaffVo != null && !companyStaffVo.getFd_staff_email().isEmpty()) {

			return 409;

		}
		return 200;
	}

	@Override
	public int checkStaffAccountID(Map<String, Object> paramMap) throws Exception {

		CompanyStaffVo companyStaffVo = staffDao.checkStaffAccountID(paramMap);
		//
		if (companyStaffVo != null && !companyStaffVo.getFd_staff_id().isEmpty()) {

			return 409;

		}
		return 200;
	}

	@Override
	public PcLoginInfoVo updateCompanyLogo(Map<String, Object> paramMap) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyVo companyVo = new CompanyVo();
		companyVo.setFd_company_logo_file_path(Common.NVL(paramMap.get("logoPath"), ""));
		companyVo.setFd_company_logo_file_name(Common.NVL(paramMap.get("logoName"), ""));
		companyVo.setPk_company(loginInfoVo.getLoginCompanyPk());
		companyVo.setFk_modifier(loginInfoVo.getLoginCompanyStaffPk());

		try {
			int result = staffDao.updateCompanyLogo(companyVo);
			//
			if (result <= 0) {
				throwException.statusCode(500);

			} else {
				loginInfoVo.setLoginCompanyLogoUrl(pathBrowserStorage + companyVo.getFd_company_logo_file_path());
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}

		return loginInfoVo;

	}

	@Override
	public void updateStaffResponseStatus(Map<String, Object> paramMap) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();
		companyStaffVo.setFd_staff_response_status_code	( Common.NVL(paramMap.get("statusCode"), "") );
		companyStaffVo.setPk_company_staff				( loginInfoVo.getLoginCompanyStaffPk() );
		companyStaffVo.setFk_modifier					( loginInfoVo.getLoginCompanyStaffPk() );
		try {
			int result = staffDao.updateStaffResponseStatus(companyStaffVo);
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
	public Map<String, Object> companyMasterCnt(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> resultMap  = new HashMap<>();
		int result = staffDao.companyMasterCnt(paramMap);
		if ( result > 0 ) {
			resultMap.put("masterYn", "Y");
		} else {
			resultMap.put("masterYn", "N");
		}
		return resultMap;
	}
	
	@Override
	public Map<String, Object> masterStaffUuid(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> resultMap = staffDao.masterStaffUuid(paramMap);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> ploonianModify(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> resultMap = staffDao.ploonianModify(paramMap);
		return resultMap;
	}
	
}
