package com.saltlux.aice_fe.pc.credit.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.credit.service.CreditService;
import com.saltlux.aice_fe.pc.join.service.JoinService;
import com.saltlux.aice_fe.pc.join.vo.TermsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/credit") // end point : localhost:8080/api/v1/workStage/join
public class CreditController extends BaseController {

    @Autowired
    CreditService creditService;

    @GetMapping("/plan")
    public Object getPlanList(@RequestParam String charge_unit,
                              @RequestParam int charge_term) throws Exception {
        Map<String, Object> resultMap = creditService.getPlan(charge_unit, charge_term);
        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/balance")
    public Object getBalance(
            @RequestParam String charge_unit,
            @RequestParam int charge_term,
            @RequestParam String fk_service_plan
    ) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = creditService.getBalance(pcLoginInfoVo, fk_service_plan, charge_term, charge_unit);
        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/my_card")
    public Object getMyCard() {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = creditService.getMyCard(pcLoginInfoVo);
        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/payPage")
    public Object getPayPage(@RequestParam String fk_service_plan, @RequestParam String fk_service_plan_dc){
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String,Object> resultMap = creditService.getPayPage(fk_service_plan,fk_service_plan_dc,pcLoginInfoVo);
        return new ResponseVo(200,resultMap);
    }




}