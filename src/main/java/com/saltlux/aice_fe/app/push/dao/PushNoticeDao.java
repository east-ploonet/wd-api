package com.saltlux.aice_fe.app.push.dao;

import com.saltlux.aice_fe.push.vo.PushVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Mapper
@Repository
public interface PushNoticeDao {

    List<PushVo> selectPushNoticeList       (PushVo paramVo) throws SQLException;
    int          selectUnreadPushNoticeCount(PushVo paramVo) throws SQLException;
    int          updatePushNoticeReceiveDate(PushVo paramVo) throws SQLException;

}
