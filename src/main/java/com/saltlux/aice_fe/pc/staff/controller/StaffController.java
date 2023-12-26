package com.saltlux.aice_fe.pc.staff.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.new_ojt.service.NewOjtService;
import com.saltlux.aice_fe.pc.staff.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/staff") // end point : localhost:8080/api/v1/workStage/staff
public class StaffController extends BaseController {

	@Autowired
	private StaffService staffService;

	@Autowired
	private PloonetApiService ploonetApiService;

	@Autowired
	private NewOjtService newOjtService;
	
	// 부서 목록
	@GetMapping("/dept/list")
	public Object listDept() throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();
		companyStaffVo.setFk_company( loginInfoVo.getLoginCompanyPk() );
		Map<String, Object> resultMap = staffService.selectCompanyDeptList(companyStaffVo);

		return new ResponseVo( 200, resultMap );
	}

	@GetMapping("/dept/getListByStaff")
	public Object getListByStaff(@RequestParam(value = "pkCompanyStaff", required = false, defaultValue = "0") Long pkCompanyStaff
								, @RequestParam(value = "fkStaffWorkCode", required = false, defaultValue = "0") String fkStaffWorkCode
	) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();
		companyStaffVo.setFk_company( loginInfoVo.getLoginCompanyPk() );
		companyStaffVo.setFk_staff_work_code(fkStaffWorkCode);
		Map<String, Object> resultMap = staffService.selectCompanyDeptListDefault(companyStaffVo);

		return new ResponseVo( 200, resultMap );
	}

	// 부서명 중복체크
	@GetMapping("/dept/check")
	public Object checkDept (
			@RequestParam(value="deptName", required=false) final String deptName
	) throws Exception {

		throwException.requestParamRequied( deptName );

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyDeptVo companyDeptVo = new CompanyDeptVo();
		companyDeptVo.setFk_company		( loginInfoVo.getLoginCompanyPk() );
		companyDeptVo.setFd_dept_name	( deptName						  );

		staffService.checkDeptName(companyDeptVo);

		return new ResponseVo(200);
	}

	// 부서 등록
	@PostMapping("/dept/regist")
	public Object registDept (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "deptName" );
		staffService.registDept(reqJsonObj);

		return new ResponseVo(200);

	}

	// 부서 수정
	@PutMapping("/dept/update")
	public Object updateDept (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "listData" );
		staffService.updateDept(reqJsonObj);

		return new ResponseVo(200);

	}

	// 부서 삭제
	@PutMapping("/dept/delete")
	public Object deleteDept (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "pkCompanyDept" );
		staffService.deleteDept(reqJsonObj);

		return new ResponseVo(200);

	}

	// 부서 직원 수 체크
	@GetMapping("/dept/count")
	public Object staffCountDept (
			  @RequestParam(value="fkDept", required=false) final String fkDept
	) throws Exception {

		throwException.requestParamRequied( fkDept );

		CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();
		companyDeptStaffVo.setFk_company_dept( Common.parseLong(fkDept) );

		Map<String, Object> resultMap = staffService.staffCountDept(companyDeptStaffVo);

		return new ResponseVo(200, resultMap);
	}

	// 담당자 부서 목록
	@GetMapping("/staffDept/list")
	public Object listStaffDept() throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyDeptVo companyDeptVo = new CompanyDeptVo();
		companyDeptVo.setFk_company( loginInfoVo.getLoginCompanyPk() );

		Map<String, Object> resultMap = staffService.selectStaffDeptList(companyDeptVo);

		return new ResponseVo( 200, resultMap );
	}
	
	// 담당자 목록(페이징)
	@RequestMapping(value = {"/listPaging"}, method = {RequestMethod.POST}, produces=PRODUCES_JSON)
	public Object getStaffListPaging(
			  @RequestParam(value = "page"        , required = false, defaultValue = "0"  ) int page
			, @RequestParam(value = "pageSize"    , required = false, defaultValue = "10" ) int pageSize
			, @RequestParam(value = "searchString", required = false, defaultValue = ""   ) String searchString
			, @RequestParam(value = "type"        , required = false                      ) String type
			, @RequestBody CompanyStaffVo companyStaffVo
	) throws Exception {
		
		System.out.println("------------------------------------------------------------");
		System.out.println("page : "+page);
		System.out.println("pageSize : "+pageSize);
		System.out.println("searchString : "+searchString);
		System.out.println("type : "+type);
		
		companyStaffVo.getSearch().setPage(page);
		companyStaffVo.getSearch().setPageSize(pageSize);
		companyStaffVo.getSearch().setSearchString(searchString);
		companyStaffVo.getSearch().setOrderType(type);
		companyStaffVo.setFd_staff_name(type);
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		companyStaffVo.setFk_company(loginInfoVo.getLoginCompanyPk());

		Map<String, Object> resultMap = staffService.getStaffListPaging(companyStaffVo);
		
//		newOjtService.staffDeptChange(914, 1779);
		return new ResponseVo(200, resultMap);
	}


	// 담당자 목록
	@RequestMapping(value = {"/list"}, method = {RequestMethod.POST}, produces=PRODUCES_JSON)
	public Object getStaffList(
			  @RequestParam(value = "page"        , required = false, defaultValue = "1"  ) int page
			, @RequestParam(value = "pageSize"    , required = false, defaultValue = "10" ) int pageSize
			, @RequestParam(value = "searchString", required = false, defaultValue = ""   ) String searchString
			, @RequestParam(value = "searchColumn", required = false, defaultValue = ""   ) String searchColumn
			, @RequestParam(value = "orderBy"     , required = false                      ) String orderBy
			, @RequestParam(value = "type"        , required = false                      ) String type
			, @RequestBody CompanyStaffVo companyStaffVo
	) throws Exception {
		
		System.out.println("type : "+type);
		
		companyStaffVo.getSearch().setPage(page);
		companyStaffVo.getSearch().setPageSize(pageSize);
		companyStaffVo.getSearch().setSearchString(searchString);
		companyStaffVo.getSearch().setSearchColumn(searchColumn);
		companyStaffVo.getSearch().setOrderBy( orderBy);
		companyStaffVo.getSearch().setOrderType(type);
		//
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		companyStaffVo.setFk_company(loginInfoVo.getLoginCompanyPk());

		Map<String, Object> resultMap = staffService.getStaffList(companyStaffVo);
		
//		newOjtService.staffDeptChange(914, 1779);
		return new ResponseVo(200, resultMap);
	}


	// 담당자 정보 조회
	@GetMapping("/get")
	public Object staffModify(
			@RequestParam(value="pkCompanyStaff", required=false) final String pkCompanyStaff
	) throws Exception {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
		throwException.requestParamRequied( pkCompanyStaff );

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();

		companyStaffVo.setPk_company_staff(Long.parseLong(pkCompanyStaff));

		Map<String, Object> resultMap = staffService.getStaff(companyStaffVo, pcLoginInfoVo);

		return new ResponseVo(200, resultMap);

	}


	// 담당자 등록
	@PostMapping("/regist")
	public Object registStaff(
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		//필수값 체크
//		throwException.requestBodyRequied( reqJsonObj, "pkCompanyDept");

		staffService.registStaff(reqJsonObj);

		return new ResponseVo(200);
	}
//	// 담당자 등록
//	@PostMapping("/regist")
//	public Object registStaff(
//			@RequestBody Map<String, Object> reqJsonObj
//			) throws Exception {
//		
//		//필수값 체크
//		throwException.requestBodyRequied( reqJsonObj, "pkCompanyDept");
//		
//		staffService.registStaff(reqJsonObj);
//		
//		return new ResponseVo(200);
//	}


	// 담당자 수정
	@PutMapping("/update")
	public Object updateStaff (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied(reqJsonObj, "staffPk"/* , "pkCompanyDept" */);
		staffService.updateStaff(reqJsonObj);

		return new ResponseVo(200);

	}


	// 담당자 삭제
	@PutMapping("/delete")
	public Object deleteStaff (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		for ( String key : reqJsonObj.keySet() ) {

			if ( "pkCompanyStaff".equalsIgnoreCase(key) ) {

				throwException.requestBodyRequied( reqJsonObj, "pkCompanyStaff" );
				staffService.deleteStaff(reqJsonObj);

			} else if ( "listPk".equalsIgnoreCase(key) ) {

				throwException.requestBodyRequied( reqJsonObj, "listPk");
				staffService.deleteStaffList(reqJsonObj);

			}
		}

		return new ResponseVo(200);

	}


	// 담당자 이메일 중복체크
	@GetMapping("/email/check")
	public Object checkStaffEmail (
			@RequestParam(value="staffEmail"    , required=false) final String staffEmail
          , @RequestParam(value="pkCompanyStaff", required=false) final String pkCompanyStaff
	) throws Exception {

		throwException.requestParamRequied( staffEmail );

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put( "staffEmail"      , staffEmail                      );
		paramMap.put( "pkCompanyStaff"  , Common.parseLong(pkCompanyStaff));
		paramMap.put( "fkCompany"       , getPcLoginInfoVo().getLoginCompanyPk());

		return new ResponseVo(staffService.checkStaffEmail(paramMap));
	}

	@GetMapping("/accountID/check")
	public Object dupCheckAccountID (
			@RequestParam(value="staffAccountID", required=false) final String staffAccountID
	) throws Exception {

		throwException.requestParamRequied( staffAccountID );

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put( "staffAccountID" , staffAccountID);

		return new ResponseVo(staffService.checkStaffAccountID(paramMap));
	}

	// 가입회사 로고 수정
	@PutMapping("/logo/company/update")
	public Object updateCompanyLogo (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "logoPath", "logoName" );

		PcLoginInfoVo loginInfoVo = staffService.updateCompanyLogo(reqJsonObj);

		// 로고 수정 후 세션에 로고파일 정보 업데이트
		if ( loginInfoVo != null ) {
			request.getSession().removeAttribute("pcLoginInfo");
			request.getSession().setAttribute	("pcLoginInfo", loginInfoVo );
		}

		return new ResponseVo(200);
	}

	// 나의 업무상태 수정
	@PutMapping("/status/update")
	public Object updateStaffStatus (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "statusCode" );
		staffService.updateStaffResponseStatus(reqJsonObj);

		return new ResponseVo(200);
	}

	// 소속된 회사 대표 직원 수
	@PostMapping("/companyMaster/count")
	public Object companyMasterCnt (
	) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		paramMap.put("fk_company", loginInfoVo.getLoginCompanyPk());

		Map<String, Object> rtnMsg = new HashMap<>();
		rtnMsg  = staffService.companyMasterCnt(paramMap);

		return new ResponseVo(200, rtnMsg);
	}

	// 사용자 Billing 기본 정보
	@GetMapping("/billing/info")
	public Object staffBillingInfo() throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		CompanyStaffVo companyStaffVo = new CompanyStaffVo();
		companyStaffVo.setFk_company(loginInfoVo.getLoginCompanyPk());

		
		Map<String, Object> resultMap = ploonetApiService.getMyCreditInfo(loginInfoVo);

		return new ResponseVo(200, resultMap);

	}

}
