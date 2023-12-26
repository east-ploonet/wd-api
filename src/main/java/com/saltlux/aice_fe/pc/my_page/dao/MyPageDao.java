package com.saltlux.aice_fe.pc.my_page.dao;


import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.my_page.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface MyPageDao {
    MyPageCompanyVo getCompanyMyPage(PcLoginInfoVo pcLoginInfoVo);

    MyPageCompanyVo getIndividualMyPage(PcLoginInfoVo pcLoginInfoVo);

    List<StatisticItemVo> getStatisticItem(PcLoginInfoVo pcLoginInfoVo);

    void updateCompany(UpdateMyPageVo req);

    void updateCompanyStaff(UpdateMyPageVo req);

    void deleteStatisticMyPage(UpdateMyPageVo req);

    MyPlanVo getPlan(PcLoginInfoVo pcLoginInfoVo);
    
    //MyPlanNewVo getPlanNew(long loginCompanyPk);
    
    MyPlanNewVo getPlanNew(long loginCompanyPk);

    MyPlanVo getNextPlan(PcLoginInfoVo pcLoginInfoVo);

    String getDept(long loginCompanyPk);


    List<MyPageOptionHistoryVo> getOptionHistory(PcLoginInfoVo pcLoginInfoVo);

    List<MyPagePayLogVo> getPayLog(Map<String, Object> paramMap);

    Integer getPayLogCnt(Map<String, Object> paramMap);

    List<PgBillVo> getCredit();
    
    Integer getMyCredit(Map<String, Object> paramMap);




    void updateStaff(UpdateMyPageVo req);
}
