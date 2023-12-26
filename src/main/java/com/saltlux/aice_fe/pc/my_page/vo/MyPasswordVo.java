package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyPasswordVo  extends BaseVo implements Serializable {

    private static final long serialVersionUID = -982890716618939467L;

    private String fd_staff_pw;

    private String new_fd_staff_pw;
}
