package com.saltlux.aice_fe.pc.issue.dto;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class TicketIssueCustomerDTO extends BaseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long pkIssueTicket;
    private String fdTicketTitle;
    private long cntContact;
    private String fdCustomerName;
    private String fdCustomerEmail;
    private String fdCustomerPhone;
    private String fdCustomerKakaoUid;
    private long fkCompanyCustomer;
    private String fdCompanyName;
    private String fdAddressCommon;
    private String fdAddressDetail;
    private String fdCompanyPhone;
    private String fdCompanyWebsite;
    private long pkCompany;
    private String fdAdditionalInformation;
    private String fdCustomerAddressBasic;
    private String fdCustomerAddressDetail;
    private String fdCompanyPosition;
    private String fdCompanyDepartment;
    private long pkCompanyDept;
    private String pkIssueContact;
    private String fdTicketWorkflowCode;

    private long pkStaffLogin;
    private boolean customerIsStaff = false;
}
