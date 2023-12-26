package com.saltlux.aice_fe.pc.issue.dao;

import com.saltlux.aice_fe._baseline.cache.CacheRelations;
import com.saltlux.aice_fe._baseline.cache.RelativeCache;
import com.saltlux.aice_fe.pc.issue.dto.IssueTicketDTO;
import com.saltlux.aice_fe.pc.issue.dto.TicketIssueCustomerDTO;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueDialogueDetailVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueDialogueVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import org.apache.ibatis.annotations.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
//@CacheNamespace(implementation = RelativeCache.class, eviction = RelativeCache.class, flushInterval = 30 * 60 * 1000)
//@CacheRelations(from = { IssueContactDao.class, IssueDialogDao.class })    //When the issue contact, issue dialog is updated, the new cache reference is updated to the ticket issue cache
public interface TicketIssueDao {

	int                     getTicketAllCnt(IssueTicketVo vo);

	List<IssueTicketDTO>    getTickets(IssueTicketVo vo);
	List<IssueDialogueVo>   getTicketDialogueList(IssueTicketVo vo);
	void                    updateTicketDialogueRead(IssueTicketVo vo);
	@Deprecated
	CompanyCustomerVo       getTicketDialogueCustomer(IssueTicketVo vo);

	TicketIssueCustomerDTO  getTicketIssueCustomerByCustomer(IssueTicketVo vo);

	TicketIssueCustomerDTO  getTicketIssueCustomerByStaff(IssueTicketVo vo);
	TicketIssueCustomerDTO  getTicketInfoBasic(IssueTicketVo vo);

	@Deprecated
	IssueDialogueDetailVo   getIssueDialogueDetail(IssueTicketVo vo);

	IssueTicketDTO   		getIssueTicketDetail(IssueTicketVo vo);

	@CacheEvict(value = "tickets", key = "#p0.id")
	int                     updateIssueTicketWorkflow(IssueTicketVo vo);

	int                     updateIssueTicketWorkflowIng(IssueTicketVo vo);

	int updateIssueTicketStatus(IssueTicketVo vo);
	int updateIssueTicketPriority(IssueTicketVo vo);
	int updateIssueTicketManager(IssueTicketVo vo);
	int updateIssueTicketLimitDate(IssueTicketVo vo);
	int updateIssueTicket(IssueTicketVo vo);
	long countTicketIssues(IssueTicketVo vo);

	void deleteIssueTicket(IssueTicketVo vo);

	List<IssueTicketVo> getAllTicketWithoutContact();

	@Select("SELECT * FROM tbl_issue_ticket")
	List<IssueTicketVo> getAll();

	@Delete("DELETE FROM tbl_issue_ticket WHERE pk_issue_ticket IN ( #{ids} )")
	void deleteByIds(@Param("ids") List<Long> ids);
}
