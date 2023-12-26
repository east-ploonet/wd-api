package com.saltlux.aice_fe.pc.user.vo.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StatisticReqBody {

    public Long pk_statistic_item;

    public Long fk_statistic_group;

    public String input_val;
}
