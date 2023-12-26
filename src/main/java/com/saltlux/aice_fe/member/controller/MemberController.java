package com.saltlux.aice_fe.member.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.member.dao.MemberDao;
import com.saltlux.aice_fe.member.service.MemberService;
import com.saltlux.aice_fe.member.vo.MemberVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("${apiVersionPrefix}/member")
@RestController
public class MemberController extends BaseController {
	
    @Autowired
    MemberService memberService;

    @Autowired
    private MemberDao memberDao;


    //사용자 목록
    @GetMapping("/list")
    public Object memberList(
		      @RequestParam Map<String, Object> paramMap
		    , @RequestParam(value = "page"      , required = false, defaultValue = "${navi.page}"       ) int page
		    , @RequestParam(value = "pageSize"  , required = false, defaultValue = "${navi.pageSize}"   ) int pageSize
    ) throws Exception {

        paramMap.put( "pageSize"    , pageSize);
        paramMap.put( "offset"      , (page - 1) * pageSize);

        //회원 리스트
	    Map<String, Object> resultMap   = memberService.selectMemberList( paramMap );

	    return new ResponseVo(200, resultMap);
    }

    //사용자 등록 처리
    @PostMapping("/regist")
    public Object memberRegist_proc(
		    @RequestBody Map<String, Object> bodyMap
    ) throws Exception {

	    //필수값 체크
	    throwException.requestBodyRequied( bodyMap, "memberId", "memberPw", "memberName");

	    //사용자 아이디 중복 확인
	    Map<String, Object> resultMap = memberService.memberIdDupCheck( bodyMap );
	    if( "N".equals( resultMap.get("acceptYN").toString() ) ){
		    throwException.statusCode(409);
	    }

	    memberService.insertMember( bodyMap );
	    return new ResponseVo(200);
    }

    //사용자 정보 조회
    @GetMapping("/get")
    public Object memberModify(
		    @RequestParam Map<String, Object> paramMap
    ) throws Exception {

        if( paramMap.isEmpty() ){
            AppLoginInfoVo appLoginInfoVo = (AppLoginInfoVo)request.getSession().getAttribute("loginInfo");
//            paramMap.put( "memberPk", appLoginInfoVo.getLoginMemberPk() );
        }

	    MemberVo memberVo = memberService.getMember( paramMap );
	    return new ResponseVo(200,  memberVo);
    }

    //사용자 정보 변경 처리
    @PostMapping("/modify")
    public Object memberModify_proc(
		    @RequestBody Map<String, Object> bodyMap
    ) throws Exception {

	    //필수값 체크
	    throwException.requestBodyRequied( bodyMap, "memberPk", "memberPw");

	    return memberService.updateMember( bodyMap );
    }

	//사용자 탈퇴 처리
	@PostMapping("/drop")
	public Object memberDrop_proc(
			@RequestBody Map<String, Object> bodyMap
	) throws Exception {

		//필수값 체크
		throwException.requestBodyRequied( bodyMap, "memberPk");

		memberService.dropMember( bodyMap );

		return new ResponseVo(200);
	}

	//사용자 삭제 처리
	@DeleteMapping("/delete")
	public Object memberDelete_proc(
			@RequestBody Map<String, Object> bodyMap
	) throws Exception {

		//필수값 체크
		throwException.requestBodyRequied( bodyMap, "memberPk");

		memberService.deleteMember( bodyMap );

		return new ResponseVo(200);
	}

	//사용자 아이디 중복확인
	@PostMapping("/memberIdDupCheck")
    public Object memberIdDupCheck(
		    @RequestBody Map<String, Object> paramMap
    ) throws Exception {

	    Map<String, Object> resultMap = memberService.memberIdDupCheck( paramMap );

	    return new ResponseVo(200, resultMap);
    }

}
