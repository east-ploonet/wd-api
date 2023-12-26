package com.saltlux.aice_fe.push.service.impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe.push.dao.PushDao;
import com.saltlux.aice_fe.push.service.PushService;
import com.saltlux.aice_fe.push.vo.PushVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PushServiceImpl extends BaseServiceImpl implements PushService {
	
	@Autowired
	private PushDao pushDao;

	// 푸시 등록
	@Override
	public PushVo insertPush(PushVo reqPushVo) {

		//DB 처리
		try {
			pushDao.insertPush( reqPushVo );

		} catch (Exception ex) {
			log.error("********** reqPushVo : {}", reqPushVo.toString());
			throwException.statusCode(500);
		}

		return reqPushVo;
	}

	//사용자 수정 처리
	@Override
	public void updatePushResult(PushVo reqPushVo) {

		Map<String, Object> result      = new HashMap<>();

		PushVo pushVo = new PushVo();

		//DB 처리
		try {
			int resultCnt = pushDao.updatePushResult(reqPushVo);

		} catch (Exception ex) {
			log.error("********** reqPushVo : {}", reqPushVo.toString());
			throwException.statusCode(500);
		}
	}

}
