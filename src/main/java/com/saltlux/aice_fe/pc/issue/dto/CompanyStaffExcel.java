package com.saltlux.aice_fe.pc.issue.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Data
public class CompanyStaffExcel implements Serializable {

    private long pkLoginStaff;
    private long pkCompany;
}
