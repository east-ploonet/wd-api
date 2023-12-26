package com.saltlux.aice_fe.push.dao;

import com.saltlux.aice_fe.member.vo.MemberVo;
import com.saltlux.aice_fe.push.vo.PushVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PushDao {

    long    insertPush(PushVo reqVo);
	int     updatePushResult(PushVo reqVo);
}
