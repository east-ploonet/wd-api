package com.saltlux.aice_fe.pc.join.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class StatisticVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -5202926712781056136L;


    //tbl_statistic_item
    private Long pk_statistic_item;
    private Long fk_statistic_group;
    private Long disp_order;
    private String use_yn;
    private String item_name;
    private String input_yn;
    private Long fk_writer;
    private Date fd_regdate;
    private Long fk_modifier;
    private Date fd_moddate;

    //tbl_statistic_group
    private String select_type;





}
