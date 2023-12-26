package com.saltlux.aice_fe.pc.credit.service.Impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.email.service.EmailService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.credit.dao.CreditDao;
import com.saltlux.aice_fe.pc.credit.service.CreditService;
import com.saltlux.aice_fe.pc.credit.vo.*;
import com.saltlux.aice_fe.pc.user.dao.UserDao;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;
import com.saltlux.aice_fe.pc.payment.response.PaymentApplyResponseVo;
import com.saltlux.aice_fe.pc.payment.response.PaymentSubscribeApplyResponseVo;
import com.saltlux.aice_fe.pc.payment.vo.PgInfoVo;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBody;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBodyNew;
import com.saltlux.aice_fe.pc.payment.vo.SubscribePaymentBody;
import com.saltlux.aice_fe.pc.payment.vo.SuccessPayNewVo;
import com.saltlux.aice_fe.pc.payment.vo.SuccessPayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreditServiceImpl extends BaseServiceImpl implements CreditService {

    @Autowired
    private CreditDao creditDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PloonetApiService ploonetApiService;
    
    @Override
    public Map<String, Object> getPlan(String charge_unit, int charge_term) {
        Map<String, Object> results = new HashMap<>();
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        String myPlan = "";
        if (pcLoginInfoVo != null) {
            //myPlan = creditDao.getMyPlan(pcLoginInfoVo.getLoginCompanyPk());
        }
        List<CreditVo> creditVoList = creditDao.getPlan();
        
        /*
         * 
         * 
         * */
        // 추가 구문
        List<CreditNewVo> creditNewVoListMonth = creditDao.getPlanNewMonth();
        List<CreditNewVo> creditNewVoListYear = creditDao.getPlanNewYear();
        List<ItemResVo> itemVoList = creditDao.getItemList();
        List<ItemResVo> itemPlusVoList = creditDao.getItemPlusList();
        List<CreditNewVo> itemNewResVoList = creditNewVoListMonth.stream().map(item -> {
        	CreditNewVo creditNewItemResVo = new CreditNewVo();
        	creditNewItemResVo.setPp_card_name(item.getPp_card_name());
        	creditNewItemResVo.setCost(item.getCost());
        	creditNewItemResVo.setCredit(item.getCredit());
            return creditNewItemResVo;
        }).collect(Collectors.toList());
        
        String finalMyPlanTest = myPlan;
        creditNewVoListMonth.forEach(item -> {
            if (item.getPp_card_cd().equals(finalMyPlanTest)) {
                //item.setMy_plan("Y");
            }
        });
        
        List<CreditNewVo> itemNewResVoYear = creditNewVoListYear.stream().map(item -> {
        	CreditNewVo creditNewItemResVo = new CreditNewVo();
        	creditNewItemResVo.setPp_card_name(item.getPp_card_name());
        	creditNewItemResVo.setCost(item.getCost());
        	creditNewItemResVo.setCredit(item.getCredit());
            return creditNewItemResVo;
        }).collect(Collectors.toList());
        
        /*
         * 
         * 
         * */
        List<Integer> dc_rate = creditDao.getDc_rate(charge_unit, charge_term);
        List<CreditItemVo> creditItemVoList = creditDao.getPlanService();
        List<CreditItemResVo> itemResVoList = creditItemVoList.stream().map(item -> {
            CreditItemResVo creditItemResVo = new CreditItemResVo();
            creditItemResVo.setFk_service_plan(item.getFk_service_plan());
            creditItemResVo.setDisplay_title(item.getDisp_name());
            creditItemResVo.setScreen_cd(item.getScreen_cd());
            creditItemResVo.setDisp_order(item.getDisp_order());
            if (item.getItem_cnt() == null) {
                creditItemResVo.setDisplay_name("N");
            } else if (item.getItem_cnt().equals("Y")) {
                creditItemResVo.setDisplay_name("Y");
            } else if (item.getItem_cnt().equals("불가")) {
                creditItemResVo.setDisplay_name("불가");
            } else if (item.getItem_cnt().equals("N")) {
                creditItemResVo.setDisplay_name("불가");
            } else {
                creditItemResVo.setDisplay_name(item.getItem_cnt() + item.getDisp_unit_name());
            }
            return creditItemResVo;
        }).collect(Collectors.toList());
        
        Map<String, List<CreditItemResVo>> collect = itemResVoList.stream().collect(Collectors.groupingBy(item -> item.getFk_service_plan()));
        String finalMyPlan = myPlan;
        creditVoList.forEach(item -> {
            if (item.getPk_service_plan().equals(finalMyPlan)) {
                item.setMy_plan("Y");
            }
            if (dc_rate.get(0) > 0 && item.getCharge_amount() > 0) {
                int total = 1;
                if (charge_unit.equals("Y")){
                    total= charge_term * 12;
                }else {
                    total = charge_term;
                }
                int money = item.getCharge_amount() * total;
                int dc_money = money - (money / dc_rate.get(0));
                item.setCharge_amount(money);
                item.setDiscount_amount(dc_money);
            }else {
                item.setDiscount_amount(item.getCharge_amount());
            }
            item.setAvailable_credit(item.getCredit_free() + item.getCredit_main());
            if (collect.get(item.getPk_service_plan()) != null) {
                item.setCreditItemVoList(collect.get(item.getPk_service_plan()));
            }
        });
        
        results.put("itemVoList", itemVoList);
        results.put("itemPlusVoList", itemPlusVoList);
        results.put("creditVoList", creditVoList);
        results.put("creditVoNewListMonth", creditNewVoListMonth);
        results.put("creditVoNewListYear", creditNewVoListYear);
        return results;
    }

    @Override
    public void registerEntry(UserReqBody reqBody, long pkPgPayLog) {
        creditDao.registerEntry(reqBody);
        ploonetApiService.sendBillEntry(reqBody , pkPgPayLog);
        creditDao.registerEntryReserve(reqBody);
    }

    @Override
    public Map<String, Object> getBalance(PcLoginInfoVo pcLoginInfoVo, String pp_card_cd, int charge_term, String charge_unit) {
    	
        Map<String,Object> resultMap= new HashMap<>();
        resultMap.put("charge_term",charge_term);
        resultMap.put("charge_unit",charge_unit);
        int balance = ploonetApiService.getBalance(pcLoginInfoVo);
        //System.out.println(pcLoginInfoVo.getLoginCompanyPk());
        MyNewCreditItemVo myPlanList = creditDao.getMyPlanList(pcLoginInfoVo.getLoginCompanyPk());
        //MyNewCreditItemVo myPlanList = creditDao.getMyPlanList(70);
        MyNewCreditItemVo mySelectPlanList= creditDao.getSelectPlan(pp_card_cd);
        //ServiceDcVo selectPlanDc = creditDao.getSelectPlanDc(charge_term, charge_unit);
        //mySelectPlanList.setDc_rate(selectPlanDc.getDc_rate());
        //mySelectPlanList.setFk_service_plan_dc(selectPlanDc.getPk_service_plan_dc());



        // 내 플랜 활인
//        if (myPlanList.getDc_rate()>0 && myPlanList.getCharge_amount()>0){
//            int money = myPlanList.getCharge_amount();
//            if (charge_unit.equals("Y")){
//                money = money * 12;
//            }
//            int dc_money = money - (money / myPlanList.getDc_rate());
//            myPlanList.setCharge_amount(money);
//            myPlanList.setDiscount_amount(dc_money);
//        }else {
//            int money = myPlanList.getCharge_amount();
//            if (charge_unit.equals("Y")){
//                money = money * 12;
//            }
//            myPlanList.setDiscount_amount(money);
//        }
//        myPlanList.setAvailable_credit(myPlanList.getCredit_free() + myPlanList.getCredit_main());
//
//        //선택 플랜 할인
//        if (mySelectPlanList.getDc_rate()>0 && mySelectPlanList.getCharge_amount()>0){
//            int money = mySelectPlanList.getCharge_amount();
//            if (charge_unit.trim().equals("Y")){
//                money = money * 12;
//            }
//            int dc_money = money - (money / mySelectPlanList.getDc_rate());
//            mySelectPlanList.setCharge_amount(money);
//            mySelectPlanList.setDiscount_amount(dc_money);
//        }else {
//            int money = mySelectPlanList.getCharge_amount();
//            if (charge_unit.trim().equals("Y")){
//                money = money * 12;
//            }
//            mySelectPlanList.setDiscount_amount(money);
//        }
//        mySelectPlanList.setAvailable_credit(mySelectPlanList.getCredit_free() + mySelectPlanList.getCredit_main());

        //잔여 크렛딧
        
        int selectInt = 0;
        int myInt = 0;
        if(mySelectPlanList != null) selectInt = mySelectPlanList.getCredit_policy();
        if(myPlanList != null) myInt = myPlanList.getCredit_policy();
        if (myInt>selectInt){
            resultMap.put("class","down");
        }else {
            resultMap.put("class","upgrade");
        }

        resultMap.put("mySelectPlanList",mySelectPlanList);
        resultMap.put("myPlanList",myPlanList);
        
        System.out.println("mySelectPlanList : "+mySelectPlanList);
        System.out.println("myPlanList : "+myPlanList);
        
        return resultMap;
    }

    @Override
    public SubscribePaymentBody getBatchKey(long loginCompanyPk) {
        SubscribePaymentBody subscribePaymentBody= creditDao.getBatchKey(loginCompanyPk);
        return subscribePaymentBody;
    }

    @Override
    public void registerCard(PgInfoVo pgInfoVo) {
        creditDao.registerPgInfo(pgInfoVo);
    }

    @Override
    public void pgPayInfoLogInsert(long loginCompanyPk) {
        creditDao.pgPayInfoLogInsert(loginCompanyPk);
    }
    
    
    @Override
    public void updateCard(PgInfoVo pgInfoVo, long loginCompanyPk) {
    	
    	Map<String,Object> paramMap = new HashMap<>();
    	
        paramMap.put("pay_key",pgInfoVo.getPay_key());
    	paramMap.put("pay_method",pgInfoVo.getPay_method());
    	paramMap.put("pay_company_cd",pgInfoVo.getPay_company_cd());
    	paramMap.put("pay_name",pgInfoVo.getPay_name());
    	paramMap.put("loginCompanyPk",loginCompanyPk);
    	
    	
        
        creditDao.updateCard(paramMap);
    }

    @Override
    public int myplan(long loginCompanyPk) {
    	int myPlan = creditDao.getMyPlan(loginCompanyPk);
        //int myPlan = creditDao.getMyPlan(70);
        return myPlan;
    }

  //신규 추가 업그레이드, 다운그레이드 대체 함수
    @Override
    public void newPayProsess(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo, String beforeMyPlanName, String planName) {
    	      
        List<String> emailList= new ArrayList<>();
        emailList.add(pcLoginInfoVo.getLoginCompanyStaffId());
        String companyId = pcLoginInfoVo.getLoginCompanyId().replaceAll("(?<=.{2}).","*");
        String[] email = pcLoginInfoVo.getLoginCompanyStaffId().split("@");
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String send = email[0].replaceAll("(?<=.{3}).", "*") + "@" + email[1].replaceAll("(?<=.{2}).", "*");
        Map<String, String> map = new HashMap();
        map.put("companyId",companyId);
        map.put("email", send);
        map.put("date",date);
        map.put("beforePlanName",beforeMyPlanName);
        map.put("planName",planName);
        
        try {
            emailService.sendEmail( emailList, "구독 변경 안내 메세지", "plan.html", map);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //creditDao.deleteReserve(req);
        creditDao.registerBillNew(req);
    }
    
    
    //업그레이드 함수 안씀
    @Override
    public void upgrade(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo) {
    	
        String beforeMyPlanName = creditDao.getMyPlanName(pcLoginInfoVo.getLoginCompanyPk());

        //MyCreditItemVo myPlanList = creditDao.getPlanList(req.getFk_service_plan());
        MyNewCreditItemVo myPlanList = creditDao.getMyPlanList(pcLoginInfoVo.getLoginCompanyPk());

        ServiceDcVo serviceDcVo=creditDao.getPlanDc(req.getFk_service_plan_dc());

        //플랜 이름 입력
        req.setService_plan_name(myPlanList.getPp_card_name());
        // 플랜 기간 입력
        if (serviceDcVo.getCharge_unit().equals("Y")){
            req.setCharge_term(serviceDcVo.getCharge_term() * 12);
        }else{
            req.setCharge_term(serviceDcVo.getCharge_term());
        }

        int sumMoney = myPlanList.getCost();
        if (serviceDcVo.getCharge_unit().equals("Y")){
            int dc_rate = serviceDcVo.getDc_rate();
            sumMoney = sumMoney-(sumMoney /dc_rate);
            sumMoney = sumMoney *12;
        }
        req.setSum_money(sumMoney);
        req.setPk_company(pcLoginInfoVo.getLoginCompanyPk());
        req.setPk_company_staff(pcLoginInfoVo.getLoginCompanyStaffPk());
        req.setUser_type(pcLoginInfoVo.getLoginUserType());

        creditDao.upgradeBeforeMyPlan(pcLoginInfoVo.getLoginCompanyPk());
        creditDao.upgradeMyPlan(req);
        
        
        creditDao.deleteReserve(req);
        creditDao.registerBill(req);

        List<String> emailList= new ArrayList<>();
        emailList.add(pcLoginInfoVo.getLoginCompanyStaffId());
        String companyId = pcLoginInfoVo.getLoginCompanyId().replaceAll("(?<=.{2}).","*");
        String[] email = pcLoginInfoVo.getLoginCompanyStaffId().split("@");
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String send = email[0].replaceAll("(?<=.{3}).", "*") + "@" + email[1].replaceAll("(?<=.{2}).", "*");
        Map<String, String> map = new HashMap();
        map.put("companyId",companyId);
        map.put("email", send);
        map.put("date",date);
        map.put("beforePlanName",beforeMyPlanName);
        map.put("planName",myPlanList.getPp_card_name());

        try {
            emailService.sendEmail( emailList, "구독 변경 안내 메세지", "plan.html", map);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //다운그레이드 함수 안씀
    @Override
    public void downgrade(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo) {
    	/*
    	MyNewCreditItemVo myPlanList = creditDao.getPlanList(req.getFk_service_plan());

        ServiceDcVo serviceDcVo=creditDao.getPlanDc(req.getFk_service_plan_dc());
        req.setPk_company_staff(pcLoginInfoVo.getLoginCompanyStaffPk());
        req.setPk_company(pcLoginInfoVo.getLoginCompanyPk());
        req.setUser_type(pcLoginInfoVo.getLoginUserType());
        //플랜 이름 입력
        req.setService_plan_name(myPlanList.getService_plan_name());
        // 플랜 기간 입력
        if (serviceDcVo.getCharge_unit().equals("Y")){
            req.setCharge_term(serviceDcVo.getCharge_term() * 12);
        }else{
            req.setCharge_term(serviceDcVo.getCharge_term());
        }
        String beforeMyPlanName = creditDao.getMyPlanName(pcLoginInfoVo.getLoginCompanyPk());

        TblPgBillLogVo tblPgBillLogVo = creditDao.getplanDate(pcLoginInfoVo.getLoginCompanyPk());
        LocalDateTime service_dt_to = tblPgBillLogVo.getService_dt_to();
        creditDao.deleteReserve(req);
        req.setService_dt_from(service_dt_to.plusDays(1));
        req.setService_dt_to(req.getService_dt_from().plusMonths(req.getCharge_term()).minusDays(1));
        creditDao.registerDownBill(req);


        String myPlan=creditDao.getNextPlanName(pcLoginInfoVo.getLoginCompanyPk());
        List<String> emailList= new ArrayList<>();
        emailList.add(pcLoginInfoVo.getLoginCompanyStaffId());
        String companyId = pcLoginInfoVo.getLoginCompanyId().replaceAll("(?<=.{2}).","*");
        String[] email = pcLoginInfoVo.getLoginCompanyStaffId().split("@");
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String send = email[0].replaceAll("(?<=.{3}).", "*") + "@" + email[1].replaceAll("(?<=.{2}).", "*");
        Map<String, String> map = new HashMap();
        map.put("companyId",companyId);
        map.put("email", send);
        map.put("date",date);
        map.put("beforePlanName",beforeMyPlanName);
        map.put("planName",myPlan);

        try {
            emailService.sendEmail( emailList, "구독 변경 안내 메세지", "plan.html", map);

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public Map<String, Object> getMyCard(PcLoginInfoVo pcLoginInfoVo) {
        Map<String,Object> result = new HashMap<>();
        String name=creditDao.getMyCard(pcLoginInfoVo);
        result.put("my_card",name);
        return result;
    }

    @Override
    public Map<String, Object> getPayPage(String pp_card_cd, String fk_service_plan_dc, PcLoginInfoVo pcLoginInfoVo) {
        Map<String,Object> result = new HashMap<>();


//        int selectInt =Integer.parseInt(pp_card_cd.trim().substring(8,11)) ;
//        int myInt = creditDao.getMyPlan(pcLoginInfoVo.getLoginCompanyPk());
//        if (myInt>selectInt){
//            result.put("class","down");
//        }else {
//            result.put("class","upgrade");
//        }

//        ServiceDcVo planDc = creditDao.getPlanDc(fk_service_plan_dc);
        MyNewCreditItemVo selectPlan = creditDao.getSelectPlan(pp_card_cd);

        /*
        int discount = 0;
        int chargeAmount = selectPlan.getCharge_amount();
        if (planDc.getCharge_unit().trim().equals("Y")){
            chargeAmount= chargeAmount *12;
        }
        if (planDc.getDc_rate()>0){
            discount= chargeAmount/planDc.getDc_rate();
        }

        int discount_amount =chargeAmount -discount;

        result.put("charge_amount",chargeAmount);
        result.put("discount",discount);
        result.put("discount_amount",discount_amount);
        */
        
        result.put("selectPlan", selectPlan);

        return result;
    }

    @Override
    public SuccessPayVo getSuccessPay(SubscribeBody req) {
        SuccessPayVo successPayVo=creditDao.getSuccessPay(req);
        return successPayVo;
    }
    
    @Override
    public SuccessPayNewVo getSuccessPayNew(SubscribeBodyNew req) {
        SuccessPayNewVo successPayNewVo=creditDao.getSuccessPayNew(req);
        return successPayNewVo;
    }

    @Override
    public void chargeCredit(PcLoginInfoVo pcLoginInfoVo, PaymentApplyResponseVo paymentApplyResponseVo,String order_method) {
 	
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("userType",pcLoginInfoVo.getLoginUserType());
        paramMap.put("companyPk",pcLoginInfoVo.getLoginCompanyPk());
        paramMap.put("companyStaffPk",pcLoginInfoVo.getLoginCompanyStaffPk());
        paramMap.put("tno",paymentApplyResponseVo.getTno());
        paramMap.put("order_method",order_method);
        paramMap.put("card_cd",paymentApplyResponseVo.getCard_cd());
        paramMap.put("sum_money",paymentApplyResponseVo.getAmount());
        paramMap.put("order_company_name",paymentApplyResponseVo.getCard_name());
        creditDao.registerChargeCredit(paramMap);
        ploonetApiService.sendChargeCredit(pcLoginInfoVo, Integer.parseInt(paymentApplyResponseVo.getAmount()));
    }

    @Override
    public int getPlanMoney(String pp_card_cd, String fk_service_plan) {
    	MyNewCreditItemVo selectPlan = creditDao.getSelectPlan(pp_card_cd);
        int dc_amount = 0;
        return dc_amount;
    }

	@Override
	public PgPayInfo getPgPayInfo(long loginCompanyPk) {
		PgPayInfo getPgPayInfo = creditDao.getPgPayInfo(loginCompanyPk);
		return getPgPayInfo;
	}
	
	@Override
	public PgPayLog getLastPgPayLog(long loginCompanyPk) {
		return creditDao.getLastPgPayLog(loginCompanyPk);
	}
	
	
	
	@Override
	public long registerPgPayLog(
					PaymentApplyResponseVo paymentApplyResponseVo,
					long loginCompanyPk,
					long loginCompanyStaffPk,
					String billStatus,
					String tran_cd,
					MyNewCreditItemVo mySelectPlanList
					) {
		
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("loginCompanyPk",loginCompanyPk);
		paramMap.put("pay_name", mySelectPlanList.getPp_card_name()); // mySelectPlanList 에 있음
		paramMap.put("pay_method", "CARD"); // ??????????? db 예시에 CARD 라고 나옴
		paramMap.put("pay_company_cd",paymentApplyResponseVo.getCard_cd()); // pay_company 가 카드라고 생각했을 때?
		paramMap.put("pay_company_name", paymentApplyResponseVo.getCard_name()); // db 예시보면 KB국민카드 이렇게 있음
		paramMap.put("pay_key", null); // payKey 가 tno 같은데요??
		paramMap.put("tran_cd", paymentApplyResponseVo.getTno()); // req 에 담겨있음  
		paramMap.put("pg_err_cd", null); // ?????????
		paramMap.put("pg_err_desc", null); //?????????
		paramMap.put("item_cd", mySelectPlanList.getPp_card_cd());// mySelectList에 있음
		paramMap.put("pk_company_staff",loginCompanyStaffPk);
		paramMap.put("bill_status", billStatus);
		creditDao.registerPgPayLog(paramMap);
		return (long) paramMap.get("pk_pg_pay_log");
	}
	
	@Override
	public long registerPgPayLogSub(PaymentSubscribeApplyResponseVo paymentSubscribeApplyResponseVo, long loginCompanyPk, long loginCompanyStaffPk, String billStatus, String tran_cd, MyNewCreditItemVo mySelectPlanList, String payKey) {
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("loginCompanyPk",loginCompanyPk);
		paramMap.put("pay_name", mySelectPlanList.getPp_card_name()); // mySelectPlanList 에 있음
		paramMap.put("pay_method", "CARD"); // ??????????? db 예시에 CARD 라고 나옴
		paramMap.put("pay_company_cd",paymentSubscribeApplyResponseVo.getCard_cd()); // pay_company 가 카드라고 생각했을 때?
		paramMap.put("pay_company_name", paymentSubscribeApplyResponseVo.getCard_name()); // db 예시보면 KB국민카드 이렇게 있음
		paramMap.put("pay_key", payKey); // payKey 가 tno 같은데요??
		paramMap.put("tran_cd", paymentSubscribeApplyResponseVo.getTno()); // req 에 담겨있음  
		paramMap.put("pg_err_cd", null); // ?????????
		paramMap.put("pg_err_desc", null); //?????????
		paramMap.put("item_cd", mySelectPlanList.getPp_card_cd());// mySelectList에 있음
		paramMap.put("pk_company_staff",loginCompanyStaffPk);
		paramMap.put("bill_status", billStatus);
		creditDao.registerPgPayLog(paramMap);
		return (long) paramMap.get("pk_pg_pay_log");
	}
	
	@Override
	public long registerCancelPgPayLog(Map<String,Object> paramMap) {
		creditDao.registerPgPayLog(paramMap);
		return (long) paramMap.get("pk_pg_pay_log");
	}
	
	
	@Override
	public long selectRegisterPgPayLog(long loginCompanyPk, String pk_pp_card) {
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("loginCompanyPk",loginCompanyPk);
		paramMap.put("pk_pp_card",pk_pp_card);
		creditDao.selectRegisterPgPayLog(paramMap);
		return (long) paramMap.get("pk_pg_pay_log");
	}

	@Override
	public void ppcardMerge(PcLoginInfoVo pcLoginInfoVo, String pk_pp_card) {
		// TODO Auto-generated method stub
		
	}
}
