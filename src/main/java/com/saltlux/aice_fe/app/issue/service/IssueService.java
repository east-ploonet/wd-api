package com.saltlux.aice_fe.app.issue.service;

import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;

import java.util.Map;

public interface IssueService {

	Map<String, Object> getIssueList(IssueTicketVo vo)  throws Exception;
	Map<String, Object> getIssueDialogueList(IssueTicketVo vo)  throws Exception;
	Map<String, Object> getIssueDialogueCustomer(IssueTicketVo vo)  throws Exception;
	Map<String, Object> getIssueDialogueDetail(IssueTicketVo vo)  throws Exception;
	void updateIssueTicketWorkflow(IssueTicketVo vo)  throws Exception;

}
