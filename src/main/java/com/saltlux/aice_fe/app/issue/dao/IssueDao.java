package com.saltlux.aice_fe.app.issue.dao;

import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueDialogueDetailVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueDialogueVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface IssueDao {

	int                     getTicketAllCnt(IssueTicketVo vo);
	Map<String, Long>       getTicketWorkflowCnt(IssueTicketVo vo);
	List<IssueTicketVo>     getTicketList(IssueTicketVo vo);
	List<IssueDialogueVo>   getTicketDialogueList(IssueTicketVo vo);
	void                    updateTicketDialogueRead(IssueTicketVo vo);
	CompanyCustomerVo getTicketDialogueCustomer(IssueTicketVo vo);
	IssueDialogueDetailVo getIssueDialogueDetail(IssueTicketVo vo);

	int                     updateIssueTicketWorkflow(IssueTicketVo vo);

	int                     updateIssueTicketWorkflowIng(IssueTicketVo vo);

}
