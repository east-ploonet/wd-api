package com.saltlux.aice_fe.pc.user.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.user.dao.UserDao;
import com.saltlux.aice_fe.pc.user.service.UserService;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/user")
public class UserController  extends BaseController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @PostMapping()
    public Object registUser(@RequestBody UserReqBody reqBody) throws Exception {
        Long staffPk = userDao.checkBeforeSaveStaff(reqBody.getFd_staff_id());
        if (staffPk!=null){
            return new ResponseVo(400);
        }
        userService.save(reqBody);
        return new ResponseVo(200, reqBody);
    }


    @GetMapping("/item")
    public Object checkItem() {
        Map<String, Object> resultMap = userService.getItems();

        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/check/email")
    public Object checkEmail(@RequestParam String email) {
        Map<String, Object> resultMap = userService.checkEmail(email);

        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/check/nickName")
    public Object checkNickName(@RequestParam String nickname) {
        Map<String, Object> resultMap = userService.checkNickName(nickname);

        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/terms")
    public Object getAllTerms() {
        Map<String, Object> resultMap = userService.getAllTerms();

        return new ResponseVo(200, resultMap);
    }


    @GetMapping("/simbol")
    public Object getSimbol(){
        PcLoginInfoVo pcLoginInfoVo= getPcLoginInfoVo();
        if (pcLoginInfoVo ==null){
            return new ResponseVo(403);
        }
        Map<String,Object> resultMap= userService.getSimbol(pcLoginInfoVo);
        return new ResponseVo(200,resultMap);
    }


}
