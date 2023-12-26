package com.saltlux.aice_fe.pc.user.service;

import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    void save(UserReqBody reqBody) throws Exception;

    Map<String, Object> checkEmail(String email);

    Map<String, Object> checkNickName(String nickname);

    Map<String, Object> getAllTerms();

    Map<String, Object> getItems();

    Map<String, Object> getUserInfo();

    Map<String, Object> getQuestionTerm();
    
    Map<String, Object> getAivoice(Map<String, Object> paramMap);
    

    void saveAdmin(UserReqBody reqBody);

    Map<String, Object> getSimbol(PcLoginInfoVo pcLoginInfoVo);
}
