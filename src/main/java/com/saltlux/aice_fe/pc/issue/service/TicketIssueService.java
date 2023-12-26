package com.saltlux.aice_fe.pc.issue.service;

import com.saltlux.aice_fe.pc.issue.dto.IssueTicketDTO;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;

import java.util.List;
import java.util.Map;

public interface TicketIssueService {

	Map<String, Object> getIssues(IssueTicketVo vo)  throws Exception;
	List<IssueTicketDTO> getAllIssues(IssueTicketVo vo)  throws Exception;
	Map<String, Object> getIssueDialogueList(IssueTicketVo vo)  throws Exception;
	Map<String, Object> getStatusIssueTicket(IssueTicketVo issueTicketVo, List<String> listStatus)  throws Exception;
	Map<String, Object> getIssueDialogueCustomer(IssueTicketVo vo)  throws Exception;
	Map<String, Object> getIssueDialogueDetail(IssueTicketVo vo)  throws Exception;
	Map<String, Object> getIssueTicketDetail(IssueTicketVo vo)  throws Exception;
	void updateIssueTicketWorkflow(IssueTicketVo vo)  throws Exception;
	void updateIssueTicketsStatus(List<IssueTicketVo> vo) throws Exception;
	void updateIssueTicketStatus(IssueTicketVo vo) throws Exception;
	void updateIssueTicketPriority(IssueTicketVo vo) throws Exception;
	void updateIssueTicketManager(IssueTicketVo vo) throws Exception;
	void deleteIssueTickets(List<IssueTicketVo> vo) throws Exception;
	void updateTicketLimitDate(IssueTicketVo vo) throws Exception;
	void updateIssueTicket(IssueTicketDTO vo) throws Exception;

	void deleteDataGarbageTicket() throws Exception;

	void clearAllCacheInTicket() throws Exception;
}
