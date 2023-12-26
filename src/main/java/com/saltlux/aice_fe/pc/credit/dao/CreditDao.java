package com.saltlux.aice_fe.pc.credit.dao;

import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.credit.vo.*;
import com.saltlux.aice_fe.pc.my_page.vo.MyPageCreditWalletVo;
import com.saltlux.aice_fe.pc.my_page.vo.MyPageOptionPostVo;
import com.saltlux.aice_fe.pc.my_page.vo.MyPageOptionVo;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;
import com.saltlux.aice_fe.pc.payment.vo.PgInfoVo;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBody;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBodyNew;
import com.saltlux.aice_fe.pc.payment.vo.SubscribePaymentBody;
import com.saltlux.aice_fe.pc.payment.vo.SuccessPayNewVo;
import com.saltlux.aice_fe.pc.payment.vo.SuccessPayVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CreditDao {
    List<CreditVo> getPlan();
    
    List<CreditNewVo> getPlanNewMonth();
    
    List<CreditNewVo> getPlanNewYear();
    
    List<ItemResVo> getItemList();

    List<ItemResVo> getItemPlusList();

    List<CreditItemVo> getPlanService();
    
    List<CreditItemVo> getPlanSelectService(String fk_service_plan);

    List<Integer> getDc_rate(String charge_unit, int charge_term);

    void ppcardMerge(PcLoginInfoVo pcLoginInfoVo, String pk_pp_card);
    
    PgPayLog getLastPgPayLog(long loginCompanyPk);
    
    long registerPgPayLog(Map<String,Object> paramMap);
    
    long selectRegisterPgPayLog(Map<String,Object> paramMap);
    
    
    void registerEntry(UserReqBody reqBody);

    void registerEntryReserve(UserReqBody reqBody);

    int getMyPlan(long loginCompanyPk);

    MyNewCreditItemVo getMyPlanList(long loginCompanyPk);

    MyNewCreditItemVo getLastSelectPlan(long loginCompanyPk);
    
    MyNewCreditItemVo getSelectPlan(String pp_card_cd);
    
    PgPayInfo getPgPayInfo(long loginCompanyPk);

    ServiceDcVo getSelectPlanDc(int charge_term, String charge_unit);

    SubscribePaymentBody getBatchKey(long loginCompanyPk);

    void pgPayInfoLogInsert(long loginCompanyPk);
    
    void registerPgInfo(PgInfoVo pgInfoVo);

    void updateCard(Map<String,Object> paramMap);

    void upgradeBeforeMyPlan(long loginCompanyPk);


    MyNewCreditItemVo getPlanList(String fk_service_plan);



    ServiceDcVo getPlanDc(String fk_service_plan_dc);

    void upgradeMyPlan(SubscribeBody req);

    void deleteReserve(SubscribeBody req);

    void registerBill(SubscribeBody req);

    void registerBillNew(SubscribeBody req);

    String getMyCard(PcLoginInfoVo pcLoginInfoVo);


    TblPgBillLogVo getplanDate(long loginCompanyPk);

    void registerDownBill(SubscribeBody req);

    String getMyPlanName(long loginCompanyPk);

    SuccessPayVo getSuccessPay(SubscribeBody req);
    
    SuccessPayNewVo getSuccessPayNew(SubscribeBodyNew req);

    List<MyPageOptionPostVo> getOptionCredit(List<MyPageOptionVo> req);

    void registerChargeCredit(Map<String,Object> paramMap);

    List<MyPageCreditWalletVo> getWallet(Map<String, Object> paramMap);

    Integer getWalletCnt(Map<String, Object> paramMap);

    String getNextPlanName(long loginCompanyPk);

    List<OptionVo> getOptionPI025(long loginCompanyPk);

    List<OptionVo> getOptionPI026(long loginCompanyPk);
}
