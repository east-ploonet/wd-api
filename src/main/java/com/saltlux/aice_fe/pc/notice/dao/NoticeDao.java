package com.saltlux.aice_fe.pc.notice.dao;

import com.saltlux.aice_fe.pc.notice.vo.NoticeDetailVo;
import com.saltlux.aice_fe.pc.notice.vo.NoticeFileVo;
import com.saltlux.aice_fe.pc.notice.vo.NoticeVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface NoticeDao {

    List<NoticeVo> findNoticeCategory();

    void save(NoticeVo noticeVo);

    void registerNoticeFile(Map<String, Object> paramMap);

    List<NoticeVo> getNotice(Map<String, Object> paramMap);

    Integer getNoticeCnt(Map<String, Object> paramMap);

    List<NoticeFileVo> getNoticeFiles(List<Long> noticeIdList);

    List<NoticeFileVo> getDetailNoticeFiles(Long pk_notice);

    void deleteNoticeFiles(NoticeVo noticeVo);

    NoticeDetailVo getDetailNotice(Long pk_notice);

    List<Long> getBeforeFrontId(Long pk_notice);

    NoticeFileVo getNoticeFileById(Long pk_notice_file_id);

    List<NoticeVo> getNoticeAdmin(Map<String, Object> paramMap);

    Integer getNoticeAdminCnt(Map<String, Object> paramMap);

    NoticeVo getDetailNoticeAdmin(Long pk_notice);

    void deleteNotice(List<Long> questionIdList);

    void updateNotice(NoticeVo noticeVo);

    void updateViews(Long pk_notice);
}
