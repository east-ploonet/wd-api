package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
public class MyPageOptionVo  implements Serializable {

    private static final long serialVersionUID = 470192659337430104L;

    private String optionId;

    private int term;

    private int count;

    private int amount;

    private int totalAmount;



}
