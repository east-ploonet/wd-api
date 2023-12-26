package com.saltlux.aice_fe.pc.dashboard.service.Impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe.pc.dashboard.dao.DashBoardDao;
import com.saltlux.aice_fe.pc.dashboard.dto.DashBoardDTO;
import com.saltlux.aice_fe.pc.dashboard.service.DashBoardService;
import com.saltlux.aice_fe.pc.dashboard.vo.DashBoardVo;
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
public class DashBoardServiceImpl extends BaseServiceImpl implements DashBoardService {

    private final List<String> statusShowCount = Arrays.asList("A1301", "A1302", "A1304", "A1305");

    @Autowired
    private DashBoardDao dashBoardDao;

    @Autowired
    private CodeDao codeDao;

    @Override
    public Map<String, Object> getIssues(DashBoardVo reqDashBoardVo) throws Exception {
//		reqIssueTicketVo.setFkAssignStaffIds(getFkAssignStaffByUser(reqIssueTicketVo.getFk_assign_staff()));
        log.info("request get Ticket List = {} ", reqDashBoardVo.getSearch().toString());
        String statusTemp = reqDashBoardVo.getSearch().getSearchStatus();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> listMap = new ArrayList<>();
        long totalCnt = dashBoardDao.countDashBoard(reqDashBoardVo);
        System.out.println();
        List<CodeVo> listCode = codeDao.selectCodeList(new CodeVo("A1300"));
        List<CodeVo> newListCode = listCode.stream().filter(item -> !item.getPk_code().equals("A1303") && !item.getPk_code().equals("A1306")).collect(Collectors.toList());
        List<CodeDTO> codeDTOS = new ArrayList<>();
        for (CodeVo codeVo : newListCode) {
            CodeDTO codeDTO = new CodeDTO(codeVo);
            reqDashBoardVo.getSearch().setSearchStatus(codeVo.getPk_code());
            codeDTO.setTotalStatusTicketIssue(dashBoardDao.countDashBoard(reqDashBoardVo));
            codeDTOS.add(codeDTO);
        }
        // set search status again
        reqDashBoardVo.getSearch().setSearchStatus(statusTemp);
        //총 게시물수
        result.put("totalCnt", totalCnt);
        result.put("workflowCnt", codeDTOS);

        if (totalCnt > 0) {
            List<DashBoardDTO> listDashBoardVo = dashBoardDao.getTickets(reqDashBoardVo);

            if (listDashBoardVo == null) {

                throwException.statusCode(204);

            } else {

                for (DashBoardDTO dashBoardVo : listDashBoardVo) {

                    Map<String, Object> mapAdd = new HashMap<>();

                    mapAdd.put("pkIssueTicket", Long.toString(dashBoardVo.getPkIssueTicket()));
                    mapAdd.put("fdTicketTitle", dashBoardVo.getFdTicketTitle());
                    mapAdd.put("fdContactChannelName", dashBoardVo.getFdContactChannelName());
                    mapAdd.put("fdContactChannelCode", dashBoardVo.getPkCode());
                    mapAdd.put("cntDialog", dashBoardVo.getCntDialog());
                    mapAdd.put("cntContact", dashBoardVo.getCntContact());
                    mapAdd.put("fdCustomerName", dashBoardVo.getFdCustomerName());
                    mapAdd.put("fdStaffName", dashBoardVo.getFdStaffName());
                    mapAdd.put("fdTicketStatusCodeName", dashBoardVo.getFdTicketStatusCodeName());
                    mapAdd.put("writerName", dashBoardVo.getWriterName());
                    mapAdd.put("registeredDate", dashBoardVo.getRegisteredDate());
                    mapAdd.put("priority", dashBoardVo.getPriority());
                    mapAdd.put("fkPriority", dashBoardVo.getFk_priority());
                    mapAdd.put("fdDueDate", dashBoardVo.getFdDueDate());
                    mapAdd.put("aiName", dashBoardVo.getAiName());
                    mapAdd.put("fdWorkFlowCode", dashBoardVo.getWorkFlowCode());
                    mapAdd.put("issue_ticket_tags", dashBoardVo.getIssue_ticket_tags());
                    mapAdd.put("message", dashBoardVo.getFd_message());

                    listMap.add(mapAdd);
                }
            }
        }
        result.put("list", listMap);

        return result;
    }

}
