package com.saltlux.aice_fe.pc.issue.service.Impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe.pc.issue.dao.CompanyCustomerDao;
import com.saltlux.aice_fe.pc.issue.dao.IssueContactDao;
import com.saltlux.aice_fe.pc.issue.dto.TicketIssueCustomerDTO;
import com.saltlux.aice_fe.pc.issue.service.CompanyCustomerService;
import com.saltlux.aice_fe.pc.issue.util.Util;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueContactVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import com.saltlux.aice_fe.pc.join.dao.JoinDao;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyVo;
import com.saltlux.aice_fe.pc.join.dao.CompanyDeptDao;
import com.saltlux.aice_fe.pc.staff.dao.StaffDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
public class CompanyCustomerServiceImpl extends BaseServiceImpl implements CompanyCustomerService {

    private final CompanyCustomerDao companyCustomerDao;
    private final JoinDao joinDao;
    private final StaffDao staffDao;
    private final CompanyDeptDao companyDeptDao;
    private final IssueContactDao issueContactDao;

    public CompanyCustomerServiceImpl(CompanyCustomerDao companyCustomerDao, JoinDao joinDao, StaffDao staffDao,
                                      CompanyDeptDao companyDeptDao, IssueContactDao issueContactDao) {
        this.companyCustomerDao = companyCustomerDao;
        this.joinDao = joinDao;
        this.staffDao = staffDao;
        this.companyDeptDao = companyDeptDao;
        this.issueContactDao = issueContactDao;
    }

    @Override
    public int saveCompanyCustomer(TicketIssueCustomerDTO ticketIssueCustomerDTO) throws SQLException {
        // update issue_contact
        if (StringUtils.isNotBlank(ticketIssueCustomerDTO.getPkIssueContact())) {
            IssueContactVo issueContactVo = new IssueContactVo();
            issueContactVo.setPk_issue_contact(ticketIssueCustomerDTO.getPkIssueContact());
            issueContactVo.setFd_customer_uid(ticketIssueCustomerDTO.getFdCustomerPhone());
            issueContactVo.setFd_contact_channel_code(Util.detectChannelByPhone(ticketIssueCustomerDTO.getFdCustomerPhone()));
            issueContactDao.updateContactChannelCode(issueContactVo);
        }
        // if fk_company_customer != 0 -> update customer
        if (ticketIssueCustomerDTO.isCustomerIsStaff()) {
            // save company information
            CompanyVo companyVo = new CompanyVo();
            companyVo.setFd_company_name(ticketIssueCustomerDTO.getFdCompanyName());
            companyVo.setFd_address_common(ticketIssueCustomerDTO.getFdAddressCommon());
            companyVo.setFd_address_detail(ticketIssueCustomerDTO.getFdAddressDetail());
            companyVo.setFd_company_phone(ticketIssueCustomerDTO.getFdCompanyPhone());
            companyVo.setFd_company_website(ticketIssueCustomerDTO.getFdCompanyWebsite());
            companyVo.setPk_company(ticketIssueCustomerDTO.getPkCompany());
            joinDao.updateCompany(companyVo);
            // save customer from tbl_company_staff
            CompanyStaffVo companyStaffVo = staffDao.getById(ticketIssueCustomerDTO.getFkCompanyCustomer());
            companyStaffVo.setFd_staff_name(ticketIssueCustomerDTO.getFdCustomerName());
            companyStaffVo.setFd_staff_email(ticketIssueCustomerDTO.getFdCustomerEmail());
            companyStaffVo.setFd_staff_phone(ticketIssueCustomerDTO.getFdCustomerPhone());
            companyStaffVo.setFd_address_common(ticketIssueCustomerDTO.getFdCustomerAddressBasic());
            companyStaffVo.setFd_address_detail(ticketIssueCustomerDTO.getFdCustomerAddressDetail());
            companyStaffVo.setFd_address_zipcode(ticketIssueCustomerDTO.getFdAdditionalInformation());
            // save department name by pk_company_staff
            if (ticketIssueCustomerDTO.getPkCompanyDept() != 0) {
                CompanyDeptVo companyDeptVo = new CompanyDeptVo();
                companyDeptVo.setPk_company_dept(ticketIssueCustomerDTO.getPkCompanyDept());
                companyDeptVo.setFd_dept_name(ticketIssueCustomerDTO.getFdCompanyDepartment());
                companyDeptDao.updateNameByPk(companyDeptVo);
            }

            return staffDao.updateStaffAsCustomer(companyStaffVo);
        } else {
            // save customer from tbl_company_customer
            CompanyCustomerVo companyCustomerVo;
            if (ticketIssueCustomerDTO.getFkCompanyCustomer() != 0) {
                companyCustomerVo = companyCustomerDao.getById(ticketIssueCustomerDTO.getFkCompanyCustomer());
            } else {
                companyCustomerVo = new CompanyCustomerVo();
            }
            companyCustomerVo.setFd_customer_email(ticketIssueCustomerDTO.getFdCustomerEmail());
            companyCustomerVo.setFd_customer_phone(ticketIssueCustomerDTO.getFdCompanyPhone());
            companyCustomerVo.setFd_customer_mobile(ticketIssueCustomerDTO.getFdCustomerPhone());
            companyCustomerVo.setFd_customer_kakao_uid(ticketIssueCustomerDTO.getFdCustomerKakaoUid());
            companyCustomerVo.setFd_customer_name(ticketIssueCustomerDTO.getFdCustomerName());
            companyCustomerVo.setPk_company_customer(ticketIssueCustomerDTO.getFkCompanyCustomer());
            companyCustomerVo.setFd_additional_information(ticketIssueCustomerDTO.getFdAdditionalInformation());
            companyCustomerVo.setFd_customer_address_common(ticketIssueCustomerDTO.getFdCustomerAddressBasic());
            companyCustomerVo.setFd_customer_address_detail(ticketIssueCustomerDTO.getFdCustomerAddressDetail());
            companyCustomerVo.setFd_company_address_common(ticketIssueCustomerDTO.getFdAddressCommon());
            companyCustomerVo.setFd_company_address_detail(ticketIssueCustomerDTO.getFdAddressDetail());
            companyCustomerVo.setFd_company_name(ticketIssueCustomerDTO.getFdCompanyName());
            companyCustomerVo.setFd_company_dept(ticketIssueCustomerDTO.getFdCompanyDepartment());
            companyCustomerVo.setFd_company_position(ticketIssueCustomerDTO.getFdCompanyPosition());
            if (ticketIssueCustomerDTO.getFkCompanyCustomer() != 0) {
                companyCustomerVo.setFk_modifier(ticketIssueCustomerDTO.getPkStaffLogin());
                return companyCustomerDao.updateCompanyCustomer(companyCustomerVo);
            } else {
                companyCustomerVo.setFk_writer(ticketIssueCustomerDTO.getPkStaffLogin());
                companyCustomerVo.setFk_company(ticketIssueCustomerDTO.getPkCompany());
                companyCustomerVo.setContact_channel_from("A1502");
                companyCustomerVo.setContact_date_from(new Date());
                return companyCustomerDao.insertCompanyCustomer(companyCustomerVo);
            }
        }
    }

    @Override
    public int changeStatusCompanyCustomer(CompanyCustomerVo companyCustomerVo) throws Exception {
        return companyCustomerDao.changeStatusCompanyCustomer(companyCustomerVo);
    }

    @Override
    public Map<String, Object> getCompanyCustomer(CompanyCustomerVo vo) throws Exception {
        log.info("request get Company Customer List = {} ", vo.getSearch().toString());

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> listMap = new ArrayList<>();
        long totalCnt = companyCustomerDao.countCompanyCustomer(vo);

        result.put("totalCnt", totalCnt);

//        if (totalCnt > 0) {
            List<CompanyCustomerVo> companyCustomers = companyCustomerDao.getCompanyCustomer(vo);

            if (companyCustomers == null) {

                throwException.statusCode(204);

            } else {

                for (CompanyCustomerVo companyCustomerVo : companyCustomers) {

                    Map<String, Object> mapAdd = new HashMap<>();

                    mapAdd.put("pkCompanyCustomer", Long.toString(companyCustomerVo.getPk_company_customer()));
                    mapAdd.put("fdCustomerName", companyCustomerVo.getFd_customer_name());
                    mapAdd.put("fdCompanyName", companyCustomerVo.getFd_company_name());
                    mapAdd.put("fdDepartment", companyCustomerVo.getFd_dept_name());
                    mapAdd.put("fdCompanyPosition", companyCustomerVo.getFd_company_position());
                    mapAdd.put("fdPhone", companyCustomerVo.getFd_customer_mobile());
                    mapAdd.put("fdCompanyPhone", companyCustomerVo.getFd_customer_phone());
                    mapAdd.put("fdCellPhone", companyCustomerVo.getFd_customer_mobile());
                    mapAdd.put("fdEmailAddress", companyCustomerVo.getFd_customer_email());
                    mapAdd.put("fdState", companyCustomerVo.getFd_state_name());
                    mapAdd.put("registerer", companyCustomerVo.getRegisterer());
                    mapAdd.put("contactDate", companyCustomerVo.getContact_date_from());
                    mapAdd.put("fdRegisteredDate", companyCustomerVo.getFd_regdate());

                    listMap.add(mapAdd);
                }
            }
//        }
        result.put("list", listMap);

        return result;
    }

    @Override
    public int saveCompanyCustomer(CompanyCustomerVo companyCustomerVo) throws Exception {
        companyCustomerVo.setFd_company_dept(companyCustomerVo.getFd_dept_name());
        if (companyCustomerVo.getPk_company_customer() == 0) {
            return companyCustomerDao.insertCompanyCustomer(companyCustomerVo);
        } else {
            return companyCustomerDao.updateCompanyCustomer(companyCustomerVo);
        }
    }

    @Override
    public CompanyCustomerVo getById(long id) {
        return companyCustomerDao.getById(id);
    }

    @Override
    public void deleteCustomers(List<CompanyCustomerVo> vo) throws Exception {
        for (CompanyCustomerVo companyCustomerVo : vo) {
            companyCustomerDao.deleteCustomers(companyCustomerVo);
            log.debug("********** updated row : {}", companyCustomerVo);
        }
    }

    @Override
    public Map<String, Object> getDetailCompanyCustomer(CompanyCustomerVo vo) throws Exception {

        Map<String, Object> result = new HashMap<>();
        CompanyCustomerVo companyCustomerVo = companyCustomerDao.getCompanyCustomerDetail(vo);
        if (null == companyCustomerVo) {
            throwException.statusCode(204);
        }
        result.put("companyCustomerInfo", companyCustomerVo);
        return result;
    }
}
