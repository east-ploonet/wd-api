package com.saltlux.aice_fe.pc.user.vo.Response;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 347617829961470207L;

    public Long pk_company_staff;

    private String fd_company_id; // 별명

    public String fd_staff_id;  // 이메일

    public String fd_staff_name; // 회원이름

    public String fd_staff_mobile; // 회원 핸드폰 번호
    
    public String fk_ai_policy_avatar_img; // 플루니안 이미지



}
