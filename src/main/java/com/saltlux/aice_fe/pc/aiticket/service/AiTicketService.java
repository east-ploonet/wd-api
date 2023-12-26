package com.saltlux.aice_fe.pc.aiticket.service;

import java.util.List;
import java.util.Map;

import com.saltlux.aice_fe.pc.aiticket.vo.AiTicketVo;
import com.saltlux.aice_fe.pc.aiticket.vo.DialogueVo;

public interface AiTicketService {

	Map<String, Object> getList(Integer offset, Integer pageSize, String startDate, String endDate, String pkIssueContact, String searchAiName, String searchType, String searchCallerName, String searchCustomerInfo) throws Exception;
	
	Map<String, Object> getDetail(String pkIssueContact) throws Exception;
	
	Map<String, Object> getTicketList(Integer offset, Integer pageSize, String startDate, String endDate, String pkIssueTicket, String searchAiName, String searchType, String searchCallerName, String searchCustomerInfo, String searchTypeList, String searchWorkflow, String searchStaff) throws Exception;
	
	Map<String, Object> getTicketDetail(String pkIssueContact) throws Exception;
	
	List<Object> getListDue(String startDate, String endDate, String searchType) throws Exception;
	
	List<Object> getTicketListDue(String startDate, String endDate, String pkIssueTicket, String searchAiName, String searchType , String searchCustomerInfo, String searchTypeList, String searchWorkflow, String searchStaff) throws Exception;
	
	Map<String, Object> getStaffList() throws Exception;
	
	Map<String, Object> getAiTypeList() throws Exception;

//	Map<String, Object> getDialogue(String pkIssueContact) throws Exception;
	Map<String, Object> getDialogue(String fdCallBrokerId) throws Exception;
	
	Map<String, Object> getCustomerInfo(String ani, String caller) throws Exception;
	
	void updateCustomerInfo(String callerType, Map<String, Object> paramMap) throws Exception;
	
	void updateTicketInfo(Map<String, Object> paramMap) throws Exception;
	
	void updateTicketFlow(String pkIssueTicket, String workflowCode) throws Exception;
	
	void changeTicketStaff(String fkIssueTicket, String staffPk) throws Exception;

	int updateIssueTicket(AiTicketVo aiTicketVo) throws Exception;

	int updateDialogueMessage(List<DialogueVo> dialogueVo) throws Exception;

	int addDialogueLog(DialogueVo dialogueLogVo) throws Exception;

	List<Object> getFormInputData(String fkIssueTicket) throws Exception;

	Map<String, Object> updateFormData(String fkIssueTicket, Map<String, Object> reqBody) throws Exception;

	Map<String, Object> updateTicketComment(String fkIssueTicket, String commentData) throws Exception;

}
