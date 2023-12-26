package com.saltlux.aice_fe.pc.send.service.impl;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.send.dao.SendDao;
import com.saltlux.aice_fe.pc.send.service.SendService;
import com.saltlux.aice_fe.pc.send.vo.SendCustomerVo;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendServiceImpl extends BaseServiceImpl implements SendService {

	@Autowired
	private SendDao sendDao;

	@Autowired
	PloonetApiService ploonetApiService;

	@Autowired
	private FileService fileService;

	@Override
	public DataMap getCustomerInfo(Map<String, Object> paramMap) {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());

		DataMap resultMap = null;

		resultMap = sendDao.getCustomerInfo(paramMap);

		return resultMap;
	}

	@Override
	public void saveCompanyCustomer(
			//Map<String, Object> paramMap,
			String formJson
	) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		SendCustomerVo sendCustomerVo = new SendCustomerVo();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);

		//String pkCompanyCustomer = paramMap.get("pkCompanyCustomer");

		//if(pkCompanyCustomer.equals(""))
		sendCustomerVo.setPk_company_customer( Common.parseInt(paramMap.get("pkCompanyCustomer")) );
		sendCustomerVo.setFd_additional_information(Common.NVL( paramMap.get("fdAdditionalInformation") , null) );
		sendCustomerVo.setFd_company_address_common(Common.NVL( paramMap.get("fdCompanyAddressCommon"), null));
		sendCustomerVo.setFd_company_address_detail(Common.NVL( paramMap.get("fdCompanyAddressDetail"), null));
//		sendCustomerVo.setFd_company_depart(Common.NVL( paramMap.get("fdCompanyDepartment"),null));
		sendCustomerVo.setFd_company_dept(Common.NVL( paramMap.get("fdCompanyDept"),null));
		sendCustomerVo.setFd_company_name(Common.NVL( paramMap.get("fdCompanyName"),null));
		sendCustomerVo.setFd_company_position(Common.NVL( paramMap.get("fdCompanyPosition"),null));
		sendCustomerVo.setFd_customer_address_common(Common.NVL( paramMap.get("fdCustomerAddressCommon"),null));
		sendCustomerVo.setFd_customer_address_detail(Common.NVL( paramMap.get("fdCustomerAddressDetail"),null));
		sendCustomerVo.setFd_customer_email(Common.NVL( paramMap.get("fdCustomerEmail"),null));
		sendCustomerVo.setFd_customer_mobile(Common.NVL( paramMap.get("fdCustomerMobile"),null));
		sendCustomerVo.setFd_customer_name(Common.NVL( paramMap.get("fdCustomerName"),null));
		sendCustomerVo.setFd_customer_phone(Common.NVL( paramMap.get("fdCustomerPhone"),null));
		sendCustomerVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		sendCustomerVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
		sendCustomerVo.setFk_modifier( loginInfoVo.getLoginCompanyStaffPk());


		try {

			Map<String, Object> param = new HashMap<>();


			int result = 0;

			result = sendDao.companyCustomerUpdate(sendCustomerVo);
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}

	@Override
	public Map<String, Object> saveNewCompanyCustomer(
			String mobileList,
			String formJson // addList
	) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
//		JSONArray addList = new JSONArray(formJson);
		List<Object> resultList = null;
		Map<String, Object> resultMap = new HashMap<>();

		try {

			int result = 0;

//			for (int i = 0; i < addList.length(); i++) {


			Map<String, Object> param = new HashMap<>();

//			    JSONObject jsonObj = addList.getJSONObject(i);
//			    System.out.println("hi" + jsonObj.get("fdCustomerMobile")); // 여기서만 phone = mobile

//			    param.put("fd_customer_name", Common.NVL( jsonObj.get("fdCustomerName"),null));
//			    param.put("fd_customer_mobile", Common.NVL( jsonObj.get("fdCustomerMobile"),null));
//			    param.put("fd_customer_email", Common.NVL( jsonObj.get("fdCustomerEmail"),null));
			param.put("fd_customer_name", Common.NVL( mobileList ,null)); // DB 상 null 들어가면 안됨.. 이름에 전화번호 집어넣으래요
			param.put("fd_customer_mobile", Common.NVL( mobileList ,null));
			param.put("fk_writer", loginInfoVo.getLoginCompanyStaffPk());
			param.put("fk_company", loginInfoVo.getLoginCompanyPk());

			int count = sendDao.isNewCompanyCustomer(param);
			if(count <= 0) {
				result = sendDao.newCompanyCustomer(param);

				if ( result <= 0 ) {
					throwException.statusCode(500);
				}
			}

//			}

			// select 해와야 됨 ( mobileList 안에 있는 친구들로 )
			Map<String, Object> paramMobile = new HashMap<>();

			paramMobile.put("mobile_list", mobileList);
			paramMobile.put("fk_company", loginInfoVo.getLoginCompanyPk());

			resultList = sendDao.getNewCompanyCustomer(paramMobile);

			resultMap.put("list", resultList);

		} catch (Exception ex) {
			System.out.println("ex:" + ex);
//			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> saveNewCompanyCustomerList(
			String[] mobileList,
			String formJson // addList
	) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		JSONArray addList = new JSONArray(formJson);
		List<Object> resultList = null;
		Map<String, Object> resultMap = new HashMap<>();

		try {

			int result = 0;

			for (int i = 0; i < addList.length(); i++) {


				Map<String, Object> param = new HashMap<>();

				JSONObject jsonObj = addList.getJSONObject(i);
//			    System.out.println("hi" + jsonObj.get("fdCustomerMobile")); // 여기서만 phone = mobile

				param.put("fd_customer_name", Common.NVL( jsonObj.get("fdCustomerName"),null));
				param.put("fd_customer_mobile", Common.NVL( jsonObj.get("fdCustomerMobile"),null));
				param.put("fd_customer_email", Common.NVL( jsonObj.get("fdCustomerEmail"),null));
//				param.put("fd_customer_name", Common.NVL( mobileList ,null)); // DB 상 null 들어가면 안됨.. 이름에 전화번호 집어넣으래요
//				param.put("fd_customer_mobile", Common.NVL( mobileList ,null)); 
				param.put("fk_writer", loginInfoVo.getLoginCompanyStaffPk());
				param.put("fk_company", loginInfoVo.getLoginCompanyPk());

				int count = sendDao.isNewCompanyCustomer(param);
				if(count <= 0) {
					result = sendDao.newCompanyCustomer(param);

					if ( result <= 0 ) {
						throwException.statusCode(500);
					}
				}

			}

			// select 해와야 됨 ( mobileList 안에 있는 친구들로 )
			Map<String, Object> paramMobile = new HashMap<>();

			paramMobile.put("mobile_list", mobileList);
			paramMobile.put("fk_company", loginInfoVo.getLoginCompanyPk());

			resultList = sendDao.getNewCompanyCustomerList(paramMobile);

			resultMap.put("list", resultList);

		} catch (Exception ex) {
			System.out.println("ex:" + ex);
//			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
		return resultMap;
	}

	@Override
	public void saveSnsInfo(String[] totalList, String[] numberToList, String sendNumber, String formJson, String adTitle, MultipartFile[] uploadFiles) throws Exception {
		// TODO Auto-generated method stub

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);

		//대표 발신번호 호출
		Map<String, Object>defaultYnMap          = new HashMap<>();
		defaultYnMap.put("fk_company", loginInfoVo.getLoginCompanyPk());
		List<Object> defaultYnData = sendDao.defaultYnUse(defaultYnMap);
		String num = defaultYnData.get(0).toString();
		String num2 = num.replace("{", "");
		String num3 = num2.replace("}", "");
		String defaultYn = num3.replace("bizPhoneNum=","");


		try {

			int result = 0;

			Map<String, Object> param = new HashMap<>();

			LocalDate nowDate = LocalDate.now();
			LocalTime nowTime = LocalTime.now();
			DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyyMMdd");
			DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HHmmss");

			String formatedNowDate = nowDate.format(formatterDate);
			String formatedNowTime = nowTime.format(formatterTime);

			String channelType = Common.NVL( paramMap.get("channelType") ,null );
			String msgBody = Common.NVL( paramMap.get("msgBody") , "" );
			if(channelType.equals("B1006") || channelType.equals("B1007") || channelType.equals("B1008")) {
				msgBody = Common.NVL(adTitle, "") + msgBody;
			}
			String reserveDt = "00000000000000";
			String reserveYn = Common.NVL( paramMap.get("reserveYn") ,null );
			if(reserveYn.equals("Y")) {
				reserveDt = Common.NVL( paramMap.get("reserveDt") , null );
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(reserveDt);
				reserveDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				if(channelType.equals("B1006") || channelType.equals("B1007") || channelType.equals("B1008")) {
					reserveDt = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
				}
			}

			String jobCode =
					"OBC-" +
							formatedNowDate + formatedNowTime + "-" +  defaultYn;

			param.put("pk_bulk_send_plan", jobCode); //fd_send_num
			param.put("number_from", sendNumber); //fd_send_num
			param.put("reserve_yn", reserveYn);//fd_send_time
			param.put("reserve_dt", Common.NVL( paramMap.get("reserveDt") , null ));//fd_send_date
			param.put("channel_type", channelType);//fd_send_channel
			param.put("agree_ad_yn", Common.NVL( paramMap.get("agreeAdYn") ,null ));//fd_send_ad
			param.put("fd_ad_name", Common.NVL( paramMap.get("fdAdName") ,null ));//fd_ad_name

			param.put("msg_title", Common.NVL( paramMap.get("msgTitle") ,null ));//fd_send_title
			param.put("msg_body", msgBody);//fd_send_comment
			param.put("fk_staff", loginInfoVo.getLoginCompanyStaffPk());
			param.put("chk_level", Common.NVL( paramMap.get("chkLevel") ,null ));//fkPriority
			param.put("memo", Common.NVL( paramMap.get("memo") ,null ));//fd_send_comment
//			param.put("fd_send_comment_tel", Common.NVL( paramMap.get("fdSendCommentTel") ,null ));

			if( uploadFiles != null && uploadFiles.length > 0 ) {

				List<FileVo> fileVoList = fileService.uploadFileToVoList( uploadFiles, "SendSns_List" );
				if( fileVoList != null && fileVoList.size() > 0 && fileVoList.get(0) != null ) {
//					param.put("fd_file_name", fileVoList.get(0).getFd_file_name()); // 이거 안씀..
					param.put("path_attach", fileVoList.get(0).getFd_file_path()); // fd_file_path
				}
			}
			param.put("cnt_user", totalList.length);//fd_customer_cnt
			param.put("fk_writer", loginInfoVo.getLoginCompanyStaffPk());
			param.put("fk_company", loginInfoVo.getLoginCompanyPk());


			param.put("fk_company", loginInfoVo.getLoginCompanyPk());
			DataMap dnisDt = sendDao.selectDnis(param);

			param.put("aiStaffSeq", dnisDt.get("fkCompanyStaffAi").toString());
			param.put("fullDnis", dnisDt.get("fullDnis").toString());
			param.put("fdDnis", dnisDt.get("fdDnis").toString());

			result = sendDao.saveSend(param);
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}


			///////////// 발신 이력 저장 완료
			//SMS
			if(channelType.equals("B1006") || channelType.equals("B1007") || channelType.equals("B1008")) {
				ploonetApiService.msgBulkSend(loginInfoVo.getLoginCompanyPk(), "INSTANT", "B1006", "aicesvc003", defaultYn, numberToList, "발신번호 인증번호 발송", msgBody, "voice", reserveDt, jobCode);
			}else {
				// acs
				Map<String, Object> result2 = new HashMap<>();
				Map<String, Object> result3 = new HashMap<>();
				result2 = ploonetApiService.acsJobCommand(defaultYn , "create", jobCode , null , numberToList);

				String resultMsg = result2.get("messages").toString();
				System.out.println("create :"+ resultMsg);
				if(resultMsg.equals("Success")) {
					result3 = ploonetApiService.acsJobStart(defaultYn , "start", jobCode , null , null, reserveDt);
				}
			}

			// 발신 고객 등록
			System.out.println(param.get("pk_bulk_send_plan")); //jobcode ?? 이상한거 갖고 옴..

//			for(String item : totalList) {
			for(int i = 0; i< totalList.length; i ++) {
				Map<String, Object> param2 = new HashMap<>();
				param2.put("fk_bulk_send_plan", jobCode);
				param2.put("number_to", numberToList[i]);
				param2.put("channel_type", Common.NVL( paramMap.get("channelType") ,null ));
				param2.put("fk_customer", totalList[i]); // ??????
				param2.put("fk_writer", loginInfoVo.getLoginCompanyStaffPk());

//				System.out.println("item : " + item);

				result = sendDao.saveSendCustomer(param2);
				if ( result <= 0 ) {
					throwException.statusCode(500);
				}
			}

			///////////////////



		}catch (Exception ex){
			System.out.println("ex:" + ex);
			throwException.statusCode(500);
		}


	}

	@Override
	public Map<String, Object> getSendHistory(int offset, int pageSize, String startDate, String endDate, String channel, String channelType, String sender) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> param = new HashMap<>();

		param.put("fk_company", loginInfoVo.getLoginCompanyPk());
		param.put("offset", offset);
		param.put("pageSize", pageSize);
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		param.put("channel", channel);
		param.put("channelType", channelType);
		param.put("sender", sender);



		List<Object> listData = sendDao.getSendHistory(param);
		Map<String, Object> result          = new HashMap<>();
		int totalCnt = sendDao.getSendHistoryCnt(param);

		result.put("totalCnt", totalCnt);
		result.put("sendHistory", listData);
		return result;
	}

	@Override
	public Map<String, Object> getSendUserHistory(String pkBulkSendPlan, int offset, int pageSize, String search) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> param = new HashMap<>();

		param.put("fk_bulk_send_plan", pkBulkSendPlan);
		param.put("offset", offset);
		param.put("pageSize", pageSize);
		param.put("search", search);


		List<Object> listData = sendDao.getSendUserHistory(param);
		Map<String, Object> result          = new HashMap<>();
		int totalCnt = sendDao.getSendUserHistoryCnt(param);


		result.put("sendUserHistory", listData);
		result.put("totalCnt", totalCnt);
		return result;
	}

	@Override
	public Map<String, Object> getSendList() throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> param = new HashMap<>();

		param.put("fk_company", loginInfoVo.getLoginCompanyPk());


		List<Object> listData = sendDao.getSendList(param);
		Map<String, Object> result          = new HashMap<>();

		result.put("sendList", listData);
		return result;
	}

	@Override
	public Map<String, Object> getSendListCustomer(Map<String, Object> paramMap) throws Exception {

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		paramMap.put("fk_company", loginInfoVo.getLoginCompanyPk());

		List<Object> listData = sendDao.getSendListCustomer(paramMap);
		Map<String, Object> result          = new HashMap<>();

		result.put("sendListCustomer", listData);
		return result;
	}

	@Override
	public DataMap getDetailInfo(Map<String, Object> paramMap) {

		DataMap resultMap = null;

		resultMap = sendDao.getDetailInfo(paramMap);

		return resultMap;
	}

	@Override
	public List<Object> defaultYnUse(Map<String, Object> paramMap) throws Exception {

		List<Object> result = sendDao.defaultYnUse(paramMap);
		return result;
	}

	@Override
	public DataMap selectDnis(Map<String, Object> paramMap) throws Exception {
		DataMap result = sendDao.selectDnis(paramMap);
		return result;
	}


	@Override
	public List<Object> getDetailCustomer(Map<String, Object> paramMap) throws Exception {

		List<Object> result = sendDao.getDetailCustomer(paramMap);

		return result;
	}

	@Override
	public Map<String, Object> getCompanyCustomer(int page, String search) throws Exception {


		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put("fk_company", loginInfoVo.getLoginCompanyPk());
		paramMap.put("page", page);
		paramMap.put("search", search);

		List<Object> listData = sendDao.getCompanyCustomer(paramMap);
		Map<String, Object> result          = new HashMap<>();

		int totalCnt = sendDao.companyCustomerCnt(paramMap);
		result.put("totalCnt", totalCnt);

		result.put("companyCustomer", listData);
		return result;
	}

}