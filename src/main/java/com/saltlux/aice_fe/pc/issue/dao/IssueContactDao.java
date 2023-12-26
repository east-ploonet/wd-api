package com.saltlux.aice_fe.pc.issue.dao;

import com.saltlux.aice_fe._baseline.cache.RelativeCache;
import com.saltlux.aice_fe.pc.issue.vo.IssueContactVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
//@CacheNamespace(implementation = RelativeCache.class, eviction = RelativeCache.class, flushInterval = 30 * 60 * 1000)
public interface IssueContactDao {

    int updateContactChannelCode(IssueContactVo issueContactVo);
    List<IssueContactVo> getContactsByTicket(IssueTicketVo issueTicketVo);
    List<IssueContactVo> getContactNotMapTicket();
    void deleteById(IssueContactVo issueContactVo);

    @Delete("DELETE FROM tbl_issue_contact WHERE pk_issue_contact IN ( #{ids} )")
    void deleteByIds(@Param("ids") List<String> ids);
    void deleteByTicketId(IssueTicketVo issueTicketVo);
}
