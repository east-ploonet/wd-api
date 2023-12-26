package com.saltlux.aice_fe.app.issue.service.Impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe.app.issue.dao.IssueDao;
import com.saltlux.aice_fe.app.issue.service.IssueService;
import com.saltlux.aice_fe.pc.issue.dao.TicketIssueDao;
import com.saltlux.aice_fe.pc.issue.dto.IssueTicketDTO;
import com.saltlux.aice_fe.pc.issue.dto.TicketIssueCustomerDTO;
import com.saltlux.aice_fe.pc.issue.service.TicketIssueService;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueDialogueDetailVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueDialogueVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class IssueServiceImpl extends BaseServiceImpl implements IssueService {

    @Autowired
    private IssueDao issueDao;

	@Autowired
	private TicketIssueDao ticketIssueDao;

    @Autowired
	private TicketIssueService ticketIssueService;

    // 일감 티켓 목록
    @Override
    public Map<String, Object> getIssueList(IssueTicketVo reqIssueTicketVo) throws Exception {

        Map<String, Object> result          = new HashMap<>();
        List<Map<String, Object>> listMap   = new ArrayList<>();

	    int totalCnt                        = issueDao.getTicketAllCnt(reqIssueTicketVo);
	    Map<String, Long> workflowCntMap    = issueDao.getTicketWorkflowCnt(reqIssueTicketVo);

	    //총 게시물수
	    result.put("totalCnt"   , totalCnt);
	    result.put("workflowCnt", workflowCntMap);

	    if(totalCnt > 0){
	        List<IssueTicketVo> listIssueTicketVo = issueDao.getTicketList(reqIssueTicketVo);

	        if(listIssueTicketVo == null){

	            throwException.statusCode(204);

	        } else {

		        for(IssueTicketVo issueTicketVo : listIssueTicketVo){

			        Map<String, Object> mapAdd = new HashMap<>();

			        mapAdd.put("pkIssueTicket"          ,Long.toString(issueTicketVo.getPk_issue_ticket()));
			        mapAdd.put("ticketWorkflowCode"     ,issueTicketVo.getFd_ticket_workflow_code());
			        mapAdd.put("ticketStatusCode"       ,issueTicketVo.getFd_ticket_status_code());
			        mapAdd.put("ticketTitle"            ,issueTicketVo.getFd_ticket_title());
			        mapAdd.put("fkIssueDialogueStart"   ,issueTicketVo.getFk_issue_dialogue_start());
			        mapAdd.put("fkAssignStaff"          ,issueTicketVo.getFk_assign_staff());
			        mapAdd.put("ticketOwnerUid"         ,issueTicketVo.getFd_ticket_owner_uid());
			        mapAdd.put("ticketLimitDate"        ,FormatUtils.dateToStringCustomize( issueTicketVo.getFd_ticket_limit_date() , "yyyy/MM/dd HH:mm:ss"));
//			        mapAdd.put("fk_writer"              ,issueTicketVo.getFk_writer());
			        mapAdd.put("regDate"                ,FormatUtils.dateToStringCustomize( issueTicketVo.getFd_regdate()           , "yyyy/MM/dd HH:mm:ss"));
//			        mapAdd.put("fk_modifier"            ,issueTicketVo.getFk_modifier());
			        mapAdd.put("modDate"                ,FormatUtils.dateToStringCustomize( issueTicketVo.getFd_moddate()           , "yyyy/MM/dd HH:mm:ss"));

			        //-- Extend Fields --//
			        mapAdd.put("pkIssue_contact"        ,issueTicketVo.getPk_issue_contact());
			        mapAdd.put("contactStatusCode"      ,issueTicketVo.getFd_contact_status_code());
			        mapAdd.put("contactChannelCode"     ,issueTicketVo.getFd_contact_channel_code());

			        mapAdd.put("pkIssueDialogue"        ,issueTicketVo.getPk_issue_dialogue());
			        mapAdd.put("fkCompanyStaff"         ,issueTicketVo.getFk_company_staff());
			        mapAdd.put("customerUid"            ,issueTicketVo.getFd_customer_uid());
			        mapAdd.put("message"                ,issueTicketVo.getFd_message());

			        mapAdd.put("customerName"           ,issueTicketVo.getFd_customer_name());
			        mapAdd.put("dialogue_read_n_cnt"    ,issueTicketVo.getDialogue_read_n_cnt());

			        mapAdd.put("issue_ticket_tags"      ,issueTicketVo.getIssue_ticket_tags());

			        listMap.add(mapAdd);
		        }
	        }
	    }
	    result.put("list", listMap);

	    return result;
    }

	// 일감 대화내역
	@Override
	public Map<String, Object> getIssueDialogueList(IssueTicketVo reqIssueTicketVo) throws Exception {

		Map<String, Object> result              = new HashMap<>();
		List<Map<String, Object>> listMap       = new ArrayList<>();

		List<IssueDialogueVo> listIssueDialogueVo   = ticketIssueDao.getTicketDialogueList(reqIssueTicketVo);

		Map<String, Object> dataCustomer = getIssueDialogueDetail(reqIssueTicketVo);
		String customerName = ((IssueDialogueDetailVo) dataCustomer.get("dialogueInfo")).getFd_customer_name();

		if(listIssueDialogueVo == null){
			throwException.statusCode(204);

		} else {

			for(IssueDialogueVo issueDialogueVo : listIssueDialogueVo){

				Map<String, Object> mapAdd = new HashMap<>();

				mapAdd.put("pkIssueDialogue"        ,Long.toString(issueDialogueVo.getPk_issue_dialogue()));
				mapAdd.put("fkIssueContact"         ,issueDialogueVo.getFk_issue_contact());
				mapAdd.put("fkCompanyStaff"         ,issueDialogueVo.getFk_company_staff());
				mapAdd.put("fkCompanyCustomer"      ,issueDialogueVo.getFk_company_customer());
				mapAdd.put("customerUid"            ,issueDialogueVo.getFd_customer_uid());
				mapAdd.put("dialogueStatusCode"     ,issueDialogueVo.getFd_dialogue_status_code());
				mapAdd.put("message"                ,issueDialogueVo.getFd_message());
				mapAdd.put("regDate"                ,FormatUtils.dateToStringCustomize( issueDialogueVo.getFd_regdate()     , "yyyy/MM/dd HH:mm:ss"));
				mapAdd.put("dialogueMessageCode"    ,issueDialogueVo.getFd_dialogue_message_code());
				mapAdd.put("readYn"                 ,issueDialogueVo.getFd_read_yn());
				mapAdd.put("contactChannelCode"     ,issueDialogueVo.getFd_contact_channel_code());

				mapAdd.put("customerName"           ,customerName);
				mapAdd.put("staffAiUid"             ,issueDialogueVo.getFd_staff_ai_uid());

				listMap.add(mapAdd);
			}

			try{
				// 목록 바인딩까지 됐으면 읽음여부 Y 처리
				issueDao.updateTicketDialogueRead(reqIssueTicketVo);

				// 현재 일감이 시작 상태이면 진행중으로 업데이트
				issueDao.updateIssueTicketWorkflowIng(reqIssueTicketVo);
				ticketIssueService.clearAllCacheInTicket();
			}catch (Exception ex){
				log.error( "********** updateTicketDialogueRead() ********** {}", ex.toString());
			}
		}

		result.put("list"           , listMap);

		return result;
	}

	@Override
	public Map<String, Object> getIssueDialogueCustomer(IssueTicketVo reqIssueTicketVo) throws Exception {

		Map<String, Object> result              = new HashMap<>();

		Map<String, Object> getIssueTicketDialog = ticketIssueService.getIssueDialogueCustomer(reqIssueTicketVo);
		TicketIssueCustomerDTO ticketIssueCustomerDTO = ((TicketIssueCustomerDTO) getIssueTicketDialog.get("customerInfo"));
		if(ticketIssueCustomerDTO == null){
			throwException.statusCode(204);
		}
		CompanyCustomerVo companyCustomerVo = new CompanyCustomerVo();
		companyCustomerVo.setPk_company_customer(ticketIssueCustomerDTO.getFkCompanyCustomer());
		companyCustomerVo.setFk_company(ticketIssueCustomerDTO.getPkCompany());
		companyCustomerVo.setFd_customer_name(ticketIssueCustomerDTO.getFdCustomerName());
		companyCustomerVo.setFd_company_name(ticketIssueCustomerDTO.getFdCompanyName());
		companyCustomerVo.setFd_customer_email(ticketIssueCustomerDTO.getFdCustomerEmail());
		companyCustomerVo.setFd_customer_mobile(ticketIssueCustomerDTO.getFdCustomerPhone());
		companyCustomerVo.setFd_company_address_common(ticketIssueCustomerDTO.getFdAddressCommon());
		companyCustomerVo.setFd_company_address_detail(ticketIssueCustomerDTO.getFdAddressDetail());
		companyCustomerVo.setFd_company_phone(ticketIssueCustomerDTO.getFdCompanyPhone());
		companyCustomerVo.setFd_company_position(ticketIssueCustomerDTO.getFdCompanyPosition());
		companyCustomerVo.setFd_ticket_workflow_code(ticketIssueCustomerDTO.getFdTicketWorkflowCode());

		if( companyCustomerVo.getLoginCompanyLogoUrl() != null && !"".equals( companyCustomerVo.getLoginCompanyLogoUrl() )){
			companyCustomerVo.setLoginCompanyLogoUrl( pathBrowserStorage + companyCustomerVo.getLoginCompanyLogoUrl() );
		}
		Map<String, Object> dataCustomer = getIssueDialogueDetail(reqIssueTicketVo);
		String customerName = ((IssueDialogueDetailVo) dataCustomer.get("dialogueInfo")).getFd_customer_name();
		if (null == companyCustomerVo.getFd_customer_name()) {
			companyCustomerVo.setFd_customer_name(customerName);
		}
		result.put("customerInfo"   , companyCustomerVo);

		return result;
	}

	@Override
	public Map<String, Object> getIssueDialogueDetail(IssueTicketVo reqIssueTicketVo) throws Exception {

		Map<String, Object> result              = new HashMap<>();

		IssueDialogueDetailVo issueDialogueDetailVo     = issueDao.getIssueDialogueDetail(reqIssueTicketVo);
		if(issueDialogueDetailVo == null){
			throwException.statusCode(204);
		}
		Map<String, Object> dataCustomer = ticketIssueService.getIssueDialogueCustomer(reqIssueTicketVo);
		String customerName = ((TicketIssueCustomerDTO) dataCustomer.get("customerInfo")).getFdCustomerName();
		if (null != customerName) {
			issueDialogueDetailVo.setFd_customer_name(customerName);
		}

		result.put("dialogueInfo"   , issueDialogueDetailVo);

		return result;
	}

	@Override
	public void updateIssueTicketWorkflow(IssueTicketVo reqIssueTicketVo) throws Exception {

		int updatedCnt = 0;

		try {
			updatedCnt = issueDao.updateIssueTicketWorkflow(reqIssueTicketVo);
			log.debug("********** updated row cnt : {}", updatedCnt);

		} catch (Exception ex) {
			log.error("********** reqTestVo : {}", reqIssueTicketVo.toString());
			throwException.statusCode(500);
		}

		if(updatedCnt == 0){
			throwException.statusCode(204);
		}

		ticketIssueService.clearAllCacheInTicket();

	}

}
