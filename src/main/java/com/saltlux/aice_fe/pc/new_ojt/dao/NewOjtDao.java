package com.saltlux.aice_fe.pc.new_ojt.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saltlux.aice_fe._baseline.baseVo.CustomSqlSessionDaoSupport;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe.pc.new_ojt.vo.NewOjt2StepVo;
import com.saltlux.aice_fe.pc.ticket.vo.TicketTimeVo;

@Repository
public class NewOjtDao extends CustomSqlSessionDaoSupport {
		
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
	
	public int getExcelUploadCount(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getExcelUploadCount", paramMap); 
	}
	
	public int getBotStatusCount(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getBotStatusCount", paramMap); 
	}
	
	public DataMap getCompanyDnis(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getCompanyDnis", paramMap); 
	}
	
	public int aiConfWorkStatusUpdate(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.aiConfWorkStatusUpdate", paramMap);
	}
	
	//OJT1 회사정보
	public DataMap getInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getInfo", paramMap); 
	}
	
	//OJT1 아바타정보
	public DataMap getAvatarInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getAvatarInfo", paramMap); 
	}
	
	public List<Object> getAiConfWork(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getAiConfWork", paramMap);
	}
	
	
	//OJT1 회사정보 저장
	public int companyUpdate(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.companyUpdate", paramMap);
	}
	
	//OJT1 회사 업무시간 삭제
	public int companyTimeDelete(Map<String, Object> paramMap) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.companyTimeDelete",paramMap);
	}
	
	//OJT1 회사 업무시간 저장
	public int getCompanyTime(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getCompanyTime", paramMap);
	}
	
	// 인사말
	public int getCompanyIntro(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getCompanyIntro", paramMap);
	}
	
	//OJT1 회사 업무시간 저장
	public int companyTimeInsert(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.companyTimeInsert", paramMap);
	}
	
	//OJT1 ai 업무시간 삭제
	public int aiTimeDelete(Map<String, Object> paramMap) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.aiTimeDelete",paramMap);
	}
	
	//OJT1 ai 업무시간 저장
	public int getAiTime(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getAiTime", paramMap);
	}
	
	//OJT1 ai 업무시간 저장
	public int aiTimeInsert(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.aiTimeInsert", paramMap);
	}

	
	public int companyIntroDelete(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.companyIntroDelete", paramMap);
	}
	//OJT1 회사 인사말 저장
	public int companyIntroUpdate(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.companyIntroUpdate", paramMap);
	}
	
	//OJT1 첫인사말 / 긴급안내멘트
	public DataMap getIntro(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getIntro", paramMap); 
	}
	
	//OJT1 업무시간
	public List<Object> getOfficeTimeList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getOfficeTimeList", paramMap);
	}
	
	//OJT3 채용 완료시
	public List<Object> getTimeOrientation(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getTimeOrientation", paramMap);
	}
	
	
	//OJT1 회사 업무시간
	public List<Object>  getCompanyTimeList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getCompanyTimeList", paramMap);
	}
	
	//OJT1 회사 인사말 저장
	public int aiStaffStatusUpdate(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.aiStaffStatusUpdate", paramMap);
	}
	
	//OJT2 Step 리스트 항목 가져오기 (신규등록할때)
	public List<Object> getStepList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getStepList", paramMap);
	}
	
	//OJT2 Step1,2 리스트 항목 가져오기 (저장된데이터 불러오기)
	public List<Object> getStepOneTwo(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getStepOneTwo", paramMap);
		//return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getStepOneTwo", paramMap);
	}
	
	//OJT2 Step1,2 리스트 항목 가져오기 (저장된데이터 불러오기 임시저장 step1 하나선택시)
	public List<Object> getStepOneTwoNull(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getStepOneTwoNull", paramMap);
		//return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getStepOneTwo", paramMap);
	}
	
	//OJT2 Step3,4 리스트 항목 가져오기 (저장된데이터 불러오기)
	public List<Object> getStepThreeFour(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getStepThreeFour", paramMap);
	}
	//OJT2 Step3,4 주소 항목 가져오기 (저장된데이터 tbl_company)
	public List<Object> getStepThreeFourAddress(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getStepThreeFourAddress", paramMap);
	}
	//OJT2 Step1,2 등록하기
	public int ojt2StepOneTwoRegist(NewOjt2StepVo newOjt2StepVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.ojt2StepOneTwoRegist", newOjt2StepVo);
	}
	
	//OJT2 Step3,4 등록하기
	public int ojt2StepThreeFourRegist(NewOjt2StepVo newOjt2StepVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.ojt2StepThreeFourRegist", newOjt2StepVo);
	}
	
	//OJT2 Step3,4 등록하기 - 안내데스크//회사 위치 및 주차시설
	public int ojt2StepThreeFourRegistAddress(NewOjt2StepVo newOjt2StepVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.ojt2StepThreeFourRegistAddress", newOjt2StepVo);
	}
	
	//OJT2 Step1,2 초기화
	public int ojt2StepOneTwoDelete(Map<String, Object> paramMap) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.ojt2StepOneTwoDelete", paramMap);
	}
	
	//OJT2 Step3,4 초기화
	public int ojt2StepThreeFourDelete(Map<String, Object> paramMap) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.ojt2StepThreeFourDelete", paramMap);
	}
	
	// 장애접수 엑셀업로드 삭제
	public int ojtErrorDataDelete(Map<String, Object> paramMap) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.ojtErrorDataDelete", paramMap);
	}
	
	public DataMap getAiConfWorkDt(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getAiConfWorkDt", paramMap); 
	}
	
	public DataMap getCompanyDetp(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.getCompanyDetp", paramMap); 
	}
	
	//초기 값 가져오기(업무 수정 부분)
	public List<Object> profileCard(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.profileCard", paramMap);
	}
	
	public List<Object> profileCardInfo(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.profileCardInfo", paramMap);
	}
	
	public DataMap profileCardDefault(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao.profileCardDefault", paramMap); 
	}

}
