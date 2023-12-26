package com.saltlux.aice_fe.pc.my_page.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.my_page.service.MyPageService;
import com.saltlux.aice_fe.pc.my_page.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/myPage")
public class MyPageController extends BaseController {


    @Autowired
    private MyPageService myPageService;
    
    @Value("${ploonet.sso.url.host}")
    public String ssoHost;

	@Value("${ploonet.sso.url.api.login}")
    public String ssoLginApiUrl;

	@Value("${ploonet.sso.url.api.logout}")
    public String ssoLogoutApiUrl;

	@Value("${ploonet.sso.url.api.apply}")
    public String ssoApplyApiUrl;
	
	@Value("${ploonet.sso.url.api.token}")
   public String ssoTokenApiUrl;

	@Value("${ploonet.sso.client.id}")
    public String ssoClientId;
	
	@Value("${ploonet.sso.client.secret}")
    public String ssoClientSecret;
	
    @GetMapping("")
    public Object getMyPage() {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = myPageService.getMyPage(pcLoginInfoVo);
        return resultMap;
    }

    @PutMapping("")
    public Object updateMyPage(@RequestBody UpdateMyPageVo req) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = myPageService.updateMyPage(pcLoginInfoVo, req);
        return resultMap;
    }

    @PostMapping("/password")
    public Object updatePassword(@RequestBody MyPasswordVo req) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = myPageService.updateMyPassword(pcLoginInfoVo, req);
        return resultMap;
    }

    @GetMapping("/plan")
    public Object getMyPlan() {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = myPageService.getMyplan(pcLoginInfoVo);
        return resultMap;
    }

    @GetMapping("/credit")
    public Object getMyCredit() {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = myPageService.getMyCredit(pcLoginInfoVo);
        return resultMap;
    }


    @GetMapping("/option")
    public Object getOption() {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = myPageService.getOption();
        return resultMap;
    }

    @PostMapping("/option")
    public Object registerOption(@RequestBody List<MyPageOptionVo> req) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = myPageService.registerOption(pcLoginInfoVo, req);
        return new ResponseVo(200);
    }

    @GetMapping("/option/history")
    public Object getOptionHistory() {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = myPageService.getOptionHistory(pcLoginInfoVo);
        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/credit/wallet")
    public Object getWallet(@PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(required = false, defaultValue = "") String startDate,
                            @RequestParam(required = false, defaultValue = "") String type,
                            @RequestParam(required = false, defaultValue = "") String endDate) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = new HashMap<>();
        LocalDateTime end = null;
        LocalDateTime start = null;
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            start = LocalDateTime.parse(startDate).withHour(0).withMinute(0).withSecond(0).withNano(0);
            end = LocalDateTime.parse(endDate).withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1);
        }

        Page<MyPageCreditWalletVo> result = myPageService.getCreditWallet(pcLoginInfoVo, pageable, start, end, type);
        resultMap.put("pages", result);
        resultMap.put("size", pageable.getPageSize());
        return new ResponseVo(200, resultMap);
    }


    @GetMapping("/credit/pay_log")
    public Object getPayLog(@PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(required = false, defaultValue = "") String startDate,
                            @RequestParam(required = false, defaultValue = "") String endDate) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = new HashMap<>();
        LocalDateTime end = null;
        LocalDateTime start = null;
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            start = LocalDateTime.parse(startDate).withHour(0).withMinute(0).withSecond(0).withNano(0);
            end = LocalDateTime.parse(endDate).withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1);
        }

        Page<MyPagePayLogVo> result = myPageService.getPayLog(pcLoginInfoVo, pageable, start, end);
        resultMap.put("pages", result);
        resultMap.put("size", pageable.getPageSize());
        return new ResponseVo(200, resultMap);
    }
    
    @GetMapping("/logout")
    public Object logout(HttpServletRequest request, HttpSession session) {
    	
    	PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(200);
        } else {
        	if(pcLoginInfoVo.getSsoIdToken() == null) {
	        	if(session.getAttribute("ssoIdToken") != null) {
	        		pcLoginInfoVo.setSsoIdToken((String)session.getAttribute("ssoIdToken"));
	        	} else if(request.getHeader("Sso-Id-Token") != null) {
	        		pcLoginInfoVo.setSsoIdToken(request.getHeader("Sso-Id-Token"));
	        	}
        	}
        }
        
        
        try {
	    	
	        //세션 체크
	        if(request.getSession().getAttribute("pcAuthToken") != null){
//		        session.invalidate(); //모든 세션 제거
		        request.getSession().removeAttribute("pcAuthToken");
	        }
		    if(request.getSession().getAttribute("pcLoginInfo") != null){
			    request.getSession().removeAttribute("pcLoginInfo");
		    }
        } catch (Exception e) {
			// TODO: handle exception
    	    return new ResponseVo(500);
		}
        
        
	    return new ResponseVo(200);
    }
}
