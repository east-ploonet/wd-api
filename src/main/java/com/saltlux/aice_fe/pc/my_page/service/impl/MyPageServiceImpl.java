package com.saltlux.aice_fe.pc.my_page.service.impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.credit.dao.CreditDao;
import com.saltlux.aice_fe.pc.credit.vo.CreditItemResVo;
import com.saltlux.aice_fe.pc.credit.vo.CreditItemVo;
import com.saltlux.aice_fe.pc.credit.vo.MyNewCreditItemVo;
import com.saltlux.aice_fe.pc.my_page.dao.MyPageDao;
import com.saltlux.aice_fe.pc.my_page.service.MyPageService;
import com.saltlux.aice_fe.pc.my_page.vo.*;
import com.saltlux.aice_fe.pc.user.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Console;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MyPageServiceImpl extends BaseServiceImpl implements MyPageService {

    @Autowired
    private MyPageDao myPageDao;

    @Autowired
    private CreditDao creditDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    ThrowException throwException;

    @Autowired
    private PloonetApiService ploonetApiService;

    @Override
    public Map<String, Object> getMyPage(PcLoginInfoVo pcLoginInfoVo) {
        Map<String, Object> result = new HashMap<>();
        if (pcLoginInfoVo.getLoginUserType().equals("B2001")) {
            MyPageCompanyVo companyMyPage = myPageDao.getCompanyMyPage(pcLoginInfoVo);
            String dept = myPageDao.getDept(pcLoginInfoVo.getLoginCompanyPk());
            if (dept != null) {
                companyMyPage.setFd_dept_name(dept);
            }
            if (StringUtils.hasText(companyMyPage.getFd_company_logo_file_path())) {
                companyMyPage.setFd_company_logo_file_path(pathBrowserStorage + companyMyPage.getFd_company_logo_file_path());
            } else {
                companyMyPage.setFd_company_logo_file_path(null);
            }
            result.put("mypage", companyMyPage);
        } else {
            MyPageCompanyVo myPageCompanyStaffVo = myPageDao.getIndividualMyPage(pcLoginInfoVo);
            if (StringUtils.hasText(myPageCompanyStaffVo.getFd_company_logo_file_path())) {
                myPageCompanyStaffVo.setFd_company_logo_file_path(pathBrowserStorage + myPageCompanyStaffVo.getFd_company_logo_file_path());
            } else {
                myPageCompanyStaffVo.setFd_company_logo_file_path(null);
            }
            List<StatisticItemVo> statisticItemVo = myPageDao.getStatisticItem(pcLoginInfoVo);
            myPageCompanyStaffVo.setStatisticItemVoList(statisticItemVo);
            result.put("mypage", myPageCompanyStaffVo);
        }
        return result;
    }

    @Override
    public Map<String, Object> updateMyPage(PcLoginInfoVo pcLoginInfoVo, UpdateMyPageVo req) {
        Map<String, Object> result = new HashMap<>();
        req.setPk_company(pcLoginInfoVo.getLoginCompanyPk());

        if (pcLoginInfoVo.getLoginUserType().equals("B2001")) {
            if (req.getFd_company_logo_file_path() != null && req.getFd_company_logo_file_path().startsWith("/storage")) {
                String path = req.getFd_company_logo_file_path().replaceAll("/storage", "");
                req.setFd_company_logo_file_path(path);
            }
            if (req.getFd_biz_license_file_path() != null && req.getFd_biz_license_file_path().startsWith("/storage")) {
                String path = req.getFd_biz_license_file_path().replaceAll("/storage", "");
                req.setFd_biz_license_file_path(path);
            }
            myPageDao.updateCompany(req);
            MyPageCompanyVo companyMyPage = myPageDao.getCompanyMyPage(pcLoginInfoVo);
            result.put("mypage", companyMyPage);
        } else {
            if (req.getFd_company_logo_file_path() != null && req.getFd_company_logo_file_path().startsWith("/storage")) {
                String path = req.getFd_company_logo_file_path().replaceAll("/storage", "");
                req.setFd_company_logo_file_path(path);

            }
            myPageDao.updateCompanyStaff(req);
            myPageDao.updateStaff(req);
            myPageDao.deleteStatisticMyPage(req);
            if (req.getStatisticVoList().size() > 0) {
                userDao.statisticMyPage(req);
            }
            MyPageCompanyVo myPageCompanyStaffVo = myPageDao.getIndividualMyPage(pcLoginInfoVo);
            List<StatisticItemVo> statisticItemVo = myPageDao.getStatisticItem(pcLoginInfoVo);
            myPageCompanyStaffVo.setStatisticItemVoList(statisticItemVo);
            result.put("mypage", myPageCompanyStaffVo);
        }
        return result;
    }

    @Override
    public Map<String, Object> getMyplan(PcLoginInfoVo pcLoginInfoVo) {
    	System.out.println("=================================구독정보관리=========================================");
        Map<String, Object> result = new HashMap<>();
        MyPlanVo myPlanVo = myPageDao.getPlan(pcLoginInfoVo);
        //MyPlanVo myNextPlanVo = myPageDao.getNextPlan(pcLoginInfoVo); //loginCompanyPk : 379 육지현
        //MyPlanNewVo myNextPlanNewVo = myPageDao.getPlanNew(70);
        MyPlanNewVo myNextPlanNewVo = myPageDao.getPlanNew(pcLoginInfoVo.getLoginCompanyPk());
        
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("companyPk", pcLoginInfoVo.getLoginCompanyPk());
        int myBalance = myPageDao.getMyCredit(paramMap);
        
        String myCard = creditDao.getMyCard(pcLoginInfoVo);
        MyPlanResponseVo planInfo = new MyPlanResponseVo();
        if(myPlanVo != null) {
	        planInfo.setCharge_unit(myPlanVo.getCharge_unit());
	        planInfo.setCharge_amount(myPlanVo.getCharge_amount());
	        planInfo.setItem_name(myPlanVo.getItem_name());
	        //if(myNextPlanVo != null) planInfo.setNext_item_name(myNextPlanVo.getItem_name());
	
	        planInfo.setService_dt_from(myPlanVo.getService_dt_from());
	        planInfo.setPay(myCard);
	        planInfo.setAvailable_credit(myPlanVo.getCredit_main());
	
	        //if(myNextPlanVo != null) planInfo.setNext_service_dt_from(myNextPlanVo.getService_dt_from());
	        planInfo.setNext_charge_amount(myPlanVo.getCharge_amount());
        }
        
        //planInfo.setCharge_unit(myPlanVo.getCharge_unit());
        /*planInfo.setCharge_amount(myPlanVo.getCharge_amount());
        planInfo.setItem_name(myPlanVo.getItem_name());
        planInfo.setNext_item_name(myNextPlanVo.getItem_name());

        planInfo.setService_dt_from(myPlanVo.getService_dt_from());
        planInfo.setPay(myCard);
        planInfo.setAvailable_credit(myPlanVo.getCredit_main());

        planInfo.setNext_service_dt_from(myNextPlanVo.getService_dt_from());
        planInfo.setNext_charge_amount(myPlanVo.getCharge_amount());*/
        
        
        result.put("planInfo", planInfo);

        /*List<CreditItemVo> creditItemVoList = creditDao.getPlanSelectService("LT011");
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
        result.put("itemResVoList", itemResVoList);
        */
        
        if(myNextPlanNewVo != null) {
        	result.put("ppCardName", myNextPlanNewVo.getPp_card_name());
        	result.put("ppCardCd",myNextPlanNewVo.getPp_card_cd());
        	result.put("cost", myBalance);
        }
        result.put("myPlan", myPlanVo);
        
        return result;
    }

    @Override
    public Map<String, Object> updateMyPassword(PcLoginInfoVo pcLoginInfoVo, MyPasswordVo req) {
        String password = userDao.getPassword(pcLoginInfoVo.getLoginCompanyStaffPk());
        Map<String, Object> resultMap = new HashMap<>();
        if (req.getFd_staff_pw().equals(req.getNew_fd_staff_pw())) {
            resultMap.put("result", "기존비밀번호 와 신규 비밀번호가 동일 합니다.");
            return resultMap;
        }
        boolean matches = BCRYPT_ENCODER.matches(req.getFd_staff_pw(), password);
        if (!matches) {
            resultMap.put("result", "비밀번호가 일치 하지 않습니다.");
            return resultMap;
        } else {
            final String staffPw = BCRYPT_ENCODER.encode(Common.NVL(req.getNew_fd_staff_pw(), ""));

            userDao.updatePassword(pcLoginInfoVo.getLoginCompanyStaffPk(), staffPw);
            resultMap.put("result", "success");
            return resultMap;
        }


    }

    @Override
    public Map<String, Object> getMyCredit(PcLoginInfoVo pcLoginInfoVo) {
        Map<String, Object> resultMap = new HashMap<>();
//        int balance = ploonetApiService.getBalance(pcLoginInfoVo);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("companyPk", pcLoginInfoVo.getLoginCompanyPk());
        System.out.println("paramMap:" + paramMap);
        
        int myBalance = myPageDao.getMyCredit(paramMap);
        List<PgBillVo> pgBillVoList = myPageDao.getCredit();
        
        resultMap.put("balance", myBalance);
        resultMap.put("pgBillVoList", pgBillVoList);
        return resultMap;
    }
    
//    @Override
//    public Map<String, Object> getMyCredit(PcLoginInfoVo pcLoginInfoVo) {
//        Map<String, Object> resultMap = new HashMap<>();
//        int balance = ploonetApiService.getBalance(pcLoginInfoVo);
//        List<PgBillVo> pgBillVoList = myPageDao.getCredit();
//        resultMap.put("balance", balance);
//        resultMap.put("pgBillVoList", pgBillVoList);
//        return resultMap;
//    }

    @Override
    public Map<String, Object> getOption() {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> subMap = new HashMap<>();
        subMap.put("1", 30000);
        subMap.put("6", 20000);
        subMap.put("12", 15000);
        resultMap.put("PI025", subMap);
        subMap = new HashMap<>();
        subMap.put("1", 20000);
        subMap.put("6", 15000);
        subMap.put("12", 10000);
        resultMap.put("PI026", subMap);
        return resultMap;
    }

    @Override
    public Map<String, Object> registerOption(PcLoginInfoVo pcLoginInfoVo, List<MyPageOptionVo> req) {
        Map<String, Object> resultMap = new HashMap<>();
        req.forEach(item -> {
            if (item.getOptionId().equals("PI025")) {
                if (item.getTerm() == 1) {
                    item.setTotalAmount(item.getCount() * 30000 * item.getTerm());
                    item.setAmount(item.getTerm() * 30000);
                } else if (item.getTerm() == 6) {
                    item.setTotalAmount(item.getCount() * 20000  * item.getTerm());
                    item.setAmount(item.getTerm() * 20000);
                } else if (item.getTerm() == 12) {
                    item.setTotalAmount(item.getCount() * 15000  * item.getTerm());
                    item.setAmount(item.getTerm() * 15000);
                }
            } else if (item.getOptionId().equals("PI026")) {
                if (item.getTerm() == 1) {
                    item.setTotalAmount(item.getCount() * 20000 );
                    item.setAmount(20000);
                } else if (item.getTerm() == 6) {
                    item.setTotalAmount(item.getCount() * 15000);
                    item.setAmount(15000);
                } else if (item.getTerm() == 12) {
                    item.setTotalAmount(item.getCount() * 10000);
                    item.setAmount(10000);
                }
            }

        });
        int balance = ploonetApiService.getBalance(pcLoginInfoVo);

        int sum = req.stream().mapToInt(item -> item.getTotalAmount()).sum();
        if (balance < sum) {
            throwException.statusCode(400);
        }
        ploonetApiService.registerCredit(pcLoginInfoVo, req);
        return resultMap;
    }

    @Override
    public Page<MyPageCreditWalletVo> getCreditWallet(PcLoginInfoVo pcLoginInfoVo, Pageable page, LocalDateTime startDate, LocalDateTime endDate, String type) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("offset", page.getOffset());
        paramMap.put("pageSize", page.getPageSize());
//        paramMap.put("type",type);
        paramMap.put("type", type);
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);
        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();


        paramMap.put("company_pk", loginInfoVo.getLoginCompanyPk());
        List<MyPageCreditWalletVo> myPageCreditWalletVoList = creditDao.getWallet(paramMap);
//        if (page.getPageNumber() == 0) {
//            int removeCount = 0;
//            for (int i = myPageCreditWalletVoList.size() - 1; i > -1; i--) {
//                if (myPageCreditWalletVoList.get(i).getCredit() == 0) {
//                    myPageCreditWalletVoList.remove(i);
//                    removeCount += 1;
//                    if (removeCount == 3) {
//                        break;
//                    }
//                }
//            }
//        }
        myPageCreditWalletVoList.forEach(item -> {
            if (item.getDisp_group_cd().equals("B20202")) {
                item.setType("발급");
            } else if (item.getDisp_group_cd().equals("B20203")) {
                item.setType("사용");
            } else if (item.getDisp_group_cd().equals("B20204")) {
                item.setType("소멸");
            }
        });
        int cnt = creditDao.getWalletCnt(paramMap);
        return new PageImpl<>(myPageCreditWalletVoList, page, cnt);
    }

    @Override
    public Map<String, Object> getOptionHistory(PcLoginInfoVo pcLoginInfoVo) {

        Map<String, Object> resultMap = new HashMap<>();

        AtomicLong totalUseCredit = new AtomicLong(0L);
        List<MyPageOptionHistoryVo> myPageOptionHistoryVos = myPageDao.getOptionHistory(pcLoginInfoVo);
        myPageOptionHistoryVos.forEach(item -> {
            totalUseCredit.addAndGet(item.getCredit());
        });
        resultMap.put("total_use_credit", totalUseCredit.longValue());
        resultMap.put("histories", myPageOptionHistoryVos);

        return resultMap;

    }

    @Override
    public Page<MyPagePayLogVo> getPayLog(PcLoginInfoVo pcLoginInfoVo, Pageable page, LocalDateTime start, LocalDateTime end) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("companyPk", pcLoginInfoVo.getLoginCompanyPk());
        //paramMap.put("companyPk", 70);
        paramMap.put("offset", page.getOffset());
        paramMap.put("pageSize", page.getPageSize());
        paramMap.put("start", start);
        paramMap.put("end", end);

        List<MyPagePayLogVo> myPagePayLogVoList = myPageDao.getPayLog(paramMap);
        myPagePayLogVoList.forEach(item -> {
        	System.out.println("item 값 : "+item);
//            if (item.getFd_repeat_type_name().contains("충전")) {
//                item.setFd_repeat_type_name("결제");
//            }
        });
        Integer cnt = myPageDao.getPayLogCnt(paramMap);
        return new PageImpl<>(myPagePayLogVoList, page, cnt);
    }


}
