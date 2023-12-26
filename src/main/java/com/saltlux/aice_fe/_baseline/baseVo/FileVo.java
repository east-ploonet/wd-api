package com.saltlux.aice_fe._baseline.baseVo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FileVo {

	//-- Global Fields --//
	//
	private String pk_file;            // 파일 pk
	private String fd_file_name;       // 파일 명
	private String fd_file_path;       // 파일 경로
	private String fd_file_size;       // 파일 용량
	private String fd_mime_code;       // 파일 종류코드
	private int    fd_sort_num;        // 정렬순서
	private String fd_open_yn  = "Y";  // 게시 여부YN
	//
	//-- Global Fields --//

}
