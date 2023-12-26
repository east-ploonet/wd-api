package com.saltlux.aice_fe.pc.issue.dto;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class IssueTicketDTO extends BaseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long pkIssueTicket;
    private String pkIssueContact;
    private String fdTicketTitle;
    private String fdContactChannelName;
    private String workFlowCode;
    private long cntContact;
    private long cntDialog;
    private String fdCustomerName;
    private String fdCustomerPhone;
    private String fdStaffName;
    private String fdTicketStatusCodeName;
    private String writerName;
    private String registeredDate;
    private String priority;
    private String fdDueDate;
    private long dueDateLongTime;
    private String aiName;
    private String pkCode;
    private String fk_priority; // this field will map with fk_priority from Database
    private String fdComment;
    private String customerAni;

    private String fdCustomerUid;
    private long fk_assign_staff;
    private String  issue_ticket_tags;          // 태그 리스트(구분자 ',' )
    private String fd_message;

    public IssueTicketDTO() {
    }

    public IssueTicketDTO(String fdCustomerUid) {
        this.fdCustomerUid = fdCustomerUid;
    }
}
