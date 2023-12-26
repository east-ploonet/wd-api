package com.saltlux.aice_fe._sample.service;

import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._sample.vo.TestVo;

import java.util.List;
import java.util.Map;

public interface TestService {

	Map<String, Object> getTestList(TestVo testVo)  throws Exception;
	Map<String, Object> getTest(TestVo testVo)      throws Exception;
	void                registTest(TestVo testVo)   throws Exception;
	void                updateTest(TestVo testVo)   throws Exception;
	void                deleteTest(TestVo testVo)   throws Exception;
	void                deleteList(TestVo testVo)   throws Exception;
	List<FileVo>        fileUploadTest(Map<String, Object> paramMap)   throws Exception;
}
