package com.saltlux.aice_fe.commonCode.dto;

import com.saltlux.aice_fe.commonCode.vo.CodeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CodeDTO extends CodeVo {

    private long totalStatusTicketIssue;

    public CodeDTO(CodeVo codeVo) {
        this.setPk_code(codeVo.getPk_code());
        this.setFk_up_code(codeVo.getFk_up_code());
        this.setFd_name(codeVo.getFd_name());
        this.setFd_use_yn(codeVo.getFd_use_yn());
        this.setFd_sort_num(codeVo.getFd_sort_num());
        this.setFd_memo(codeVo.getFd_memo());
        this.setFd_name(codeVo.getFd_name());
    }
}
