package com.saltlux.aice_fe.pc.user.vo.Response;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserSimbolVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 347617829961470207L;

    public Long pk_company;

    private String fd_company_logo_file_name; // 로고이름

    public String fd_company_logo_file_path;  // 로고 패스




}
