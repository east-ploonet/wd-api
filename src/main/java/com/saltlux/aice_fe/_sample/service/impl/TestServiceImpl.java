package com.saltlux.aice_fe._sample.service.impl;

import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.security.AESUtil;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe._sample.dao.TestDao;
import com.saltlux.aice_fe._sample.service.TestService;
import com.saltlux.aice_fe._sample.vo.TestVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TestServiceImpl extends BaseServiceImpl implements TestService {

    @Autowired
    private TestDao testDao;

	@Autowired
	private FileService fileService;

	// 테스트 목록 (전체)
    @Override
    public Map<String, Object> getTestList(TestVo reqTestVo) throws Exception {

        Map<String, Object> result          = new HashMap<>();
        List<Map<String, Object>> listMap   = new ArrayList<>();
	    AESUtil aesUtil                     = new AESUtil(authEncryptKey, authEncryptIv);

	    int totalCnt = testDao.getTestAllCnt(reqTestVo);

	    //총 게시물수
	    result.put("totalCnt", totalCnt);

	    if(totalCnt > 0){
	        List<TestVo> listTestVo = testDao.getTestList(reqTestVo);

	        if(listTestVo == null){

	            throwException.statusCode(204);

	        } else {

		        for(TestVo testVo : listTestVo){

			        Map<String, Object> mapAdd = new HashMap<>();

			        mapAdd.put("testPk"         ,Long.toString(testVo.getPk_test()));
			        mapAdd.put("testName"       ,testVo.getFd_name());

			        mapAdd.put("testRegIp"      ,testVo.getFd_writer_ip());
			        mapAdd.put("testPw1way"     ,testVo.getFd_pw_1way());
			        mapAdd.put("testPw2way"     ,testVo.getFd_pw_2way());

			        if( testVo.getFd_pw_2way() != null && !"".equals(testVo.getFd_pw_2way()) ){
				        mapAdd.put("testPw2wayDecode"   ,aesUtil.aesDecode( testVo.getFd_pw_2way()) );
			        }

			        mapAdd.put("testRegDate"    ,FormatUtils.dateToStringCustomize( testVo.getFd_regdate()      , "yyyy.MM.dd HH:mm") );
			        mapAdd.put("testUpdateDate" ,FormatUtils.dateToStringCustomize( testVo.getFd_update_date()  , "yyyy.MM.dd HH:mm"));
			        listMap.add(mapAdd);
		        }
	        }
	    }
	    result.put("list", listMap);

	    return result;
    }

    // 테스트 조회 (단일 건수)
    @Override
    public Map<String, Object> getTest(TestVo reqTestVo) throws Exception {

	    log.debug( "********** Transaction name : {}", TransactionSynchronizationManager.getCurrentTransactionName() );
	    Map<String, Object> result = new HashMap<>();

	    TestVo testVo = testDao.getTest(reqTestVo);

	    if(testVo == null){
	        throwException.statusCode(204);

	    } else {
		    result.put("testPk"     ,Long.toString(testVo.getPk_test()));
		    result.put("testName"   ,testVo.getFd_name());
		    result.put("testRegDate",testVo.getFd_regdate());
		    result.put("testModDate",testVo.getFd_update_date());
	    }

	    return result;
    }

    // 테스트 입력
    @Override
    public void registTest(TestVo reqTestVo) throws Exception {

	    AESUtil aesUtil             = new AESUtil(authEncryptKey, authEncryptIv);

	    reqTestVo.setFd_writer_ip   ( getClientIp() );
	    reqTestVo.setFd_pw_1way     ( BCRYPT_ENCODER.encode(reqTestVo.getFd_pw_1way()) );
	    reqTestVo.setFd_pw_2way     ( aesUtil.aesEncode( reqTestVo.getFd_pw_2way()) );

	    try {
		    int pk_test = testDao.insertTest(reqTestVo);

	    } catch (DuplicateKeyException dupEx) {
		    throwException.statusCode(409);

	    } catch (Exception ex) {
		    log.error("********** reqTestVo : {}", reqTestVo.toString());
		    throwException.statusCode(500);
	    }
    }

    // 테스트 수정
    @Override
    public void updateTest(TestVo reqTestVo) throws Exception {

	    AESUtil aesUtil             = new AESUtil(authEncryptKey, authEncryptIv);

	    if ( reqTestVo.getFd_pw_1way() != null && !reqTestVo.getFd_pw_1way().isEmpty() ) {
			reqTestVo.setFd_pw_1way     ( BCRYPT_ENCODER.encode(reqTestVo.getFd_pw_1way()) );
		}

		if ( reqTestVo.getFd_pw_2way() != null && !reqTestVo.getFd_pw_2way().isEmpty() ) {
			reqTestVo.setFd_pw_2way     ( aesUtil.aesEncode(reqTestVo.getFd_pw_2way()) );
		}

	    int updatedCnt = 0;
	    
	    try {
	        updatedCnt = testDao.updateTest(reqTestVo);
	        log.debug("********** updated row cnt : {}", updatedCnt);

	    } catch (Exception ex) {
	    	log.error("********** reqTestVo : {}", reqTestVo.toString());
		    throwException.statusCode(500);
	    }

	    if(updatedCnt == 0){
		    throwException.statusCode(204);
	    }
    }

    // 테스트 삭제
    @Override
    public void deleteTest(TestVo reqTestVo) throws Exception {

	    int deletedCnt = 0;

	    try {
		    deletedCnt = testDao.deleteTest(reqTestVo);

	    } catch (Exception ex) {
		    log.error("********** reqTestVo : {}", reqTestVo.toString());
		    throwException.statusCode(500);
	    }

	    if(deletedCnt == 0){
		    throwException.statusCode(204);
	    }
    }

	// 테스트 삭제(List)
	@Override
	public void deleteList(TestVo reqTestVo) throws Exception {

		int deletedCnt = 0;

		try {
			deletedCnt = testDao.deleteList(reqTestVo);

		} catch (Exception ex) {
			log.error("********** reqTestVo : {}", reqTestVo.toString());
			throwException.statusCode(500);
		}

		if(deletedCnt == 0){
			throwException.statusCode(204);
		}
	}

	// 파일업로드 테스트
	@Override
	public List<FileVo> fileUploadTest(Map<String, Object> paramMap) throws Exception {

		return fileService.uploadFileToVoList( (MultipartFile[]) paramMap.get("uploadFiles"), "TB_TEST" );
	}

}
