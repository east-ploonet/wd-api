package com.saltlux.aice_fe.pc.issue.dao;

import com.saltlux.aice_fe._baseline.cache.RelativeCache;
import com.saltlux.aice_fe.pc.issue.vo.IssueContactVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueDialogueVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
//@CacheNamespace(implementation = RelativeCache.class, eviction = RelativeCache.class, flushInterval = 30 * 60 * 1000)
public interface IssueDialogDao {

    List<IssueDialogueVo> getAllDialogWithoutContact();
    void deleteById(IssueDialogueVo issueDialogueVo);
    void deleteByContactId(IssueContactVo issueContactVo);

    @Delete("DELETE FROM tbl_issue_dialogue WHERE pk_issue_dialogue IN ( #{ids} )")
    void deleteByIds(@Param("ids") List<Long> ids);
}
