package com.saltlux.aice_fe.pc.join.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.join.service.JoinService;
import com.saltlux.aice_fe.pc.join.vo.TermsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/join") // end point : localhost:8080/api/v1/workStage/join
public class JoinController extends BaseController {
	
	@Value("${kakao.rest.api}")
	private String kakaoRestApi;
	
	@Value("${kakao.redirect.uri}")
	private String kakaoRedirectUri;
	
	@Value("${kakao.clientSecret.key}")
	private String kakaoClientSecret;
	
	@Value("${storage.location}")
	private String storageLocation;
	
	@Autowired
	private JoinService joinService;

	// 약관 목록
	@RequestMapping(value = {"/terms/list"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
	public Object getTestList(
			  @RequestParam Map<String, Object> paramMap
			, @RequestParam(value = "termsTargetCode", required=false, defaultValue="A1701"  ) String termsTargetCode
	) throws Exception {

		TermsVo termsVo = new TermsVo();
		termsVo.setFd_terms_target_code(termsTargetCode);

		Map<String, Object> resultMap = joinService.getTermsList(termsVo);

		return new ResponseVo(200, resultMap);
	}

	// 가입회사 약관 입력 (body 요청값)
	@PostMapping("/terms/regist")
	public Object registTerms(
			  @RequestBody  Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "listCompanyTerms");
		throwException.requestBodyRequied( reqJsonObj, "listStaffTerms"  );
		joinService.registTerms(reqJsonObj);

		return new ResponseVo(200);
	}

	// 사용자 정보 입력 (body 요청값)
	@RequestMapping("/info/regist")
	public Object registInfo(
			@RequestParam(value="bizNum"      , required=false) final String bizNum
			, @RequestParam(value="companyName" , required=false) final String companyName
			, @RequestParam(value="companyId"   , required=false) final String companyId
			, @RequestParam(value="companyPhone", required=false) final String companyPhone
			, @RequestParam(value="companyFax"  , required=false) final String companyFax
			, @RequestParam(value="addrZipcode" , required=false) final String addrZipcode
			, @RequestParam(value="addrCommon"  , required=false) final String addrCommon
			, @RequestParam(value="addrDetail"  , required=false) final String addrDetail
			, @RequestParam(value="siteUrl"     , required=false) final String siteUrl
			, @RequestParam(value="staffName"   , required=false) final String staffName
			, @RequestParam(value="staffPw"     , required=false) final String staffPw
			, @RequestParam(value="staffDuty"   , required=false) final String staffDuty
			, @RequestParam(value="staffPhone"  , required=false) final String staffPhone
			, @RequestParam(value="staffMobile" , required=false) final String staffMobile
			, @RequestParam(value="staffEmail"  , required=false) final String staffEmail
			, @RequestParam(value="deptName"    , required=false) final String deptName
			, @RequestParam(value="deptRole"    , required=false) final String deptRole
			, @RequestParam(value="useYn"       , required=false) final String useYn
			, @RequestParam(value="masterYn"    , required=false) final String masterYn
			, @RequestParam(value="uploadFile"  , required=false) final MultipartFile[] uploadFile
	) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put( "bizNum"     .trim(),  bizNum        );
		paramMap.put( "companyName".trim(),  companyName   );
		paramMap.put( "companyId"  .trim(),  companyId     );
		paramMap.put( "companyPhone".trim(), companyPhone  );
		paramMap.put( "companyFax" .trim(),  companyFax    );
		paramMap.put( "addrZipcode".trim(),  addrZipcode   );
		paramMap.put( "addrCommon" .trim(),  addrCommon    );
		paramMap.put( "addrDetail" .trim(),  addrDetail    );
		paramMap.put( "siteUrl"    .trim(),  siteUrl       );
		paramMap.put( "staffName"  .trim(),  staffName     );
		paramMap.put( "staffPw"    .trim(),  staffPw       );
		paramMap.put( "staffDuty"  .trim(),  staffDuty     );
		paramMap.put( "staffPhone" .trim(),  staffPhone    );
		paramMap.put( "staffMobile".trim(),  staffMobile   );
		paramMap.put( "staffEmail" .trim(),  staffEmail    );
		paramMap.put( "deptName"   .trim(),  deptName      );
		paramMap.put( "deptRole"   .trim(),  deptRole      );
		paramMap.put( "useYn"      .trim(),  useYn         );
		paramMap.put( "masterYn"   .trim(),  masterYn      );
		paramMap.put( "uploadFile" .trim(),  uploadFile    );

		throwException.requestParamRequied(companyId, companyName, bizNum);
		joinService.registInfo(paramMap);

		return new ResponseVo(200);
	}

	// 직원 ID 중복체크
	@GetMapping("/staff/dup_check")
	public Object checkStaffId (
			@RequestParam(value="staffId"  , required=false) final String staffId
//			,@RequestParam(value="companyFk", required=false) final String companyFk
	) throws Exception {

		throwException.requestParamRequied( staffId );

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put( "staffId"	 , staffId 	 );
//		paramMap.put( "companyFk", companyFk );

		Map<String, Object> rtnMsg = new HashMap<>();
		rtnMsg  = joinService.checkStaffId(paramMap);

		return new ResponseVo(200, rtnMsg);
	}

	// 사업자등록번호 중복체크
	@GetMapping("/bizNum/dup_check")
	public Object dupCheckBizNumber (
			@RequestParam(value="bizNum", required=false) final String bizNum
	) throws Exception {

		throwException.requestParamRequied( bizNum );

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put( "bizNum", bizNum );

		Map<String, Object> rtnMsg = joinService.dupCheckBizNumber(paramMap);

		return new ResponseVo(200, rtnMsg);
	}

	@PostMapping("/uploadFiles")
	public Object uploadFileTest(
//			@RequestParam(value="formJson"     , required=false    ) final String formJson
			@RequestParam(value="uploadFile"   , required=false    ) final MultipartFile[] uploadFiles,
			@RequestParam(value="isImage"   , required=false    ) Boolean isImage

	) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put( "uploadFiles" , uploadFiles  );

		if(isImage&& Arrays.stream(uploadFiles).anyMatch(item->!(item.getContentType().split("/")[0].equalsIgnoreCase("image"))))
		{
			return new ResponseVo(400, "이미지만 업로드 가능합니다.");
		}
		//파일 저장경로를 리턴
		List<FileVo> fileVoList = joinService.fileUpload(paramMap);

		HashMap<String, Object> rtnMsg = new HashMap<>();
		if( fileVoList == null) {
			return new ResponseVo(500, rtnMsg);
		}
		
        rtnMsg.put("filePath"     , fileVoList.get(0).getFd_file_path() );
        rtnMsg.put("fd_company_logo_file_path", fileVoList.get(0).getFd_file_path() );
		rtnMsg.put("fd_company_logo_file_name", fileVoList.get(0).getFd_file_name());

//		return new ResponseEntity<Object>("Success", HttpStatus.OK);
		return new ResponseVo(200, rtnMsg);
	}


	//////////////////////////////////////// :: 회원가입 Proc :: ////////////////////////////////////////

	// 1. 가입회사 정보 등록
	@PostMapping("/company/regist")
	public Object registCompany (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "companyId", "companyName" );
		Map<String, Object> resultMap = joinService.registCompany( reqJsonObj );

		return new ResponseVo(200, resultMap);
	}
	
	// 2. 담당자 정보 등록
	@PostMapping("/staff/regist")
	public Object registStaff (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "fkCompany", "staffEmail", "staffPw", "staffName", "staffMobile" );
		Map<String, Object> resultMap = joinService.registStaff( reqJsonObj );
		
		return new ResponseVo(200, resultMap);
	}

	// 3. 가입회사 약관 동의
	@PostMapping("/company/terms/agree")
	public Object agreeTermsCompany (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "fkCompany", "fkTerms" );
		joinService.agreeTermsCompany( reqJsonObj );
		
		return new ResponseVo(200);
	}
	
	// 4. 담당자 약관 동의
	@PostMapping("/staff/terms/agree")
	public Object agreeTermsStaff (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "fkStaff", "fkTerms" );
		joinService.agreeTermsStaff( reqJsonObj );

		return new ResponseVo(200);
	}

	// 5. AI 직원 등록
	@PostMapping("/ai/staff/regist")
	public Object registStaffAi (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

	//	throwException.requestBodyRequied( reqJsonObj, "fkCompany", "fkWriter", "aiUid", "aiEmail", "fkStaffWorkCode" );

		Map<String, Object> resultMap = new HashMap<>();
		throwException.requestBodyRequied( reqJsonObj, "fkCompany", "fkWriter", "aiUid", "aiEmail");
		long pkCompanyStaff = joinService.registStaffAi( reqJsonObj );
		
		resultMap.put("pkCompanyStaff", pkCompanyStaff);
		if(pkCompanyStaff <= 0)
		{
			return new ResponseVo(400, "AI 생성중 오류가 발생했습니다.\n관리자에게 문의해주세요.");
		}
		return new ResponseVo(200, resultMap);
	}

	//////////////////////////////////////// :: 회원가입 Proc :: ////////////////////////////////////////

	// 회원가입 본인인증 요청
	@GetMapping("/auth")
	public Object joinAuth() throws Exception {

		Map<String, Object> resultMap = joinService.joinAuth();

		return new ResponseVo(200, resultMap);
	}
	
	// 본인인증 결과 복호화
	@PostMapping("/auth/result")
	public Object joinAuthResult(
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		Map<String, Object> resultMap = joinService.joinAuthResult(reqJsonObj);

		return new ResponseVo(200, resultMap);
	}

    // 회사 ID 중복체크
    @GetMapping("/company/dup_check")
    public Object checkCompanyId (
            @RequestParam(value="companyId" , required=false) final String companyId
    ) throws Exception {

        throwException.requestParamRequied( companyId );

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put( "companyId", companyId );

        Map<String, Object> rtnMsg = new HashMap<>();
        rtnMsg  = joinService.checkCompanyId(paramMap);

        return new ResponseVo(200, rtnMsg);
    }
	
	@GetMapping("/kakaoCallback")
    public String kakaoCallback(HttpServletRequest request) {
		String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://kauth.kakao.com/oauth/token";
        String code = request.getParameter("code");
        
        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + kakaoRestApi); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=" + kakaoRedirectUri); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            sb.append("&client_secret=" + kakaoClientSecret);
            
            
            //String encodeResult = URLEncoder.encode("UTF-8", kakaoRedirectUri);
            
            
            System.out.println(request.getServerName());
        	System.out.println("sb:" + sb);
            System.out.println("kakaoRestApi:" + kakaoRestApi);
            System.out.println("kakaoRedirectUri:" + kakaoRedirectUri);
            System.out.println("kakaoClientSecret:" + kakaoClientSecret);
            bw.write(sb.toString());
            bw.flush();

        	
            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode:" + responseCode);
            
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("result:" + result);
            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            
            

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_Token:" + access_Token);
            br.close();
            bw.close();
        }catch (IOException e) {
        	System.out.println("eee:" + e);
            e.printStackTrace();
        }
        
        return kakaoUser(access_Token);
    }
	
	@GetMapping("/kakaoUser")
    public String kakaoUser(String token) {
		String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
		JsonElement element = null;
		String result = "";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리로 JSON파싱
            /*
            JsonParser parser = new JsonParser();
            element = parser.parse(result);
            

            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }*/


            br.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("result:" + result);
        
		return result;
    }
	

    
}
