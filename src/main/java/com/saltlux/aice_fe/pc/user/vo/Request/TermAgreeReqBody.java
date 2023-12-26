package com.saltlux.aice_fe.pc.user.vo.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TermAgreeReqBody {
    private Long pk_terms;
    private String fd_agree_yn;
}
