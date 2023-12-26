package com.saltlux.aice_fe.pc.notice.service;

import com.saltlux.aice_fe.pc.notice.vo.NoticeFileVo;
import com.saltlux.aice_fe.pc.notice.vo.NoticeVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface NoticeService {

    Map<String, Object> findNoticeCategory();

    void save(NoticeVo noticeVo);

    Page<NoticeVo> getNotice(Pageable pageable, String query);

//    void registerFiles(Map<String, Object> paramMap, Long fk_notice) throws Exception;

    Map<String, Object> getDetailNotice(Long pk_notice);

    NoticeFileVo getNoticeFile(Long pk_notice_file_id);
}
