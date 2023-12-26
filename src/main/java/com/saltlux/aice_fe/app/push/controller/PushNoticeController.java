package com.saltlux.aice_fe.app.push.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.app.push.service.PushNoticeService;
import com.saltlux.aice_fe.push.vo.PushVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/notice")
public class PushNoticeController extends BaseController {

	@Autowired
	private PushNoticeService pushNoticeService;

	// 푸시 알림 목록 조회
	@GetMapping("/list")
	public Object listNoticePush() throws Exception {

		AppLoginInfoVo loginInfoVo = getAppLoginInfoVo();

		PushVo pushVo = new PushVo();
		pushVo.setFk_company_staff( loginInfoVo.getLoginCompanyStaffPk() );

		Map<String, Object> resultMap = pushNoticeService.selectPushNoticeList(pushVo);

		return new ResponseVo( 200, resultMap );
	}

	// 푸시 알림 미확인 메세지 Count
	@GetMapping("/unread/count")
	public Object countUnreadNoticePush() throws Exception {

		AppLoginInfoVo loginInfoVo = getAppLoginInfoVo();

		PushVo pushVo = new PushVo();
		pushVo.setFk_company_staff( loginInfoVo.getLoginCompanyStaffPk() );

		Map<String, Object> resultMap = pushNoticeService.selectUnreadPushNoticeCount(pushVo);

		return new ResponseVo(200, resultMap);

	}

}
