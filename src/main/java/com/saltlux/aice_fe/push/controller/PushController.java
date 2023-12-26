package com.saltlux.aice_fe.push.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.push.FCMSender;
import com.saltlux.aice_fe._baseline.push.PushResultHandler;
import com.saltlux.aice_fe.app.auth.dao.AppAuthDao;
import com.saltlux.aice_fe.member.service.MemberService;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.push.service.PushService;
import com.saltlux.aice_fe.push.vo.PushVo;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("${apiVersionPrefix}/push")
@RestController
public class PushController extends BaseController implements PushResultHandler {

	@Value("${push.fcm.serverKey}")
	protected String pushFcmServerKey;

	@Autowired
	AppAuthDao appAuthDao;

	@Autowired
	PushService pushService;


	//발송 테스트
    @GetMapping("/sendTest")
    public Object pushSendTest(

    ) throws Exception {

		// 메시지 payload
	    JSONObject jsonObj 		    = new JSONObject();
	    JSONObject jsonObjData 	    = new JSONObject();

	    jsonObjData.put("title"     , "안녕하세요.");
	    jsonObjData.put("body"      , "내용입니다.");
	    jsonObj.put("notification"  , jsonObjData);
	    String fcmString            = jsonObj.toJSONString();


		// 클라이언트 토큰 리스트
	    List<Map<String, Object>> tokenlist = new ArrayList<>();
	    Map<String, Object> token           = new HashMap<>();

//		token.put("TOKEN", "dP1-9Ut_T5KMWphTEBjBhv:APA91bGNHlUAHb7xigsjelxy0JHHp64qs46GH10ACYRORhBJ8znUBbtJ5yy0YshHdFMEHukyJRIXJL_Yw8wVD88W9qPw7f1DDW1Ekmlw2Etyba167CODE6-bN7hk_Or3-FT_iRQNH1a3");
	    token.put("TOKEN", "fs2V2q2ATDS3CxoHdl20o-:APA91bG7CktrvwiU-9VfcVGIpoiMMJ0UFuc3xFjJxompa1CxTqxiQ-xky3-9GVWhRuFXhfFdEaleIUNq5Cor2j8X7e4QNkUSvm14rXboCczr_e2xq8CcZk-Kbe-DjcznYg9xZed7d1TJ");

		tokenlist.add(token);

		// 발송
		FCMSender fsender = new FCMSender(
				tokenlist,
				fcmString,
				pushFcmServerKey,
				0,
				0
			);
	    fsender.setCallback(this);
	    fsender.send(false);

	    return new ResponseVo(200);
    }

	@PostMapping("/send")
	public Object pushSend(
			@RequestBody Map<String, Object> bodyMap
	) throws Exception {

		// 내부 ip 요청이 아닌 경우 권한없음 에러 리턴 (C 클래스 적용)
		String clientIp         = getRealIp(request);
		String clientIpBound    = clientIp.substring(0,clientIp.lastIndexOf("."));

		String hostIp           = InetAddress.getLocalHost().getHostAddress();
		String hostIpBound      = hostIp.substring(0,hostIp.lastIndexOf("."));

/*
		log.debug("****************************************");
		log.debug("clientIp = {}"       , clientIp);
		log.debug("hostIp = {}"         , hostIp);
		log.debug("clientIpBound = {}"  , clientIpBound);
		log.debug("hostIpBound = {}"    , hostIpBound);
		log.debug("****************************************");
*/

		if( !hostIpBound.equals(clientIpBound)
				&& !"127.0.0.1".equals(clientIp)
		){
			return new ResponseVo(403);
		}

		//필수값 체크
		throwException.requestBodyRequied( bodyMap, "staffPk", "title", "message");

		CompanyStaffVo getCompanyStaff = new CompanyStaffVo();
		getCompanyStaff.setPk_company_staff( Long.parseLong(bodyMap.get("staffPk").toString()) );

		if( getCompanyStaff.getPk_company_staff() > 0L){
			getCompanyStaff  = appAuthDao.getCompanyStaffByPk(getCompanyStaff);
		}

		if(getCompanyStaff.getFd_push_token() == null || "".equals(getCompanyStaff.getFd_push_token()) || "N".equals(getCompanyStaff.getFd_push_noti_yn()) ){
			return new ResponseVo(204);
		}

		// 푸시 이력 저장
		PushVo pushVo = new PushVo();
		pushVo.setFk_company_staff( getCompanyStaff.getPk_company_staff() );
		pushVo.setFk_issue_ticket(  (bodyMap.get("ticketPk") != null && !"".equals(bodyMap.get("ticketPk").toString()) ) ? Long.parseLong(bodyMap.get("ticketPk").toString()) : 0L );
		pushVo.setFd_title(         bodyMap.get("title").toString() );
		pushVo.setFd_message(       bodyMap.get("message").toString() );

		pushVo = pushService.insertPush( pushVo );


		// 메시지 payload
		JSONObject jsonObj 		            = new JSONObject();

		JSONObject jsonObj_notification 	= new JSONObject();
		jsonObj_notification.put("title"        , pushVo.getFd_title());
		jsonObj_notification.put("body"         , pushVo.getFd_message());
		jsonObj_notification.put("click_action" , "PushClick");
		jsonObj.put("notification"              , jsonObj_notification);


		JSONObject jsonObj_data 	            = new JSONObject();
		jsonObj_data.put("title"                , pushVo.getFd_title());
		jsonObj_data.put("body"                 , pushVo.getFd_message());
		jsonObj_data.put("click_action"         , "PushClick");
		jsonObj_data.put("type"                 , "1");
		if(pushVo.getFk_issue_ticket() > 0L){
			jsonObj_data.put("link"             , pushInboundUrl + "/counselTalk?pkIssueTicket=" + pushVo.getFk_issue_ticket() );
		}else{
			jsonObj_data.put("link"             , pushInboundUrl + "/counsel" );
		}
		jsonObj.put("data"                      , jsonObj_data);


		String fcmString            = jsonObj.toJSONString();

		// 클라이언트 토큰 리스트
		List<Map<String, Object>> tokenlist = new ArrayList<>();
		Map<String, Object> token           = new HashMap<>();

		token.put("TOKEN"   , getCompanyStaff.getFd_push_token());
		token.put("pushPk"  , pushVo.getPk_company_staff_push());
		tokenlist.add(token);

		// 발송
		FCMSender fsender = new FCMSender(
				tokenlist,
				fcmString,
				pushFcmServerKey,
				0,
				0
		);
		fsender.setCallback(this);
		fsender.send(false);
		
		return new ResponseVo(200);
	}

	@Override
	public void endSendPushNotification(List<Map<String, Object>> clients, int mseq, int paseq) {
/*
		log.debug("****************************************");
		log.debug("clients = {}", clients);
		log.debug("****************************************");
*/

		for (Map<String, Object> tMap : clients) {

			PushVo pushVo = new PushVo();
			pushVo.setPk_company_staff_push(Long.parseLong(tMap.get("pushPk").toString()) );
			pushVo.setFd_send_result(       tMap.get("result").toString() );

			pushService.updatePushResult( pushVo );
		}
	}

	@Override
	public void insertPushResult(Map<String, Object> client, int mseq, int paseq) {

	}

	@Override
	public void updatePushResult(Map<String, Object> client, int mseq, int paseq) {

	}

	@Override
	public void endSendPushNotificationIos(int mseq, int paseq) {

	}

	public static String getRealIp(HttpServletRequest request) {

		String ip = null;
		//
		if(request != null) {

			ip = request.getHeader("X-Forwarded-For");
			//
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		}
		//
		return ip;
	}

}
