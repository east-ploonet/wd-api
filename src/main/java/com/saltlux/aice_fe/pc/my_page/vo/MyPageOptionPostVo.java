package com.saltlux.aice_fe.pc.my_page.vo;


import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MyPageOptionPostVo implements Serializable {

    private static final long serialVersionUID = -8166083254063720875L;

    private Long itemSeq;

    private String itemCd;

    private int itemCount;

    private String itemTermUnit;

    private int itemTermCount;

    private String itemStatus="B20105";

    private String serviceDtFrom;

    private String serviceDtTo;

    private int amountCredit;
}
