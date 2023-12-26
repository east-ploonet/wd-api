package com.saltlux.aice_fe.app.push.service.impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe.app.push.dao.PushNoticeDao;
import com.saltlux.aice_fe.app.push.service.PushNoticeService;
import com.saltlux.aice_fe.push.vo.PushVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PushNoticeServiceImpl extends BaseServiceImpl implements PushNoticeService {
	
	@Autowired
	private PushNoticeDao pushNoticeDao;

	@Override
	public Map<String, Object> selectPushNoticeList(PushVo paramVo) throws Exception {

		Map<String, Object> result          = new HashMap<>();
		List<Map<String, Object>> listMap   = new ArrayList<>();

		List<PushVo> listData = pushNoticeDao.selectPushNoticeList(paramVo);

		if ( listData == null || listData.isEmpty() ) {

			throwException.statusCode(204);

		} else {

			for ( PushVo vo : listData ) {

				Map<String, Object> obj = new HashMap<>();
				//
				obj.put( "pkPush"		, vo.getPk_company_staff_push()	);
				obj.put( "fkStaff"		, vo.getFk_company_staff()		);
				obj.put( "fkIssueTicket", vo.getFk_issue_ticket()		);
				obj.put( "title"		, vo.getFd_title()				);
				obj.put( "message"		, vo.getFd_message()			);
				obj.put( "sendResult"	, vo.getFd_send_result()		);
				obj.put( "sendDate"		, vo.getFd_send_date()			);
				obj.put( "sendDateStr"	, vo.getFd_send_date_str()	    );
				obj.put( "receiveDate"	, vo.getFd_receive_date()		);

				listMap.add(obj);

			}

			result.put( "listData", listMap );

			// 푸시알림 목록 조회 후 수신일시 업데이트(미확인 메세지 전체 읽음 처리)
			pushNoticeDao.updatePushNoticeReceiveDate(paramVo);

		}

		return result;

	}

	@Override
	public Map<String, Object> selectUnreadPushNoticeCount(PushVo paramVo) throws Exception {

		Map<String, Object> result = new HashMap<>();
		//
		result.put( "unreadCount", pushNoticeDao.selectUnreadPushNoticeCount(paramVo) );

		return result;

	}

}
