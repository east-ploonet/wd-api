package com.saltlux.aice_fe.pc.aiticket.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.saltlux.aice_fe.pc.aiticket.vo.AiTicketVo;
import com.saltlux.aice_fe.pc.aiticket.vo.DialogueVo;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao;
import com.saltlux.aice_fe.pc.aiticket.service.AiTicketService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AiTicketServiceImpl extends BaseServiceImpl implements AiTicketService {

    @Autowired
    private AiTicketDao aiTicketDao;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Override
	public Map<String, Object> getList(Integer offset, Integer pageSize, String startDate, String endDate, String pkIssueContact, String searchAiName, String searchType, String searchCallerName, String searchCustomerInfo) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		DataMap resultMap = null;
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("offset", offset);
		paramMap.put("pageSize", pageSize);
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("pkIssueContact", pkIssueContact);
		paramMap.put("searchAiName", searchAiName);
		paramMap.put("searchType", searchType);
		paramMap.put("searchCallerName", searchCallerName);
		paramMap.put("searchCustomerInfo", searchCustomerInfo);
		
		//Map<String, Object> result          = new HashMap<>();
		
		Map<String, Object> result          = new HashMap<>();
		int totalCnt = aiTicketDao.getListCnt(paramMap);
		List<Object> listData = new ArrayList<Object>();
		if(totalCnt > 0) {
			listData = aiTicketDao.getList(paramMap);
		}
				
		result.put("list", listData);
		List<Object> aiTypelistData = aiTicketDao.getAiTypeList(paramMap);
		
		result.put("aiTypeList", aiTypelistData);
//		result.put("aiTypeList", null);
		//int totalCnt = listData.size();
		
		result.put("totalCnt", totalCnt);
		return result;
	}
	
	@Override
	public Map<String, Object> getDetail(String pkIssueContact) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		
		DataMap resultMap = null;
		Map<String, Object> paramMap = new HashMap<>();
		List<String> list = new ArrayList<String>();
		
			
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("pkIssueContact", pkIssueContact);
		
		Map<String, Object> result          = new HashMap<>();
		List<Object> listData = new ArrayList<Object>();
		listData = aiTicketDao.getView(paramMap);
		result.put("list", listData);

		return result;
	}
	
	
	@Override
	public Map<String, Object> getTicketList(Integer offset, Integer pageSize, String startDate, String endDate, 
			String pkIssueTicket, String searchAiName, String searchType, String searchCallerName, String searchCustomerInfo,
			String searchTypeList, String searchWorkflow, String searchStaff
			) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		
		DataMap resultMap = null;
		Map<String, Object> paramMap = new HashMap<>();
		List<String> list = new ArrayList<String>();
		
		if(!Common.isBlank(searchTypeList)) {
			searchTypeList = searchTypeList.replace(" ", "");
			if(!Common.isBlank(searchTypeList)) {
				String []  searchList = searchTypeList.split(",");
				paramMap.put("searchTypeList", searchList);
			}
		}
			
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("offset", offset);
		paramMap.put("pageSize", pageSize);
		paramMap.put("startDate", startDate.trim());
		paramMap.put("endDate", endDate.trim());
		paramMap.put("pkIssueTicket", pkIssueTicket);
		paramMap.put("searchAiName", searchAiName);
		paramMap.put("searchType", searchType);
		paramMap.put("searchCallerName", searchCallerName);
		paramMap.put("searchCustomerInfo", searchCustomerInfo);
		paramMap.put("searchWorkflow", searchWorkflow);
		paramMap.put("searchStaff", searchStaff);
		
		if(!loginInfoVo.getLoginLevelCode().equals("A1001")) {
			paramMap.put("loginStaffPk", loginInfoVo.getLoginCompanyStaffPk());
		}
		
		//Map<String, Object> result          = new HashMap<>();
		
		Map<String, Object> result          = new HashMap<>();
		DataMap resultCnt          = new DataMap();
		resultCnt = aiTicketDao.getTicketListCnt2(paramMap);
		List<Object> listData = new ArrayList<Object>();
		if(Common.parseInt(resultCnt.get("cnt"))> 0) {
			listData = aiTicketDao.getNewTicketList(paramMap);
			result.put("list", listData);
		}else {
			result.put("list", listData);
		}
		
		List<Object> aiTypelistData = aiTicketDao.getAiTypeList(paramMap);
		result.put("aiTypeList", aiTypelistData);
//		int totalCnt = aiTicketDao.getTicketListCnt(paramMap);
//		result.put("totalCnt", totalCnt);

		
		result.put("resultCnt", resultCnt);
		return result;
	}
	
	@Override
	public Map<String, Object> getTicketDetail(String pkIssueContact) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		
		DataMap resultMap = null;
		Map<String, Object> paramMap = new HashMap<>();
		List<String> list = new ArrayList<String>();
		
			
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("pkIssueContact", pkIssueContact);
		
		if(!loginInfoVo.getLoginLevelCode().equals("A1001")) {
			paramMap.put("loginStaffPk", loginInfoVo.getLoginCompanyStaffPk());
		}
		
		
		Map<String, Object> result          = new HashMap<>();
		List<Object> listData = new ArrayList<Object>();
		listData = aiTicketDao.getTicketList(paramMap);
		result.put("list", listData);

		return result;
	}
	
	@Override
	public List<Object> getTicketListDue(String startDate, String endDate, String pkIssueTicket, String searchAiName, String searchType , String searchCustomerInfo, String searchTypeList, String searchWorkflow, String searchStaff) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("startDate", startDate.trim());
		paramMap.put("endDate", endDate.trim());
		paramMap.put("pkIssueTicket", pkIssueTicket);
		paramMap.put("searchAiName", searchAiName);
		paramMap.put("searchType", searchType);
		paramMap.put("searchCustomerInfo", searchCustomerInfo);
		paramMap.put("searchWorkflow", searchWorkflow);
		paramMap.put("searchStaff", searchStaff);
		
		
		Map<String, Object> result          = new HashMap<>();
		
		List<Object> listData = aiTicketDao.getTicketListDue(paramMap);
		
		return listData;
	}
	
	@Override
	public List<Object> getListDue(String startDate, String endDate, String searchType) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("searchType", searchType);
		
		Map<String, Object> result          = new HashMap<>();
		
		List<Object> listData = aiTicketDao.getListDue(paramMap);
		
		return listData;
	}
	
	@Override
	public Map<String, Object> getAiTypeList() throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		DataMap resultMap = null;
		Map<String, Object> paramMap = new HashMap<>();
		
		//Map<String, Object> result          = new HashMap<>();
		
		Map<String, Object> result          = new HashMap<>();
		List<Object> listData = aiTicketDao.getAiTypeList(paramMap);
		result.put("aiTypeList", listData);
		
		return result;
	}
	
	@Override
	public Map<String, Object> getStaffList() throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		DataMap resultMap = null;
		Map<String, Object> paramMap = new HashMap<>();
		
		
		Map<String, Object> result          = new HashMap<>();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		
		List<Object> listData = aiTicketDao.getStaffList(paramMap);
		result.put("getStaffList", listData);
		
		return result;
	}

	@Override
	public Map<String, Object> getDialogue(String fdCallBrokerId) {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("fdCallBrokerId", fdCallBrokerId);
		
		Map<String, Object> result          = new HashMap<>();
		List<Object> listData = aiTicketDao.getDialogue(paramMap);
		result.put("list", listData);
		
		return result;
	}

    
	@Override
	public Map<String, Object> getCustomerInfo(String ani, String caller) {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("ani", ani);
		
		DataMap staffResultMap = null;
		DataMap customerResultMap = null;
		
		Map<String, Object> result          = new HashMap<>();
		
		if(caller.equals("") ) {
			System.out.println("caller != ''");
		}else if(caller.equals("staff") || caller.equals("USER01")){
			staffResultMap = aiTicketDao.getStaffInfo(paramMap);
			result.put("staffInfo", staffResultMap);
		}else {
			customerResultMap = aiTicketDao.getCustomerInfo(paramMap);
			result.put("customerInfo", customerResultMap);
		}
		
		return result;
	}
	
	
	@Override
	public void updateCustomerInfo(
			String callerType,
			Map<String, Object> paramMap // customer
			) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> paramMapUpdate = new HashMap<>();
		
		try {
			
			int result = 0;
			
			// staff => USER01
			if(callerType.equals("USER01")) {
				// company_staff 로 update
				paramMapUpdate.put("pkCompanyStaff", Common.parseInt(paramMap.get("pkCompanyStaff")));
				paramMapUpdate.put("fdStaffName", Common.NVL(paramMap.get("fdStaffName"),"") );
				paramMapUpdate.put("fdStaffMobile", Common.NVL(paramMap.get("fdStaffMobile"),"") );
				paramMapUpdate.put("fdStaffPhone", Common.NVL(paramMap.get("fdStaffPhone"),"") );
				paramMapUpdate.put("fdStaffEmail", Common.NVL(paramMap.get("fdStaffEmail"),"") );
				paramMapUpdate.put("fdStaffDuty", Common.NVL(paramMap.get("fdStaffDuty"),"") );
				paramMapUpdate.put("deptDispName", Common.NVL(paramMap.get("deptDispName"),"") );
				paramMapUpdate.put("fdAddressCommon", Common.NVL(paramMap.get("fdAddressCommon"),"") );
				paramMapUpdate.put("fdAddressDetail", Common.NVL(paramMap.get("fdAddressDetail"),"") );
				paramMapUpdate.put("fkCompany", Common.parseInt(paramMap.get("fkCompany")) ); 
				paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
				paramMapUpdate.put("fdAdditionalInformation", Common.NVL(paramMap.get("fdAdditionalInformation"),"") ); // 없음 그냥...
				
				result = aiTicketDao.updateCompanyStaffInfo(paramMapUpdate);
				
				////////// 회사 DB로 따로 집어넣어야 할듯?
				
				paramMapUpdate.put("fdCompanyName", Common.NVL(paramMap.get("fdCompanyName"),"") ); // 회사 DB
				paramMapUpdate.put("fdCompanyPhone", Common.NVL(paramMap.get("fdCompanyPhone"),"") ); // 회사 DB
				paramMapUpdate.put("fdCompanyAddressCommon", Common.NVL(paramMap.get("fdCompanyAddressCommon"),"") ); // 회사 DB
				paramMapUpdate.put("fdCompanyAddressDetail", Common.NVL(paramMap.get("fdCompanyAddressDetail"),"") ); // 회사 DB
				
				result = aiTicketDao.updateStaffCompanyInfo(paramMapUpdate);
				
			}else {
				// company_customer 로 update
				int fkCompany = Common.parseInt(paramMap.get("fkCompany"));
				if(fkCompany == 0) {
					fkCompany = (int) loginInfoVo.getLoginCompanyPk(); 
				}
				paramMapUpdate.put("pkCompanyCustomer", Common.parseInt(paramMap.get("pkCompanyCustomer")));
				paramMapUpdate.put("fkCompany", fkCompany);
				paramMapUpdate.put("fdCustomerName", Common.NVL(paramMap.get("fdCustomerName"),"") );
				paramMapUpdate.put("fdCompanyName", Common.NVL(paramMap.get("fdCompanyName"),"") );
				paramMapUpdate.put("fdCompanyDept", Common.NVL(paramMap.get("fdCompanyDept"),"") );
				paramMapUpdate.put("fdCompanyPosition", Common.NVL(paramMap.get("fdCompanyPosition"),"") );
				paramMapUpdate.put("fdCustomerMobile", Common.NVL(paramMap.get("fdCustomerMobile"),"") );
				paramMapUpdate.put("fdCustomerPhone", Common.NVL(paramMap.get("fdCustomerPhone"),"") );
				paramMapUpdate.put("fdCustomerEmail", Common.NVL(paramMap.get("fdCustomerEmail"),"") );
				paramMapUpdate.put("fdAdditionalInformation", Common.NVL(paramMap.get("fdAdditionalInformation"),"") );
				paramMapUpdate.put("fdCustomerAddressCommon", Common.NVL(paramMap.get("fdCustomerAddressCommon"),"") );
				paramMapUpdate.put("fdCustomerAddressDetail", Common.NVL(paramMap.get("fdCustomerAddressDetail"),"") );
				paramMapUpdate.put("fdCompanyAddressCommon", Common.NVL(paramMap.get("fdCompanyAddressCommon"),"") );
				paramMapUpdate.put("fdCompanyAddressDetail", Common.NVL(paramMap.get("fdCompanyAddressDetail"),"") );
				paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
				
				result = aiTicketDao.updateCompanyCustomerInfo(paramMapUpdate);
			}
		
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		} catch (Exception ex) {
			System.out.println("ex:" + ex);
//			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	
	
	@Override
	public void updateTicketInfo(
			Map<String, Object> paramMap // ticket
			) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> paramMapUpdate = new HashMap<>();
		
		int result = 0;
		try {
			
			paramMapUpdate.put("pkIssueTicket", paramMap.get("fkIssueTicket"));
			paramMapUpdate.put("fkPriority", paramMap.get("fkPriority"));
			paramMapUpdate.put("fdTicketWorkflowCode", paramMap.get("fdTicketWorkflowCode"));
			paramMapUpdate.put("fdTicketLimitDate", paramMap.get("fdTicketLimitDate"));
			paramMapUpdate.put("fdComment", Common.NVL(paramMap.get("fdComment"),""));
			paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
			
			result = aiTicketDao.updateTicketInfo(paramMapUpdate);
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		}catch  (Exception ex) {
			System.out.println("ex:" + ex);
//			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	
	@Override
	public void updateTicketFlow(
			String pkIssueTicket, String workflowCode
			) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> paramMapUpdate = new HashMap<>();
		
		int result = 0;
		if(workflowCode.equals("A1301")) {
			workflowCode = "A1302";
		}
		try {
			
			paramMapUpdate.put("pkIssueTicket", pkIssueTicket);
			paramMapUpdate.put("fdTicketWorkflowCode", workflowCode);
			paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
			
			result = aiTicketDao.updateTicketFlow(paramMapUpdate);
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		}catch  (Exception ex) {
			System.out.println("ex:" + ex);
//			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}

	@Override
	public void changeTicketStaff(String fkIssueTicket, String staffPk) throws Exception {
		// TODO Auto-generated method stub
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> paramMapUpdate = new HashMap<>();
		
		int result = 0;
		try {
			
			paramMapUpdate.put("pkIssueTicket", fkIssueTicket);
			paramMapUpdate.put("staffPk", staffPk);
			paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
			
			result = aiTicketDao.changeTicketStaff(paramMapUpdate);
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		}catch  (Exception ex) {
			System.out.println("ex:" + ex);
//			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}

	@Override
	public int updateIssueTicket(AiTicketVo aiTicketVo) throws Exception {
		int fkIssueDialogueStart = aiTicketDao.getDialogue(aiTicketVo);
		aiTicketVo.setFkIssueDialogueStart(fkIssueDialogueStart);

		int fkAssignStaff = aiTicketDao.getAssignStaff(aiTicketVo);
		aiTicketVo.setFkAssignStaff(fkAssignStaff);

		aiTicketDao.createTicket(aiTicketVo);
		int pkIssueTicket =  aiTicketDao.getPkIssueTicket(aiTicketVo);
		Map<String, Object> paramMapUpdate = new HashMap<>();
		paramMapUpdate.put("pkIssueTicket", pkIssueTicket);
		paramMapUpdate.put("tbBrokerId", aiTicketVo.getTbBrokerId());

		return aiTicketDao.updatePkIssueTicket(paramMapUpdate);

	}


	@Override
	public int updateDialogueMessage(List<DialogueVo> dialogueVoList) throws Exception {
		int totalUpdated = 0;

		for (DialogueVo dialogueVo : dialogueVoList) {
			int pkIssueDialogue = dialogueVo.getPkIssueDialogue();
			String fdMessage = dialogueVo.getFdMessage();

			Map<String, Object> paramMapUpdate = new HashMap<>();

			paramMapUpdate.put("pkIssueDialogue", pkIssueDialogue);
			paramMapUpdate.put("fdMessage", fdMessage);
			int updatedRows = aiTicketDao.updateFdMessage(paramMapUpdate);
			totalUpdated += updatedRows;
		}
		return totalUpdated;
	}

	@Override
	public int addDialogueLog(DialogueVo dialogueLogVo) {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		int result = 0;

		try {
			Map<String, Object> paramMapUpdate = new HashMap<>();

			paramMapUpdate.put("fkIssueDialogue", dialogueLogVo.getFkIssueDialogue());
			paramMapUpdate.put("fkIssueContact", dialogueLogVo.getFkIssueContact());
			paramMapUpdate.put("fdCallBrokerId", dialogueLogVo.getFdCallBrokerId());
			paramMapUpdate.put("fdMessage", dialogueLogVo.getFdMessage());
			paramMapUpdate.put("fdMessageTo", dialogueLogVo.getFdMessageTo());
			paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());

			result = aiTicketDao.addDialogueLog(paramMapUpdate);

		} catch (Exception e) {
			result = -1;
		}
		return result;
	}


	/**
	 * 2023.12.05 leedh tbl_form_input_data 레코드 조회
	 */
	@Override
	public List<Object> getFormInputData(
			String fkIssueTicket
	) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		DataMap resultMap = new DataMap();
		Map<String, Object> mapData = new HashMap<>();
		List<Object> inputList = aiTicketDao.getFormInputData(fkIssueTicket);
		log.info("#getFormInputData : {}", inputList);
		return inputList;
	}

	@Override
	public Map<String, Object> updateFormData(
			String fkIssueTicket,
			Map<String, Object> reqMap
	) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		int result = 0;
		Long companySeq = loginInfoVo.getLoginCompanyPk();
		String brokerId = (String) reqMap.get("fdCallBrokerId");
		String contactId = (String) reqMap.get("fkIssueContact");

		try {
			for (Map.Entry<String, Object> entry : reqMap.entrySet()) {
				String inputKey = entry.getKey();
				Object inputValue = entry.getValue();

				if (!"fkIssueTicket".equals(inputKey)) {
					Map<String, Object> updateMap = new HashMap<>();
					updateMap.put("fkIssueTicket", fkIssueTicket);
					updateMap.put("inputKey", inputKey);
					updateMap.put("inputValue", inputValue);

					result = aiTicketDao.updateFormData(updateMap);
				}
			}
			if (result > 0) {
				Map<String, Object> mapExtComm = new HashMap<>();
				mapExtComm.put("fkCompany", companySeq.toString());
				mapExtComm.put("fkIssueTicket", fkIssueTicket);
				mapExtComm.put("extAct", "UPDATE_RESERVE");
				mapExtComm.put("mapData", reqMap);
				mapExtComm.put("tbBrokerId", brokerId);
				mapExtComm.put("fkIssueContact", contactId);
				mapExtComm.put("formCode", "FORM_CONSULT_EXPRESS");

				JSONObject reqExtComm = new JSONObject(mapExtComm);

				StringBuilder sbRouteKey = new StringBuilder();
				sbRouteKey.append(companySeq.toString());
				sbRouteKey.append(".");
				sbRouteKey.append("UPDATE_RESERVE");

				log.info("updateFormData v1.ex.ext.comm.update route:{},data:{}",sbRouteKey.toString(),reqExtComm);

				rabbitTemplate.convertAndSend("v1.ex.ext.comm.update",sbRouteKey.toString(), reqExtComm.toString());
			}
		} catch (Exception e) {
			log.error("updateFormData errpr: {}", e);
			throwException.statusCode(500);
		}
		return null;
	}

	public String toJson() {
		StringBuilder sb = new StringBuilder();
		try {
			ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
			sb.append(mapper.writeValueAsString(this));
		}catch(Exception e) {
			log.error("DaoJson error",e);
		}
		return sb.toString();
	}

	@Override
	public Map<String, Object> updateTicketComment(String fkIssueTicket, String commentData) throws Exception {
		String finalComment = commentData.replaceAll("^\"|\"$", "");

		String[] keyValuePairs = finalComment.split(", ");
		StringBuilder formattedComment = new StringBuilder();

		for (String keyValue : keyValuePairs) {
			String[] parts = keyValue.split(":");
			if (parts.length >= 1) {
				String key = parts[0].trim();
				String value = (parts.length > 1) ? parts[1].trim() : "";
				formattedComment.append(key).append(" : ").append(value).append("\n");
			} else {
				throw new IllegalArgumentException("wrong key-value pair format: " + keyValue);
			}
		}

		Map<String, Object> paramMapUpdate = new HashMap<>();
		paramMapUpdate.put("fkIssueTicket", fkIssueTicket);
		paramMapUpdate.put("commentData", formattedComment.toString());
		paramMapUpdate.put("finalComment", finalComment);

		int result = aiTicketDao.updateTicketComment(paramMapUpdate);

		if (result > 0) {
			log.info("update to ticket comment");
		} else {
			log.error("Failed to ticket comment" + finalComment);
		}

		return null;
	}

}
