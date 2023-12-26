package com.saltlux.aice_fe._sample.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._sample.vo.ChatRoomVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("${apiVersionPrefix}/chat")
public class ChatController extends BaseController {

	List<ChatRoomVo> roomList = new ArrayList<ChatRoomVo>();
	static int roomNumber = 0;

	/**
	 * 방 목록 화면
	 */
	@GetMapping("/list")
	public Object room() {

		return "thymeleaf/sample/chat/list";
	}

	/**
	 * 방 목록
	 */
	@PostMapping("/getRoomList")
	public @ResponseBody List<ChatRoomVo> getRoom(
			@RequestParam HashMap<Object, Object> params
	){
		return roomList;
	}

	/**
	 * 방 생성
	 */
	@PostMapping("/createRoom")
	public @ResponseBody List<ChatRoomVo> createRoom (
			@RequestParam HashMap<Object, Object> params
	){
		String roomName = params.get("roomName").toString();

		if(roomName != null && !"".equals(roomName.trim())) {

			ChatRoomVo room = new ChatRoomVo();
			room.setRoomNumber(++roomNumber);
			room.setRoomName(roomName);

			roomList.add(room);
		}
		return roomList;
	}

	/**
	 * 채팅
	 */
	@RequestMapping("/chating")
	public Object chating(
			@RequestParam HashMap<Object, Object> params, Model model
	){

		int roomNumber = Integer.parseInt(params.get("roomNumber").toString());

		List<ChatRoomVo> new_list
			= roomList
				.stream()
				.filter(
					obj->obj.getRoomNumber() == roomNumber
				).collect(
					Collectors.toList()
				);

		if(new_list.size() > 0) {

			model.addAttribute( "roomName"  , params.get("roomName") );
			model.addAttribute( "roomNumber", params.get("roomNumber") );

			return "thymeleaf/sample/chat/room";
		}else {
			return "thymeleaf/sample/chat/list";
		}
	}
}
