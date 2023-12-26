package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyPageOptionHistoryVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -645285405481551178L;

    private Long pk_credit_wallet_log;
    private String item_name;
    private Long one_item_credit;
    private Long item_month;
    private Long item_count;
    private Long credit;
    private String date;
    private String credit_item_cd;


}
