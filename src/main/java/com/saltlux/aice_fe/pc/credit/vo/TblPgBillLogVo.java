package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class TblPgBillLogVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 2576986193115195780L;

    private LocalDateTime service_dt_to;
}
