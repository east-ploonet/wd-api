package com.saltlux.aice_fe.pc.issue.service;

import com.saltlux.aice_fe.pc.issue.dto.TicketIssueCustomerDTO;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CompanyCustomerService {

    int saveCompanyCustomer(TicketIssueCustomerDTO ticketIssueCustomerDTO) throws SQLException;

    int changeStatusCompanyCustomer(CompanyCustomerVo companyCustomerVo) throws Exception;

    Map<String, Object> getDetailCompanyCustomer(CompanyCustomerVo vo)  throws Exception;

    Map<String, Object> getCompanyCustomer(CompanyCustomerVo vo)  throws Exception;

    int saveCompanyCustomer(CompanyCustomerVo companyCustomerVo) throws Exception;

    CompanyCustomerVo getById(long id);

    void deleteCustomers(List<CompanyCustomerVo> vo) throws Exception;
}
