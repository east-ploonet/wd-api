package com.saltlux.aice_fe.ecApi.service;

import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.credit.vo.MyNewCreditItemVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.my_page.vo.MyPageOptionVo;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public interface PloonetApiService {

    Map<String, Object> sendMessageApi		(CompanyStaffVo companyStaffVo) throws Exception ;
    Map<String, Object> sendMessageIndividualApi		(CompanyStaffVo companyStaffVo) throws Exception ;
    Map<String, Object> getUserBillingApi	(CompanyStaffVo companyStaffVo) throws Exception ;


    void fristSendBill(MyNewCreditItemVo myNewCreditItemVo, long loginCompanyPk, long pkPgPayLog);

    void firstSendBillEntry(MyNewCreditItemVo myNewCreditItemVo, long loginCompanyPk, long pkPgPayLog);

    int creditClose(long loginCompanyPk);

    void sendBillEntry(UserReqBody reqBody, long pkPgPayLog);

    int getBalance(PcLoginInfoVo pcLoginInfoVo);
    Map<String, Object>  getMyCreditInfo(PcLoginInfoVo pcLoginInfoVo);


    void registerCredit(PcLoginInfoVo pcLoginInfoVo, List<MyPageOptionVo> req);

    void sendChargeCredit(PcLoginInfoVo pcLoginInfoVo,int money);

    Map<String, Object> acsJobCommand(String dn, String command, String jobCode, String startTime, String [] reqNumbers) throws Exception ;

    Map<String, Object> acsJobStart(String dn, String command, String jobCode, String startTime, String [] reqNumbers, String reserveDt) throws Exception ;

    Map<String, Object> acsJobPause(String dn, String command, String jobCode) throws Exception ;

    JSONArray voiceNumbers(int type, String dnisType) throws Exception ;

    Map<String, Object> dnisSave(String userType, long fkCompany, long fkCompanyStaffAi, String vgwId, String number) throws Exception;

    Map<String, Object> tempDnisRegist(String userType, long fkCompany, long fkCompanyStaffAi, JSONObject number) throws Exception ;

    Map<String, Object> tempDnisDelete(String fullDnis, String dnis, long fkCompany, long fkCompanyStaffAi) throws Exception ;

    Map<String, Object> makeCallAPi(String command, String number, String destinationNumber, String vgwId, String channelType, String callType, String language, String svcType, String aiStaffSeq) throws Exception;

    Map<String, Object> msgSend(long fkCompany, String noticeCheckType, String msgType, String templateCode, String idFrom, String idTo, String title, String bodyMsg, String channelType) throws Exception;

    Map<String, Object> msgBulkSend(long fkCompany, String noticeCheckType, String msgType, String templateCode, String idFrom, String [] idTo, String title, String bodyMsg, String channelType, String reserveDt, String jobCode) throws Exception;

    Map<String, Object> talkbotBrokerUpdate(long fkCompany, long fkCompanyStaffAi, String type) throws Exception;
}
