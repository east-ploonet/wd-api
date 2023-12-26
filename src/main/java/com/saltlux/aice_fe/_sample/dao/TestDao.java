package com.saltlux.aice_fe._sample.dao;

import com.saltlux.aice_fe._sample.vo.TestVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TestDao {

    int             getTestAllCnt(TestVo testVo);
    List<TestVo>    getTestList(TestVo testVo);
	TestVo          getTest(TestVo testVo);
    int             insertTest(TestVo testVo);
	int             updateTest(TestVo testVo);
	int             deleteTest(TestVo testVo);
	int             deleteList(TestVo testVo);
}
