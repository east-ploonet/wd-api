package com.saltlux.aice_fe.pc.dashboard.service;

import com.saltlux.aice_fe.pc.dashboard.vo.DashBoardVo;
import com.saltlux.aice_fe.pc.issue.dto.IssueTicketDTO;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;

import java.util.List;
import java.util.Map;

public interface DashBoardService {

	Map<String, Object> getIssues(DashBoardVo vo)  throws Exception;

}
