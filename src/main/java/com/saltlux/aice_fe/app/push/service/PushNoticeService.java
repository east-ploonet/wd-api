package com.saltlux.aice_fe.app.push.service;

import com.saltlux.aice_fe.push.vo.PushVo;

import java.util.Map;

public interface PushNoticeService {

	Map<String, Object> selectPushNoticeList(PushVo paramVo) 		throws Exception;
	Map<String, Object>	selectUnreadPushNoticeCount(PushVo paramVo) throws Exception;

}
