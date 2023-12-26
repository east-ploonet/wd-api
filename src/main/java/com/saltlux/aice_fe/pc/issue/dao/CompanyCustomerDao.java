package com.saltlux.aice_fe.pc.issue.dao;

import com.saltlux.aice_fe._baseline.cache.RelativeCache;
import com.saltlux.aice_fe.pc.issue.dto.IssueTicketDTO;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
//@CacheNamespace(implementation = RelativeCache.class, eviction = RelativeCache.class, flushInterval = 30 * 60 * 1000)
public interface CompanyCustomerDao {

    int insertCompanyCustomer(CompanyCustomerVo companyCustomerVo);

    int updateCompanyCustomer(CompanyCustomerVo companyCustomerVo);
    int changeStatusCompanyCustomer(CompanyCustomerVo companyCustomerVo);

    List<CompanyCustomerVo> getCompanyCustomer(CompanyCustomerVo vo);

    long countTicketIssues(CompanyCustomerVo vo);

    long countCompanyCustomer(CompanyCustomerVo vo);

    CompanyCustomerVo getCompanyCustomerDetail(CompanyCustomerVo vo);

    CompanyCustomerVo getById(Long id);

    void deleteCustomers(CompanyCustomerVo vo);
}
