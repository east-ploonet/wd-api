package com.saltlux.aice_fe.pc.join.service;

import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe.pc.join.vo.TermsVo;

import java.util.List;
import java.util.Map;

public interface JoinService {

	Map<String, Object> getTermsList(TermsVo termsVo)  throws Exception;
	void registTerms(Map<String, Object> paramMap) throws Exception;
	void registInfo(Map<String, Object> paramMap) throws Exception;
	List<FileVo> fileUpload(Map<String, Object> paramMap)   throws Exception;
	Map<String, Object> checkStaffId(Map<String, Object> paramMap) throws Exception;
	Map<String, Object> dupCheckBizNumber(Map<String, Object> paramMap) throws Exception;

	Map<String, Object> registCompany(Map<String, Object> paramMap) throws Exception;
	Map<String, Object> registStaff(Map<String, Object> paramMap) throws Exception;
	void agreeTermsCompany(Map<String, Object> paramMap) throws Exception;
	void agreeTermsStaff(Map<String, Object> paramMap) throws Exception;
	long registStaffAi(Map<String, Object> paramMap) throws Exception;

	Map<String, Object> joinAuth() throws Exception;
	Map<String, Object> joinAuthResult(Map<String, Object> paramMap) throws Exception;

	Map<String, Object> checkCompanyId(Map<String, Object> paramMap) throws Exception;
	long registerPgPayLogEntry(long loginCompanyPk, long loginCompanyStaffPk);
}
