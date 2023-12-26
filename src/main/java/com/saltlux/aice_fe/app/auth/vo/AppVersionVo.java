package com.saltlux.aice_fe.app.auth.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class AppVersionVo implements Serializable {

	private static final long serialVersionUID = 5094781138008883786L;

	private long    pk_app_version;         // app 버전관리 pk
	private String  fd_os_code;             // OS구분 코드
	private int     fd_version_major;       // app 버전[major]
	private int     fd_version_minor;       // app 버전[minor]
	private int     fd_version_patch;       // app 버전[patch]
	private String  fd_version_title;       // app 버전 제목
	private String  fd_version_contents;    // app 버전 설명
	private Date    fd_version_open_date;   // app 버전 게시일시
	private String  fd_open_yn;             // 게시 여부YN
	private Date    fd_regdate;             // 등록일시
	private Date    fd_moddate;             // 수정일시
}
