package com.saltlux.aice_fe._sample.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -8630997188527685645L;

    private long    pk_test;            // 테스트 테이블 pk
    private String  fd_name;            // 테스트 명

    private String  fd_writer_ip;       // 등록자 IP

    private String  fd_pw_1way;         // 암호화 값 (단방향)
    private String  fd_pw_2way;         // 암호화 값 (양방향)

    private Date    fd_regdate;         // 등록일시
    private Date    fd_update_date;     // 수정일시

    private List<Long> pk_list;         // 테스트 pk 리스트

	private String fd_file_name;       // 파일 명
	private String fd_file_path;       // 파일 경로

}
