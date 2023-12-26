package com.saltlux.aice_fe.pc.join.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.service.CompanyStaffService;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.staff.dao.StaffDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/companyStaff") // end point : localhost:8080/api/v1/workStage/companyStaff
public class CompanyStaffController extends BaseController {

    private final CompanyStaffService companyStaffService;
    private final StaffDao staffDao;


    public CompanyStaffController(CompanyStaffService companyStaffService, StaffDao staffDao) {
        this.companyStaffService = companyStaffService;
        this.staffDao = staffDao;
    }

    @GetMapping("/findAll")
    public Object findAllCompanyStaff(@RequestParam(value = "deptName", defaultValue = "") String deptName) throws Exception {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();

        CompanyStaffVo companyStaffVo = new CompanyStaffVo();
        companyStaffVo.setFk_company(pcLoginInfoVo.getLoginCompanyPk());
        companyStaffVo.getSearch().setPageSize(0); // -> get all
        companyStaffVo.getSearch().setPage(0);
        companyStaffVo.getSearch().setSearchColumn(deptName);
        List<CompanyStaffVo> list = staffDao.getStaffList(companyStaffVo);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", list);
        return new ResponseVo(200, resultMap);
    }
}
