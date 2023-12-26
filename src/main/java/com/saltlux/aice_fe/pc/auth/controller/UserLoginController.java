package com.saltlux.aice_fe.pc.auth.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("${apiVersionPrefix}/workStage/user")
@RestController
public class UserLoginController extends BaseController {
	
	@Value("${ploonet.api.base.billing.url}")
    public String billingUrl;
	
	@Value("${ploonet.api.union.api.url}")
    public String unionApi;

    // 로그인 정보 조회 (Get Session)
    @GetMapping("/login/info")
    public Object loginInfo() throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        
        if ( pcLoginInfoVo == null ) {

            return new ResponseVo(204);

        } else {
        	
        	// quick
            JSONObject jsonObject = new JSONObject();
            String uuid = pcLoginInfoVo.getUuid();
            jsonObject.put("uuid", uuid);
            
//            String quickUrl = "/authentication/issue";//2023 11 07 변경됨
            String quickUrl = "/interface-system/" + uuid;//2023 11 07 변경됨
            
//            JSONParser jsonParser = new JSONParser();
//        	String apiResult = Common.getRestDataUnionApiPost(unionApi + quickUrl, jsonObject, billingUrl);
//        	org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject) jsonParser.parse(apiResult);
//        	org.json.simple.JSONObject quickObj = (org.json.simple.JSONObject) jsonObj.get("data");
//        	
//        	String quickStartStatus = quickObj.get("quickStartStatus").toString();
        	
        	String quickStartStatus = "-1";
        	org.json.simple.JSONObject jsonObj = new org.json.simple.JSONObject(resultMap);
            
        	String authToken = pcLoginInfoVo.getAuthToken();
            JSONParser jsonParser = new JSONParser();
            try {
//            	String apiResult = Common.getRestDataUnionApiPost(unionApi + quickUrl, jsonObject, billingUrl); //2023 11 07 변경됨
            	String apiResult = Common.getRestDataUnionApiGet(unionApi + quickUrl, authToken, billingUrl); //2023 11 07 변경됨
            	
            	System.out.println("apiResult ::::: " + apiResult);
            	
            	jsonObj = (org.json.simple.JSONObject) jsonParser.parse(apiResult);
            	//org.json.simple.JSONObject quickObj = (org.json.simple.JSONObject) jsonObj.get("data");
            	
				Map<String, Object> resultMapList1 = (Map<String, Object>) jsonObj.get("data");
				org.json.simple.JSONArray array = (org.json.simple.JSONArray) resultMapList1.get("ais");
				
				
		
				for(int i=0; i<array.size(); i++) {
					org.json.simple.JSONObject staffList = (org.json.simple.JSONObject) array.get(i);
					if(staffList.get("botDisplay").equals("Y")) {
						quickStartStatus = staffList.get("quickStartStatus").toString();
						resultMap.put("data", staffList);
					}
				}
				
            	
            	System.out.println("quickStartStatus ::::: " + quickStartStatus);
            	
            	//quickStartStatus = quickObj.get("quickStartStatus").toString();
    			
    		} catch (Exception e) {
    			// TODO: handle exception
    			System.out.println("Exception" + e);
    		}
        	
//        	String quickStartStatus = (String) ((org.json.simple.JSONObject) quickObj.get("data")).get("quickStartStatus");
//        	System.out.println("11111111111111 ::::: "  + ((org.json.simple.JSONObject) quickObj.get("data")).get("quickStartStatus").getClass() );
        	
        	
        	// quick
        	resultMap.put( "quickStartStatus"       , quickStartStatus         );
        	//resultMap.put( "quickObj"               , jsonObj         );
        	
        	//resultMap.put( "uuid"                   , pcLoginInfoVo.getUuid()         );
        	resultMap.put( "loginCompanyPk"         , pcLoginInfoVo.getLoginCompanyPk()         );
            resultMap.put( "loginCompanyName"       , pcLoginInfoVo.getLoginCompanyName()       );
            resultMap.put( "loginCompanyId"         , pcLoginInfoVo.getLoginCompanyId()         );
            if(pcLoginInfoVo.getLoginCompanyLogoUrl() != null) resultMap.put( "loginCompanyLogoUrl"    , pcLoginInfoVo.getLoginCompanyLogoUrl().replace("/storage", "")    );
            resultMap.put("loginUserType"           , pcLoginInfoVo.getLoginUserType()          );

            //
            resultMap.put( "loginCompanyStaffPk"    , pcLoginInfoVo.getLoginCompanyStaffPk()    );
            resultMap.put( "loginCompanyStaffId"    , pcLoginInfoVo.getLoginCompanyStaffId()    );
            resultMap.put( "loginCompanyStaffName"  , pcLoginInfoVo.getLoginCompanyStaffName()  );
            resultMap.put( "loginCompanyStaffDept"  , pcLoginInfoVo.getLoginCompanyStaffDept()  );
            
            // 권한 추가
            resultMap.put( "loginLevelCode"  , pcLoginInfoVo.getLoginLevelCode());
            resultMap.put( "loginFdStaffMobile"  , pcLoginInfoVo.getFdStaffMobile()  );
            resultMap.put( "loginFdStaffPhone"  , pcLoginInfoVo.getFdStaffPhone()  );
        }

        return new ResponseVo(200, resultMap);
    }
}
