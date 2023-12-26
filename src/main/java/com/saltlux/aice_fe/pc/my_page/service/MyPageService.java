package com.saltlux.aice_fe.pc.my_page.service;

import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.my_page.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MyPageService {
    Map<String, Object> getMyPage(PcLoginInfoVo pcLoginInfoVo);

    Map<String, Object> updateMyPage(PcLoginInfoVo pcLoginInfoVo, UpdateMyPageVo req);

    Map<String, Object> getMyplan(PcLoginInfoVo pcLoginInfoVo);
    
    Map<String, Object> updateMyPassword(PcLoginInfoVo pcLoginInfoVo, MyPasswordVo req);

    Map<String, Object> getMyCredit(PcLoginInfoVo pcLoginInfoVo);
    
    Map<String, Object> getOption();

    Map<String, Object> registerOption(PcLoginInfoVo pcLoginInfoVo, List<MyPageOptionVo> req);

    Page<MyPageCreditWalletVo> getCreditWallet(PcLoginInfoVo pcLoginInfoVo, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate,String type);

    Map<String, Object> getOptionHistory(PcLoginInfoVo pcLoginInfoVo);

    Page<MyPagePayLogVo> getPayLog(PcLoginInfoVo pcLoginInfoVo, Pageable pageable, LocalDateTime start, LocalDateTime end);
}
