package com.saltlux.aice_fe._sample.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._sample.service.TestService;
import com.saltlux.aice_fe._sample.vo.TestVo;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/test") // end point : localhost:8080/api/v1/test
public class TestController extends BaseController {

    @Autowired
    private TestService testService;

    @Value("${profile.name}")
    public String profileName;


	// API TEST
    @RequestMapping(path = {"/ping"}, method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.DELETE})
    public String isRunning( @PathVariable("apiVersion") String apiVersion ) {

    	// @PathVariable 의 apiVersion 으로 요청 api 버전을 가져올 수 있다. (버전별 분기 필요할 때 사용)
        return "I'm Alive on [" + profileName + "], apiVersion is [v"+ apiVersion +"]";
    }

    // 테스트 목록 (전체)
    @RequestMapping(value = {"/list"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
    public Object getTestList(
		    @RequestParam Map<String, Object> paramMap
		    , @RequestParam(value = "page"      , required = false, defaultValue = "1"  ) int page
		    , @RequestParam(value = "pageSize"  , required = false, defaultValue = "10" ) int pageSize
		    , @RequestParam(value = "search"    , required = false, defaultValue = ""   ) String searchString
    ) throws Exception {

	    TestVo testVo = new TestVo();

	    testVo.getSearch().setPage(page);
	    testVo.getSearch().setPageSize(pageSize);
	    testVo.getSearch().setSearchString(searchString);
	    testVo.getSearch().setSearchColumn("fd_name");

        Map<String, Object> resultMap = testService.getTestList(testVo);

        return new ResponseVo(200, resultMap);
    }

    // 테스트 조회 (파라메타 요청값)
    @GetMapping("/get")
    public Object getTest(
            @RequestParam(value="testPk", required=true)    String testPk
    ) throws Exception {

	    TestVo testVo = new TestVo();
	    testVo.setPk_test(Long.parseLong(testPk));

	    Map<String, Object> resultMap = testService.getTest(testVo);

	    return new ResponseVo(200, resultMap);
    }

    // 테스트 입력 (body 요청값)
    @PostMapping("/regist")
    public Object registTest(
            @RequestBody Map<String, Object> reqJsonObj
    ) throws Exception {

        //필수값 체크
	  //throwException.requestBodyRequied( reqJsonObj, "testPk", "testName", "testPw1way", "testPw2way");
        throwException.requestBodyRequied( reqJsonObj, "testName", "testPw1way", "testPw2way");

        TestVo testVo = new TestVo();

	  //testVo.setPk_test(    Long.parseLong( reqJsonObj.get("testPk").toString()) );
	    testVo.setFd_name(    reqJsonObj.get("testName").toString().trim()    );
	    testVo.setFd_pw_1way( reqJsonObj.get("testPw1way").toString().trim()  );
	    testVo.setFd_pw_2way( reqJsonObj.get("testPw2way").toString().trim()  );

	    testService.registTest(testVo);

		return new ResponseVo(200);
    }

    // 테스트 수정 (body 요청값)
    @PutMapping("/update")
    public Object updateTest(
            @RequestBody Map<String, Object> reqJsonObj
    ) throws Exception {

        //필수값 체크
      //throwException.requestBodyRequied( reqJsonObj, "testPk", "testName", "testPw1way", "testPw2way");
        throwException.requestBodyRequied( reqJsonObj, "testPk", "testName");

        TestVo testVo = new TestVo();
	    testVo.setPk_test(    Long.parseLong( reqJsonObj.get("testPk").toString()) );
	    testVo.setFd_name(    reqJsonObj.get("testName").toString() );
	    testVo.setFd_pw_1way( Common.NVL(reqJsonObj.get("testPw1way"), "").trim()  );
	    testVo.setFd_pw_2way( Common.NVL(reqJsonObj.get("testPw2way"), "").trim()  );

	    testService.updateTest(testVo);

	    return new ResponseVo(200);
    }

    // 테스트 삭제 (body 요청값)
    @PutMapping("/delete")
    public Object deleteTest(
            @RequestBody Map<String, Object> reqJsonObj
    ) throws Exception {

		TestVo testVo = new TestVo();

    	for ( String key : reqJsonObj.keySet() ) {

    		if ( "testPk".equalsIgnoreCase(key) ) {

				//필수값 체크
				throwException.requestBodyRequied( reqJsonObj, "testPk");
				testVo.setPk_test( Long.parseLong( reqJsonObj.get("testPk").toString()) );
				testService.deleteTest(testVo);

			} else if ( "listPk".equalsIgnoreCase(key) ) {

				throwException.requestBodyRequied( reqJsonObj, "listPk");
				testVo.setPk_list( (List<Long>) reqJsonObj.get("listPk") );
				testService.deleteList(testVo);

			}
		}

	    return new ResponseVo(200);
    }

	// 인증값 조회 (세션 정보)
	@GetMapping("/authInfo")
	public Object getToken(
			HttpSession session
	) {

    	if(session.getAttribute("authToken") == null){
		    throwException.statusCode(401);

	    }else if(StringUtils.isEmpty(session.getAttribute("authToken").toString())){
		    throwException.statusCode(401);
	    }

        String loginAuthToken       = session.getAttribute("authToken").toString();
		AppLoginInfoVo appLoginInfoVo = (AppLoginInfoVo) session.getAttribute("loginInfo");

	    HashMap<String, Object> rtnMsg = new HashMap<>();

		rtnMsg.put("sessionId"              , session.getId() );
		rtnMsg.put("maxInactiveInterval"    , session.getMaxInactiveInterval() );
		rtnMsg.put("creationTime"           , new Date( session.getCreationTime()).toInstant().atZone(ZoneId.systemDefault()) );
		rtnMsg.put("lastAccessTime"         , new Date( session.getLastAccessedTime()).toInstant().atZone(ZoneId.systemDefault()) );
		rtnMsg.put("isNew"                  , session.isNew() );

	    rtnMsg.put("loginAuthToken"         , loginAuthToken);
	    rtnMsg.put("loginInfo"              , appLoginInfoVo);

	    return new ResponseVo(200, rtnMsg);
	}

	@PostMapping("/uploadFiles")
	public Object uploadFileTest(
			 @RequestParam(value="formJson"     , required=false    ) final String formJson
			,@RequestParam(value="uploadFile"   , required=false    ) final MultipartFile[] uploadFiles
	) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put( "uploadFiles" , uploadFiles  );

		List<FileVo> fileVoList = testService.fileUploadTest(paramMap);

		HashMap<String, Object> rtnMsg = new HashMap<>();
		rtnMsg.put("fileVoList"     , fileVoList );

//		return new ResponseEntity<Object>("Success", HttpStatus.OK);
		return new ResponseVo(200, rtnMsg);
	}

}
