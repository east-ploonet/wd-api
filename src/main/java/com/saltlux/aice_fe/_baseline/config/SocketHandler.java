package com.saltlux.aice_fe._baseline.config;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class SocketHandler extends TextWebSocketHandler {

	//HashMap<String, WebSocketSession> sessionMap = new HashMap<>();   //웹소켓 세션을 담아둘 맵
	List<HashMap<String, Object>> sessionMapList = new ArrayList<>();   //웹소켓 세션을 담아둘 리스트 ---roomListSessions

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {

		//메시지 발송
		String msg                  = message.getPayload();
		JSONObject jsonObject       = jsonToObjectParser(msg);
		String messageRoomNumber    = jsonObject.get("roomNumber").toString();

		HashMap<String, Object> tempMap = new HashMap<String, Object>();

		if(sessionMapList.size() > 0) {

			for (HashMap<String, Object> socketMap : sessionMapList) {

				String roomNumber = (String) socketMap.get("roomNumber");   //세션 리스트의 저장된 방번호를 가져와서
				if (roomNumber.equals(messageRoomNumber)) {                 //같은값의 방이 존재한다면
					tempMap = socketMap;                                    //해당 방번호의 세션 리스트에 존재하는 모든 WebSocketSession 값을 가져온다.
					break;
				}
			}

			//해당 방의 세션들만 찾아서 메시지를 발송.
			for(String k : tempMap.keySet()) {
				if(k.equals("roomNumber")) { //초기값일 경우에는 skip
					continue;
				}

				WebSocketSession wss = (WebSocketSession) tempMap.get(k);
				if(wss != null) {
					try {
						wss.sendMessage( new TextMessage(jsonObject.toJSONString()) );

					} catch (IOException ex) {
						log.error("********** handleTextMessage : ", ex);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		//소켓 연결
		super.afterConnectionEstablished(session);

		boolean flag        = false;
		String url          = Objects.requireNonNull(session.getUri()).toString();
		String sessionRoomNumber   = url.split("/workapi/v1/chat/connect/")[1];
		int idx             = sessionMapList.size();

		log.debug("********** afterConnectionEstablished > url : {}", url);

		for(int i=0; i<sessionMapList.size(); i++) {

			String rN = (String) sessionMapList.get(i).get("roomNumber");

			if(rN.equals(sessionRoomNumber)) {
				flag    = true;
				idx     = i;
				break;
			}
		}

		if(flag) {  //존재하는 방이면 세션만 추가
			HashMap<String, Object> map = sessionMapList.get(idx);
			map.put(session.getId(), session);

		}else {     //최초 생성 방이면 방번호와 세션을 추가
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roomNumber"    , sessionRoomNumber);
			map.put(session.getId() , session);
			sessionMapList.add(map);
		}

		//세션등록이 끝나면 발급받은 세션ID값의 메시지를 발송
		JSONObject jsonObject       = new JSONObject();
		jsonObject.put("type"       , "getId");
		jsonObject.put("sessionId"  , session.getId());
		session.sendMessage(new TextMessage(jsonObject.toJSONString()));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		//소켓 종료
		for (HashMap<String, Object> stringObjectHashMap : sessionMapList) { //소켓이 종료되면 해당 세션값들을 찾아서 삭제
			stringObjectHashMap.remove(session.getId());
		}

		super.afterConnectionClosed(session, status);
	}

	private static JSONObject jsonToObjectParser(String jsonStr) {

		JSONParser jsonParser   = new JSONParser();
		JSONObject jsonObject   = null;

		try {
			jsonObject = (JSONObject) jsonParser.parse(jsonStr);

		} catch (ParseException ex) {
			log.error("********** jsonToObjectParser : ", ex);
		}

		return jsonObject;
	}
}
