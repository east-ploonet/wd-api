package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class BalanceBody extends BaseVo implements Serializable {

    private static final long serialVersionUID = -2269358025570883333L;

    private Long companySeq;

    private Long companyStaffSeq;

    private String creditType;

    private int amount;

    private String creditLimit;

    private String searchTime;
}
