package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class StatisticItemVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 9080942742773987823L;

    public Long pk_statistic_item;

    public Long fk_statistic_group;

    private String item_name;

    private String input_val;
}
