package com.saltlux.aice_fe.pc.aiticket.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DialogueVo {

    private int pkIssueDialogue;
    private String fdMessage;
    private int fkIssueDialogue;
    private String fkIssueContact;
    private String fdCallBrokerId;
    private String fdMessageTo;
    private int fkWriter;
}
