package com.saltlux.aice_fe.pc.issue.service.Impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe.pc.issue.dao.IssueContactDao;
import com.saltlux.aice_fe.pc.issue.dao.IssueDialogDao;
import com.saltlux.aice_fe.pc.issue.dao.TicketIssueDao;
import com.saltlux.aice_fe.pc.issue.dto.IssueTicketDTO;
import com.saltlux.aice_fe.pc.issue.dto.TicketIssueCustomerDTO;
import com.saltlux.aice_fe.pc.issue.service.TicketIssueService;
import com.saltlux.aice_fe.pc.issue.vo.IssueContactVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueDialogueDetailVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueDialogueVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import com.saltlux.aice_fe.commonCode.dao.CodeDao;
import com.saltlux.aice_fe.commonCode.dto.CodeDTO;
import com.saltlux.aice_fe.commonCode.vo.CodeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TicketIssueServiceImpl extends BaseServiceImpl implements TicketIssueService {

    private final List<String> statusShowCount = Arrays.asList("A1301", "A1302", "A1304", "A1305");

    @Autowired
    private TicketIssueDao ticketIssueDao;

    @Autowired
    private CodeDao codeDao;

    @Autowired
    private IssueContactDao issueContactDao;

    @Autowired
    private IssueDialogDao issueDialogDao;

    @Override
    public Map<String, Object> getIssues(IssueTicketVo reqIssueTicketVo) throws Exception {
//		reqIssueTicketVo.setFkAssignStaffIds(getFkAssignStaffByUser(reqIssueTicketVo.getFk_assign_staff()));
        log.info("request get Ticket List = {} ", reqIssueTicketVo.getSearch().toString());
        String statusTemp = reqIssueTicketVo.getSearch().getSearchStatus();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> listMap = new ArrayList<>();
        long totalCnt = ticketIssueDao.countTicketIssues(reqIssueTicketVo);
        List<CodeVo> listCode = codeDao.selectCodeList(new CodeVo("A1300"));
        List<CodeVo> newListCode = listCode.stream().filter(item -> !item.getPk_code().equals("A1303") && !item.getPk_code().equals("A1306") && !item.getPk_code().equals("A1307")).collect(Collectors.toList());
        List<CodeDTO> codeDTOS = new ArrayList<>();
        for (CodeVo codeVo : newListCode) {
            CodeDTO codeDTO = new CodeDTO(codeVo);
            reqIssueTicketVo.getSearch().setSearchStatus(codeVo.getPk_code());
            codeDTO.setTotalStatusTicketIssue(ticketIssueDao.countTicketIssues(reqIssueTicketVo));
            codeDTOS.add(codeDTO);
        }
        // set search status again
        reqIssueTicketVo.getSearch().setSearchStatus(statusTemp);
        //총 게시물수
        result.put("totalCnt", totalCnt);
        result.put("workflowCnt", codeDTOS);

        if (totalCnt > 0) {
            List<IssueTicketDTO> listIssueTicketVo = ticketIssueDao.getTickets(reqIssueTicketVo);

            if (listIssueTicketVo == null) {

                throwException.statusCode(204);

            } else {

                for (IssueTicketDTO issueTicketVo : listIssueTicketVo) {

                    Map<String, Object> mapAdd = new HashMap<>();

                    mapAdd.put("pkIssueTicket", Long.toString(issueTicketVo.getPkIssueTicket()));
                    mapAdd.put("fdTicketTitle", issueTicketVo.getFdTicketTitle());
                    mapAdd.put("fdContactChannelName", issueTicketVo.getFdContactChannelName());
                    mapAdd.put("fdContactChannelCode", issueTicketVo.getPkCode());
                    mapAdd.put("cntDialog", issueTicketVo.getCntDialog());
                    mapAdd.put("cntContact", issueTicketVo.getCntContact());
                    mapAdd.put("fdCustomerName", issueTicketVo.getFdCustomerName());
                    mapAdd.put("fdStaffName", issueTicketVo.getFdStaffName());
                    mapAdd.put("fdTicketStatusCodeName", issueTicketVo.getFdTicketStatusCodeName());
                    mapAdd.put("writerName", issueTicketVo.getWriterName());
                    mapAdd.put("registeredDate", issueTicketVo.getRegisteredDate());
                    mapAdd.put("priority", issueTicketVo.getPriority());
                    mapAdd.put("fkPriority", issueTicketVo.getFk_priority());
                    mapAdd.put("fdDueDate", issueTicketVo.getFdDueDate());
                    mapAdd.put("aiName", issueTicketVo.getAiName());
                    mapAdd.put("fdWorkFlowCode", issueTicketVo.getWorkFlowCode());
                    mapAdd.put("issue_ticket_tags", issueTicketVo.getIssue_ticket_tags());
                    mapAdd.put("message", issueTicketVo.getFd_message());

                    listMap.add(mapAdd);
                }
            }
        }
        result.put("list", listMap);

        return result;
    }

    @Override
    public List<IssueTicketDTO> getAllIssues(IssueTicketVo vo) throws Exception {
        return ticketIssueDao.getTickets(vo);
    }

    // 일감 대화내역
    @Override
    public Map<String, Object> getIssueDialogueList(IssueTicketVo reqIssueTicketVo) throws Exception {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> listMap = new ArrayList<>();

        List<IssueDialogueVo> listIssueDialogueVo = ticketIssueDao.getTicketDialogueList(reqIssueTicketVo);


        if (listIssueDialogueVo == null) {
            throwException.statusCode(204);

        } else {

            for (IssueDialogueVo issueDialogueVo : listIssueDialogueVo) {

                Map<String, Object> mapAdd = new HashMap<>();
                if (!StringUtils.isNotBlank(issueDialogueVo.getFd_message())) {
                    continue;
                }
                mapAdd.put("pkIssueDialogue", Long.toString(issueDialogueVo.getPk_issue_dialogue()));
                mapAdd.put("fkIssueContact", issueDialogueVo.getFk_issue_contact());
                mapAdd.put("fkCompanyStaff", issueDialogueVo.getFk_company_staff());
                mapAdd.put("fkCompanyCustomer", issueDialogueVo.getFk_company_customer());
                mapAdd.put("customerUid", issueDialogueVo.getFd_customer_uid());
                mapAdd.put("dialogueStatusCode", issueDialogueVo.getFd_dialogue_status_code());
                mapAdd.put("message", issueDialogueVo.getFd_message());
                mapAdd.put("regDate", FormatUtils.dateToStringCustomize(issueDialogueVo.getFd_regdate(), "yyyy/MM/dd HH:mm:ss"));
                mapAdd.put("dialogueMessageCode", issueDialogueVo.getFd_dialogue_message_code());
                mapAdd.put("readYn", issueDialogueVo.getFd_read_yn());
                mapAdd.put("contactChannelCode", issueDialogueVo.getFd_contact_channel_code());

                mapAdd.put("customerName", issueDialogueVo.getFd_customer_name());
                mapAdd.put("staffAiUid", issueDialogueVo.getFd_staff_ai_uid());
                if (issueDialogueVo.getFd_dialogue_message_code().equalsIgnoreCase("B1302")) {
                    mapAdd.put("isBot", true);
                } else {
                    mapAdd.put("isBot", false);
                }
                listMap.add(mapAdd);
            }

            try {
                // 목록 바인딩까지 됐으면 읽음여부 Y 처리

                ticketIssueDao.updateTicketDialogueRead(reqIssueTicketVo);

                // 현재 일감이 시작 상태이면 진행중으로 업데이트
//                ticketIssueDao.updateIssueTicketWorkflowIng(reqIssueTicketVo);

            } catch (Exception ex) {
                log.error("********** updateTicketDialogueRead() ********** {}", ex.toString());
            }
        }

        result.put("list", listMap);

        return result;
    }

    @Override
    public Map<String, Object> getStatusIssueTicket(IssueTicketVo issueTicketVo, List<String> listStatus) throws Exception {
        List<CodeVo> listCode = codeDao.selectCodeList(new CodeVo("A1300"));
        List<CodeDTO> codeDTOS = new ArrayList<>();

        for (CodeVo codeVo : listCode) {
            CodeDTO codeDTO = new CodeDTO(codeVo);
            issueTicketVo.getSearch().setSearchStatus(codeVo.getPk_code());
            long countByStatus = 0;
            if (listStatus.size() > 0) {
                if (listStatus.contains(codeVo.getPk_code())) {
                    countByStatus = ticketIssueDao.countTicketIssues(issueTicketVo);
                }
            } else {
                if (statusShowCount.contains(codeVo.getPk_code())) {
                    countByStatus = ticketIssueDao.countTicketIssues(issueTicketVo);
                }
            }
            codeDTO.setTotalStatusTicketIssue(countByStatus);
            codeDTOS.add(codeDTO);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", codeDTOS);
        return result;
    }

    @Override
    public Map<String, Object> getIssueDialogueCustomer(IssueTicketVo reqIssueTicketVo) throws Exception {

        Map<String, Object> result = new HashMap<>();

        TicketIssueCustomerDTO companyCustomerVo = ticketIssueDao.getTicketIssueCustomerByCustomer(reqIssueTicketVo);
        if (companyCustomerVo == null) {
            companyCustomerVo = ticketIssueDao.getTicketIssueCustomerByStaff(reqIssueTicketVo);
            if (companyCustomerVo != null) {
                companyCustomerVo.setCustomerIsStaff(true);
            }
        }
        if (companyCustomerVo == null) {
            companyCustomerVo = ticketIssueDao.getTicketInfoBasic(reqIssueTicketVo);
        }

        result.put("customerInfo", companyCustomerVo);

        return result;
    }

    @Override
    public Map<String, Object> getIssueDialogueDetail(IssueTicketVo reqIssueTicketVo) throws Exception {

        Map<String, Object> result = new HashMap<>();

        IssueDialogueDetailVo issueDialogueDetailVo = ticketIssueDao.getIssueDialogueDetail(reqIssueTicketVo);
        if (issueDialogueDetailVo == null) {
            throwException.statusCode(204);
        }

        result.put("dialogueInfo", issueDialogueDetailVo);

        return result;
    }

    @Override
    public Map<String, Object> getIssueTicketDetail(IssueTicketVo vo) throws Exception {
        log.info("request get Ticket Detail = {} ", vo.toString());
        Map<String, Object> result = new HashMap<>();

        IssueTicketDTO issueDialogueDetailVo = ticketIssueDao.getIssueTicketDetail(vo);
        if (null == issueDialogueDetailVo) {
            throwException.statusCode(204);
            return null;
        }
        Map<String, Object> dataCustomer = getIssueDialogueCustomer(vo);
        String customerName = ((TicketIssueCustomerDTO) dataCustomer.get("customerInfo")).getFdCustomerName();
        if (null != customerName) {
            issueDialogueDetailVo.setFdCustomerName(customerName);
        }
        result.put("dialogueInfo", issueDialogueDetailVo);

        return result;
    }

    @Override
    public void updateIssueTicketWorkflow(IssueTicketVo reqIssueTicketVo) throws Exception {

        int updatedCnt = 0;

        try {
            updatedCnt = ticketIssueDao.updateIssueTicketWorkflow(reqIssueTicketVo);
            log.debug("********** updated row cnt : {}", updatedCnt);

        } catch (Exception ex) {
            log.error("********** reqTestVo : {}", reqIssueTicketVo.toString());
            throwException.statusCode(500);
        }

        if (updatedCnt == 0) {
            throwException.statusCode(204);
        }
        clearAllCacheInTicket();
    }

    @Override
    public void updateIssueTicketsStatus(List<IssueTicketVo> vo) throws Exception {
        for (IssueTicketVo issueTicketVo : vo) {
            ticketIssueDao.updateIssueTicketStatus(issueTicketVo);
            log.debug("********** updated row : {}", issueTicketVo);
        }
        clearAllCacheInTicket();
    }

    @Override
    public void updateIssueTicketStatus(IssueTicketVo vo) throws Exception {
        ticketIssueDao.updateIssueTicketStatus(vo);
        clearAllCacheInTicket();
    }

    @Override
    public void updateIssueTicketPriority(IssueTicketVo vo) throws Exception {
        ticketIssueDao.updateIssueTicketPriority(vo);
        clearAllCacheInTicket();
    }

    @Override
    public void updateIssueTicketManager(IssueTicketVo vo) throws Exception {
        ticketIssueDao.updateIssueTicketManager(vo);
        clearAllCacheInTicket();
    }

    @Override
    public void deleteIssueTickets(List<IssueTicketVo> vo) throws Exception {
        for (IssueTicketVo issueTicketVo : vo) {
            ticketIssueDao.deleteIssueTicket(issueTicketVo);
            List<IssueContactVo> issueContactVos = issueContactDao.getContactsByTicket(issueTicketVo);
            for (IssueContactVo issueContactVo : issueContactVos) {
                issueDialogDao.deleteByContactId(issueContactVo);
            }
            issueContactDao.deleteByTicketId(issueTicketVo);
        }
    }

    @Override
    public void updateTicketLimitDate(IssueTicketVo vo) throws Exception {
        ticketIssueDao.updateIssueTicketLimitDate(vo);
        clearAllCacheInTicket();
    }

    @Override
    public void updateIssueTicket(IssueTicketDTO vo) throws Exception {
        IssueTicketVo issueTicketVo = new IssueTicketVo(vo);
        ticketIssueDao.updateIssueTicket(issueTicketVo);
        clearAllCacheInTicket();
    }

    @Override
    public void deleteDataGarbageTicket() throws Exception {
        // get all issue ticket has no contact
        List<IssueTicketVo> ticketWithoutContact = ticketIssueDao.getAllTicketWithoutContact();
        ticketIssueDao.deleteByIds(ticketWithoutContact.stream().map(IssueTicketVo::getPk_issue_ticket).collect(Collectors.toList()));
        List<IssueContactVo> issueContactVosWithoutTicket = issueContactDao.getContactNotMapTicket();
        issueContactVosWithoutTicket.forEach(item -> {
            issueContactDao.deleteById(item);
        });
        List<IssueDialogueVo> issueDialogueVos = issueDialogDao.getAllDialogWithoutContact();
        List<Long> issueDialogueIds = issueDialogueVos.stream().map(IssueDialogueVo::getPk_issue_dialogue).collect(Collectors.toList());
        issueDialogDao.deleteByIds(issueDialogueIds);
    }

    @Override
    public void clearAllCacheInTicket() throws Exception {
//        Configuration config = sqlSessionFactory.getConfiguration();
//        Cache cacheViewTicket = config.getCache("com.saltlux.aice_fe.pc.issue.dao.ViewTicketIssueDao");
//        if(cacheViewTicket != null){
//            cacheViewTicket.clear();
//        }
//        Cache cache = config.getCache("com.saltlux.aice_fe.pc.issue.dao.TicketIssueDao");
//        if(cache != null){
//            cache.clear();
//        }
    }
}
