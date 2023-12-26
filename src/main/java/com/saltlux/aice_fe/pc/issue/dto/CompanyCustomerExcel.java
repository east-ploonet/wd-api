package com.saltlux.aice_fe.pc.issue.dto;

import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Data
public class CompanyCustomerExcel implements Serializable {

    private long pkLoginStaff;
    private long pkCompany;

}
