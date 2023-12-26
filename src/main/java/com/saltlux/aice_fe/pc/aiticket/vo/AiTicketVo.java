package com.saltlux.aice_fe.pc.aiticket.vo;

import lombok.Data;


@Data
public class AiTicketVo {
    private String tbBrokerId;      // broker_id
    private String dnis;            // dnis
    private int fkCompany;       // fk_company
    private String callerType;      // caller_type
    private String ani;             //  customer_ani
    private String fromName;        // caller_name
    private String fromFk;          // fk_company_customer
    private String msgTitle;        // fd_ticket_title
    private String fkCompanyStaffAi; // fk_assign_ai
    private int fkWriter;        // fk_writer
    private int fkIssueDialogueStart; // fk_issue_dialogue_start
    private int fkAssignStaff; // fk_assign_staff


}
