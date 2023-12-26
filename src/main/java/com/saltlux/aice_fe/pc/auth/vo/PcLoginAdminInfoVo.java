package com.saltlux.aice_fe.pc.auth.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class PcLoginAdminInfoVo implements Serializable {

	private static final long serialVersionUID = 6251926713279786981L;

	private long  pkAdmin;         // 회사 ID(이메일 형식, @ploonet.com)
	private String  loginAdminId;         // 회사 ID(이메일 형식, @ploonet.com)
	private String loginAdminName;
	private int loginAdminLevel;
	private String loginAdminMobile;
	private String loginAdminEmail;
}
