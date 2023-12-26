package com.saltlux.aice_fe.pc.issue.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.issue.dao.CompanyCustomerDao;
import com.saltlux.aice_fe.pc.issue.dto.TicketIssueCustomerDTO;
import com.saltlux.aice_fe.pc.issue.service.CompanyCustomerService;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/company-customer")
public class CompanyCustomerController extends BaseController {

    protected final ThrowException throwException;
    private final CompanyCustomerService companyCustomerService;

    public CompanyCustomerController(ThrowException throwException, CompanyCustomerService companyCustomerService) {
        this.throwException = throwException;
        this.companyCustomerService = companyCustomerService;
    }

    @PostMapping("/list")
    public Object getTestList(@RequestParam(value = "page"      , required = false, defaultValue = "1"  ) int page
            , @RequestParam(value = "pageSize"  , required = false, defaultValue = "1000" ) int pageSize
            , @RequestParam(value = "searchString"    , required = false, defaultValue = ""   ) String searchString
            , @RequestParam(value = "searchCol"    , required = false, defaultValue = ""   ) String searchColumn
            , @RequestParam(value = "startDate"    , required = false, defaultValue = ""   ) String startDate
            , @RequestParam(value = "endDate"    , required = false, defaultValue = ""   ) String endDate
            , @RequestParam (value = "orderBy", required = false) String orderBy
            , @RequestParam(value = "type", required = false) String type
            , @RequestBody CompanyCustomerVo companyCustomerVo
    ) throws Exception {

        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();

        companyCustomerVo.setFk_company( pcLoginInfoVo.getLoginCompanyPk() );

        companyCustomerVo.getSearch().setPage(page);
        companyCustomerVo.getSearch().setPageSize(pageSize);
//        companyCustomerVo.getSearch().setSearchString(searchString);
        companyCustomerVo.getSearch().setSearchColumn(searchColumn);
        companyCustomerVo.getSearch().setStartDate(startDate);
        companyCustomerVo.getSearch().setEndDate(endDate);
        companyCustomerVo.getSearch().setOrderBy( orderBy);
        companyCustomerVo.getSearch().setOrderType(type);
        Map<String, Object> resultMap = companyCustomerService.getCompanyCustomer(companyCustomerVo);

        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/detail")
    public Object getDetail(@RequestParam(value="pkCompanyCustomer") long pkCompanyCustomer
    ) throws Exception {

        CompanyCustomerVo companyCustomerVo = new CompanyCustomerVo();
        companyCustomerVo.setPk_company_customer(pkCompanyCustomer);
        companyCustomerVo.setFk_company( getPcLoginInfoVo().getLoginCompanyPk() );
        Map<String, Object> resultMap = companyCustomerService.getDetailCompanyCustomer(companyCustomerVo);

        return new ResponseVo(200, resultMap);
    }

    @PostMapping("/deleteByIds")
    public Object deleteById(@RequestBody Map<String, Object> ids) throws Exception {

        List<String> list = (List<String>) ids.get("ids");
        List<CompanyCustomerVo> cvo = new ArrayList<>();
        for (String id : list) {
            CompanyCustomerVo companyCustomerVo = new CompanyCustomerVo();
            companyCustomerVo.setPk_company_customer(Long.valueOf(id));
            companyCustomerVo.setFd_active_state("A1103");
            companyCustomerService.changeStatusCompanyCustomer(companyCustomerVo);
            cvo.add(companyCustomerVo);
        }

        companyCustomerService.deleteCustomers(cvo);

        return new ResponseVo(200);
    }

    @PostMapping("/saveCompanyCustomer")
    public Object saveCompanyCustomer(@RequestBody Map<String, Object> companyCustomer) throws Exception {
        throwException.requestBodyRequied( companyCustomer, "companyCustomer");
        ObjectMapper mapper = new ObjectMapper();
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        
        CompanyCustomerVo companyCustomerVo = mapper.convertValue(companyCustomer.get("companyCustomer"), new TypeReference<CompanyCustomerVo>() {});
        companyCustomerVo.setFk_company(pcLoginInfoVo.getLoginCompanyPk());
//        companyCustomerVo.setFk_modifier(pcLoginInfoVo.getLoginCompanyStaffPk());
        if (companyCustomerVo.getPk_company_customer() == 0) {
            // insert
            companyCustomerVo.setFk_writer(pcLoginInfoVo.getLoginCompanyStaffPk());
            companyCustomerVo.setContact_channel_from("A1502");
            companyCustomerVo.setFd_active_state("A1101");
            companyCustomerVo.setFd_regdate(new Date());
            companyCustomerVo.setContact_date_from(new Date());
        } else {
            // update
            companyCustomerVo.setFk_modifier(pcLoginInfoVo.getLoginCompanyStaffPk());
            companyCustomerVo.setFd_moddate(new Date());
        }
        companyCustomerService.saveCompanyCustomer(companyCustomerVo);
        return new ResponseVo(200);
    }

    @PostMapping("/saveByTicket")
    public Object saveByTicket(@RequestBody Map<String, Object> companyCustomer) throws Exception {
        throwException.requestBodyRequied( companyCustomer, "companyCustomer");
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        ObjectMapper mapper = new ObjectMapper();
        TicketIssueCustomerDTO ticketIssueCustomerDTO = mapper.convertValue(companyCustomer.get("companyCustomer"), new TypeReference<TicketIssueCustomerDTO>() {});
        ticketIssueCustomerDTO.setPkStaffLogin(pcLoginInfoVo.getLoginCompanyStaffPk());
        if (ticketIssueCustomerDTO.getPkCompany() == 0) {
            ticketIssueCustomerDTO.setPkCompany(pcLoginInfoVo.getLoginCompanyPk());
        }
        companyCustomerService.saveCompanyCustomer(ticketIssueCustomerDTO);
        return new ResponseVo(200);
    }
}
