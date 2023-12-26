package com.saltlux.aice_fe.pc.admin.vo.req;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteAdminReq extends BaseVo implements Serializable {

    private static final long serialVersionUID = 7503174725235906069L;

    private List<Long> questionIdList=new ArrayList<>();
}
