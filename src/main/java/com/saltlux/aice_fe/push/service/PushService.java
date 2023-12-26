package com.saltlux.aice_fe.push.service;

import com.saltlux.aice_fe.push.vo.PushVo;

import java.util.Map;

public interface PushService {

	PushVo  insertPush(PushVo reqVo);
	void    updatePushResult(PushVo reqVo);
}
