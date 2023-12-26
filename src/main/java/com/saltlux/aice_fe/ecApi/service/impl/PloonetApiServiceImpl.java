package com.saltlux.aice_fe.ecApi.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.config.BadRequestException;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.channel.dao.ChannelDao;
import com.saltlux.aice_fe.pc.channel.vo.ChannelTranVo;
import com.saltlux.aice_fe.pc.credit.dao.CreditDao;
import com.saltlux.aice_fe.pc.credit.vo.BalanceBody;
import com.saltlux.aice_fe.pc.credit.vo.MyNewCreditItemVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.my_page.vo.MyPageOptionPostVo;
import com.saltlux.aice_fe.pc.my_page.vo.MyPageOptionVo;
import com.saltlux.aice_fe.pc.send.dao.SendDao;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PloonetApiServiceImpl extends BaseServiceImpl implements PloonetApiService {

    @Autowired
    ThrowException throwException;

    @Autowired
    private CreditDao creditDao;

    @Autowired
    private SendDao sendDao;

    @Autowired
    private ChannelDao channelDao;

    @Value("${ploonet.api.base.url}")
    private String ploonetApiBaseUrl;

    @Value("${ploonet.api.message.send.url}")
    private String messageSendUrl;

    @Value("${ploonet.api.billing.charge}")
    private String billingurl;

    @Value("${ploonet.api.billing.amount.url}")
    private String billingAmountUrl;

    @Value("${ploonet.api.message.title}")
    private String messageTitle;

    @Value("${ploonet.api.auth.id}")
    private String authId;

    @Value("${ploonet.api.auth.pw}")
    private String authPw;

    @Value("${ploonet.voicegw.api.url}")
    private String voicegwApi;

    @Value("${ploonet.configmanager.api.url}")
    private String configmanagerUrl;

    @Value("${ploonet.notification.api.url}")
    private String notificationUrl;

    @Value("${ploonet.msg.api.url}")
    private String msgApiUrl;

    @Value("${ploonet.api.talkbot.api.url}")
    private String talkbotApiUrl;

    @Value("${ploonet.api.talkbot.api.update.url}")
    private String talkbotApiUpdateUrl;

    @Autowired
    private Environment env;

    @Override
    public Map<String, Object> sendMessageApi(CompanyStaffVo companyStaffVo) throws Exception {

        final String BASIC_AUTHORIZATION = "basic " + Base64.encodeBase64String((authId + ":" + authPw).getBytes());

        //-- request Url
        final String requestUrl = messageSendUrl.replaceAll("\\{userId}", companyStaffVo.getFd_staff_id());

        System.out.println("requestURL : " + requestUrl);

        //-- request Body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("destination", companyStaffVo.getFd_staff_mobile());    // 메세지를 보낼 대상 (전화번호)
        //requestBody.put( "source"	  	, "18776116" 		);						// 메세지를 보내는 발신번호(알림톡인 경우 제외)
        requestBody.put("type", "AT");                        // 메세지 타입(SMS/MMS/LMS/AT/FT)
        requestBody.put("title", messageTitle);                        // 메세지 제목(MMS/LMS 전송시)
        requestBody.put("templateCode", "aicesvc010");                        // 메세지 등록 코드(카카오알림톡 전송시)
        String messageContent = env.getProperty("ploonet.api.message.content");
        messageContent = messageContent.replaceAll("\\{COMPANY}", companyStaffVo.getFd_company_name())
                .replaceAll("\\{NAME}", companyStaffVo.getFd_staff_name())
                .replaceAll("\\{DESTNAME}", companyStaffVo.getFd_staff_name())
                .replaceAll("\\{INVITECODE}", companyStaffVo.getFd_signup_keycode());

        requestBody.put("messages", messageContent);                        // 메세지 본문 내용

		/*
		[#{COMPANY}]
		안녕하세요.
		워크센터 서비스 담당자 #{NAME}님이 #{DESTNAME}님에게 워크센터 초청코드를 보내셨습니다.

		* 회사명 : [#{COMPANY}]
		* 담당자명 : [#{DESTNAME}]
		* 초대코드 : [#{INVITECODE}]

		WORK+를 이용하면 담당자님께 접수되는 문의건을 AI 직원이 빠르게 전달해드립니다.
		플루닛 WORK+ 앱을 설치하시고, 전달받은 초대코드와 함께 담당자님 정보로 인증하시면 앱 사용이 가능합니다.
		AI 직원과 함께 하는 플루닛 서비스를 이용해 주셔서 감사합니다.

		WORK+는 애플 앱스토어나 구글 플레이스토어에서 다운 받으실 수 있습니다.

		*Android 앱 다운로드 바로가기
		구글플레이에서 '플루닛 워크+' 검색 → 워크+ 다운로드

		*iOS 앱 다운로드 바로가기
		앱스토어에서 '플루닛 워크+' 검색 → 워크+ 다운로드
		*/

        //-- --//

        Map<String, Object> resultMap = null;

        try {
            String ploonetBaseUrlApi = ploonetApiBaseUrl.replaceAll("/workapi", ""); // workapi 를 api로 수정 (2023 08 01)
            System.out.println("ploonetBaseUrlApi : " + ploonetBaseUrlApi);
            System.out.println("ploonetBaseUrlApi url   = " + ploonetBaseUrlApi + requestUrl);

            ResponseEntity<String> responseEntity = this.getWebClient(ploonetBaseUrlApi)
                    .method(HttpMethod.POST)
                    .uri(uriBuilder -> uriBuilder.path(requestUrl).build())
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTHORIZATION)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.value() != 200, r -> Mono.empty())
                    .toEntity(String.class)
                    .block();

            Gson gson = new Gson();
            resultMap = gson.fromJson(responseEntity.getBody(), new TypeToken<Map<String, Object>>() {
            }.getType());

            log.debug("********************************* ploonet api result *********************************");
            log.debug("request url   = {}", ploonetApiBaseUrl + requestUrl);
            log.debug("http status   = {}", responseEntity.getStatusCode());
            log.debug("response body = {}", responseEntity.getBody());
            log.debug("********************************* ploonet api result *********************************");


        } catch (HttpClientErrorException ex) {

            log.error("********************************* ploonet api error *********************************");
            log.error("http status   = {}", ex.getStatusCode());
            log.error("response body = {}", ex.getResponseBodyAsString());
            log.error("********************************* ploonet api error *********************************");

        } catch (Exception ex) {

            log.error(":: API Error :: " + ex.getMessage());
        }

        return resultMap;

    }

    @Override
    public Map<String, Object> getUserBillingApi(CompanyStaffVo companyStaffVo) throws Exception {

        final String BASIC_AUTHORIZATION = "basic " + Base64.encodeBase64String((authId + ":" + authPw).getBytes());

        //-- request Url
        final String requestUrl = billingAmountUrl.replaceAll("\\{companySeq}", Long.toString(companyStaffVo.getFk_company()));

        //-- --//

        Map<String, Object> resultMap = null;

        try {
            ResponseEntity<String> responseEntity = this.getWebClient()
                    .method(HttpMethod.GET)
                    .uri(uriBuilder -> uriBuilder.path(requestUrl).build())
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTHORIZATION)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.value() != 200, r -> Mono.empty())
                    .toEntity(String.class)
                    .block();

            Gson gson = new Gson();
            resultMap = gson.fromJson(responseEntity.getBody(), new TypeToken<Map<String, Object>>() {
            }.getType());

            log.debug("********************************* ploonet api result *********************************");
            log.debug("request url   = {}", ploonetApiBaseUrl + requestUrl);
            log.debug("http status   = {}", responseEntity.getStatusCode());
            log.debug("response body = {}", responseEntity.getBody());
            log.debug("********************************* ploonet api result *********************************");


        } catch (HttpClientErrorException ex) {

            log.error("********************************* ploonet api error *********************************");
            log.error("http status   = {}", ex.getStatusCode());
            log.error("response body = {}", ex.getResponseBodyAsString());
            log.error("********************************* ploonet api error *********************************");

        } catch (Exception ex) {

            log.error(":: API Error :: " + ex.getMessage());
        }

        return resultMap;

    }

    @Override
    public void sendBillEntry(UserReqBody reqBody, long pkPgPayLog) {
        HttpResponse<String> response = null;
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("creditType", "LT");
        jsonMap.put("itemStatus", "B20102");
        jsonMap.put("servicePlan", "LT011");
        jsonMap.put("servicePlanDc", "DCM01");

        //##############@@@@@@@@@@@@@@@@@@
        jsonMap.put("itemCd", "CARD_SM_001");
        jsonMap.put("itemType", "SUBSC_MAIN" );
        jsonMap.put("itemName", "엔트리");
        jsonMap.put("pkPgPayLog", pkPgPayLog);

        JSONObject body = new JSONObject();
        body.put("solutionType", "B11");
        body.put("userType", reqBody.getUser_type());
        body.put("companySeq", reqBody.getPk_company());
        body.put("companyStaffSeq", reqBody.getPk_company_staff());
        body.put("plan", jsonMap);

        try {
            response = Unirest.post(billingurl + "/charge")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getBalance(PcLoginInfoVo pcLoginInfoVo) {
        HttpResponse<String> response = null;
        try {
            String url = "";
            url = billingurl + "/credit/info/B11/" + pcLoginInfoVo.getLoginCompanyPk();
            response = Unirest.get(url)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        int sum = 0;
        
        /*String body = response.getBody();
        Type listType = new TypeToken<ArrayList<BalanceBody>>() {
        }.getType();
        final ArrayList<BalanceBody>[] list = new ArrayList[]{new Gson().fromJson(body, listType)};
        int sum = 0;
        for (ArrayList<BalanceBody> balanceBodies : list) {
            for (BalanceBody name : balanceBodies) {
                if (name.getCreditType().equals("PC")) {
                    sum -= name.getAmount();
                } else {
                    sum += name.getAmount();
                }
            }
        }*/

        return sum;
    }

    //잔액조회 신규
    @Override
    public Map<String, Object>  getMyCreditInfo(PcLoginInfoVo pcLoginInfoVo) {
        Map<String, Object> resultMap = new HashMap<>();

        HttpResponse<JsonNode> response = null;

        //잔액 조회
        try {
            String url = "";
            url = billingurl + "/v1/credit/left/B11/" + pcLoginInfoVo.getLoginCompanyPk();
            response = Unirest.get(url).asJson();
            System.out.println("url:" + url);
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        JSONArray result = new JSONArray();
        int creditPolicy = 0;

        result = response.getBody().getObject().getJSONArray("resultValue");

        if(result != null && result.length() > 0) {
            for (int i = 0; i < result.length(); i++) {
                JSONObject resultJson = new JSONObject();
                resultJson = (JSONObject) result.get(i);
                String itemCd = resultJson.getString("itemCd");
                if(!itemCd.equals("CARD_MB_001")) creditPolicy += resultJson.getLong("credit");
            }
        }

        resultMap.put("creditPolicy", creditPolicy);

        // 구독중인 상품
        try {
            String url = "";
            url = billingurl + "/v1/credit/info/B11/" + pcLoginInfoVo.getLoginCompanyPk();
            response = Unirest.get(url).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        JSONObject resultObj = new JSONObject();
        resultObj = response.getBody().getObject().getJSONObject("resultValue");
        resultMap.put("creditName", resultObj.get("itemName"));
        resultMap.put("itemCd", resultObj.get("itemCd"));

        return resultMap;
    }



    //신규 크레딧 추가
    @Override
    public void fristSendBill(MyNewCreditItemVo myNewCreditItemVo, long loginCompanyPk, long pkPgPayLog) {
        HttpResponse<String> response = null;

        JSONObject jsonMap = new JSONObject();
        jsonMap.put("itemCd", myNewCreditItemVo.getPp_card_cd());
        jsonMap.put("itemType", myNewCreditItemVo.getPp_card_type() );
        jsonMap.put("itemName", myNewCreditItemVo.getPp_card_name());
        jsonMap.put("itemStatus", "B20102");
        jsonMap.put("pkPgPayLog", pkPgPayLog);

        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String stringDate = DateFor.format(date);
        jsonMap.put("payDtFrom", stringDate);

        JSONArray items = new JSONArray();
        items.put(jsonMap);

        JSONObject body = new JSONObject();
        body.put("solutionType", "B11");
        body.put("fkCompany", loginCompanyPk);
        body.put("items", items);


        try {
            response = Unirest.post(billingurl + "/credit/plus")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asString();
        } catch (UnirestException e) {
            System.out.println("!!!!!!!!!!!!!!빌링오류:" + e);
            e.printStackTrace();
        }
    }


    @Override
    public void firstSendBillEntry(MyNewCreditItemVo myNewCreditItemVo, long loginCompanyPk, long pkPgPayLog) {
        HttpResponse<String> response = null;

        JSONObject jsonMap = new JSONObject();
        jsonMap.put("itemCd", "CARD_SM_001");
        jsonMap.put("itemType", "SUBSC_MAIN" );
        jsonMap.put("itemName", "엔트리");
        jsonMap.put("itemStatus", "B20102");
        jsonMap.put("pkPgPayLog", pkPgPayLog);

        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String stringDate = DateFor.format(date);
        jsonMap.put("payDtFrom", stringDate);

        JSONArray items = new JSONArray();
        items.put(jsonMap);

        JSONObject body = new JSONObject();
        body.put("solutionType", "B11");
        body.put("fkCompany", loginCompanyPk);
        body.put("items", items);


        try {
            response = Unirest.post(billingurl + "/credit/plus")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asString();
        } catch (UnirestException e) {
            System.out.println("!!!!!!!!!!!!!!빌링오류:" + e);
            e.printStackTrace();
        }
    }

    //요금제 해지 환불금액 산출
    @Override
    public int creditClose(long loginCompanyPk) {
        HttpResponse<JsonNode> response = null;

        JSONObject body = new JSONObject();
        body.put("solutionType", "B11");
        body.put("fkCompany", loginCompanyPk);

        try {

            response = Unirest.post(billingurl + "/credit/close")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
        } catch (UnirestException e) {
            System.out.println("!!!!!!!!!!!!!!빌링오류:" + e);
            response = null;
            e.printStackTrace();
            return -1;
        }

        JSONArray result = new JSONArray();
        int costPayback = 0;
        result = response.getBody().getObject().getJSONArray("resultValue");
        if(result != null && result.length() > 0) {
            for (int i = 0; i < result.length(); i++) {
                JSONObject resultJson = new JSONObject();
                resultJson = (JSONObject) result.get(i);
                costPayback += resultJson.getLong("costPayback");

            }
        }

        return costPayback;

    }

    @Override
    public void registerCredit(PcLoginInfoVo pcLoginInfoVo, List<MyPageOptionVo> req) {
        HttpResponse<String> response = null;
        List<MyPageOptionPostVo> listItem = creditDao.getOptionCredit(req);
        Map<String, List<MyPageOptionVo>> collect = req.stream().collect(Collectors.groupingBy(item -> item.getOptionId()));
        listItem.forEach(item -> {
            if (collect.get(item.getItemCd()) != null) {
                MyPageOptionVo myPageOptionVo = collect.get(item.getItemCd()).get(0);
                item.setItemCount(myPageOptionVo.getCount());
                if (myPageOptionVo.getTerm() == 12) {
                    item.setItemTermUnit("Y");
                    item.setItemTermCount(1);
                } else {
                    item.setItemTermUnit("M");
                    item.setItemTermCount(myPageOptionVo.getTerm());
                }

                item.setServiceDtFrom(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                item.setServiceDtTo(LocalDateTime.now().plusMonths(myPageOptionVo.getTerm()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                item.setAmountCredit(myPageOptionVo.getAmount());
            }
        });

        JSONObject body = new JSONObject();
        body.put("solutionType", "B11");
        body.put("userType", pcLoginInfoVo.getLoginUserType());
        body.put("companySeq", pcLoginInfoVo.getLoginCompanyPk());
        body.put("channelType", "voice");
        body.put("listItem", listItem);
        try {
            response = Unirest.post(billingurl + "/deduct")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendChargeCredit(PcLoginInfoVo pcLoginInfoVo, int money) {
        HttpResponse<JsonNode> response = null;
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("creditType", "LL");
        jsonMap.put("itemStatus", "B20103");
        jsonMap.put("amountCredit", money * 2);


        JSONObject body = new JSONObject();
        body.put("solutionType", "B11");
        body.put("userType", pcLoginInfoVo.getLoginUserType());
        body.put("companySeq", pcLoginInfoVo.getLoginCompanyPk());
        body.put("companyStaffSeq", pcLoginInfoVo.getLoginCompanyStaffPk());
        body.put("plan", jsonMap);

        try {
            response = Unirest.post(billingurl + "/charge")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
            if (!response.getBody().getObject().get("code").toString().equals("0000")) {
                throw new BadRequestException(response.getBody().getObject().get("messages").toString() + "(" + response.getBody().getObject().get("code").toString() + ")");
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }


    private WebClient getWebClient() {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(ploonetApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

    }

    private WebClient getWebClient(String ploonetBaseUrlApi) {
        //ploonetBaseUrlApi 이거 workApi 대신에 api 날려야 해서 url을 직접 받을 예정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(ploonetBaseUrlApi)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

    }


    @Override
    public Map<String, Object> sendMessageIndividualApi(CompanyStaffVo companyStaffVo) throws Exception {

        final String BASIC_AUTHORIZATION = "basic " + Base64.encodeBase64String((authId + ":" + authPw).getBytes());

        //-- request Url
        final String requestUrl = messageSendUrl.replaceAll("\\{userId}", companyStaffVo.getFd_staff_id());

        //-- request Body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("destination", companyStaffVo.getFd_staff_mobile());    // 메세지를 보낼 대상 (전화번호)
        //requestBody.put( "source"	  	, "18776116" 		);						// 메세지를 보내는 발신번호(알림톡인 경우 제외)
        requestBody.put("type", "AT");                        // 메세지 타입(SMS/MMS/LMS/AT/FT)
        requestBody.put("title", messageTitle);                        // 메세지 제목(MMS/LMS 전송시)
        requestBody.put("templateCode", "aicesvc010");                        // 메세지 등록 코드(카카오알림톡 전송시)

        String messageContent = env.getProperty("ploonet.api.message.content");
        messageContent = messageContent
                .replaceAll("\\{COMPANY}", companyStaffVo.getFd_staff_name())
                .replaceAll("\\{NAME}", companyStaffVo.getFd_company_id())
                .replaceAll("\\{DESTNAME}", companyStaffVo.getFd_staff_name())
                .replaceAll("\\{INVITECODE}", companyStaffVo.getFd_signup_keycode());

        requestBody.put("messages", messageContent);                        // 메세지 본문 내용

		/*
		[#{COMPANY}]
		안녕하세요.
		워크센터 서비스 담당자 #{NAME}님이 #{DESTNAME}님에게 워크센터 초청코드를 보내셨습니다.

		* 회사명 : [#{COMPANY}]
		* 담당자명 : [#{DESTNAME}]
		* 초대코드 : [#{INVITECODE}]

		WORK+를 이용하면 담당자님께 접수되는 문의건을 AI 직원이 빠르게 전달해드립니다.
		플루닛 WORK+ 앱을 설치하시고, 전달받은 초대코드와 함께 담당자님 정보로 인증하시면 앱 사용이 가능합니다.
		AI 직원과 함께 하는 플루닛 서비스를 이용해 주셔서 감사합니다.

		WORK+는 애플 앱스토어나 구글 플레이스토어에서 다운 받으실 수 있습니다.

		*Android 앱 다운로드 바로가기
		구글플레이에서 '플루닛 워크+' 검색 → 워크+ 다운로드

		*iOS 앱 다운로드 바로가기
		앱스토어에서 '플루닛 워크+' 검색 → 워크+ 다운로드
		*/

        //-- --//

        Map<String, Object> resultMap = null;

        try {
            String ploonetBaseUrlApi = ploonetApiBaseUrl.replaceAll("workapi", "api"); // workapi 를 api로 수정 (2023 08 01)
            ResponseEntity<String> responseEntity = this.getWebClient(ploonetBaseUrlApi)
                    .method(HttpMethod.POST)
                    .uri(uriBuilder -> uriBuilder.path(requestUrl).build())
                    .header(HttpHeaders.AUTHORIZATION, BASIC_AUTHORIZATION)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.value() != 200, r -> Mono.empty())
                    .toEntity(String.class)
                    .block();

            Gson gson = new Gson();
            resultMap = gson.fromJson(responseEntity.getBody(), new TypeToken<Map<String, Object>>() {
            }.getType());

            log.debug("********************************* ploonet api result *********************************");
            log.debug("request url   = {}", ploonetApiBaseUrl + requestUrl);
            log.debug("http status   = {}", responseEntity.getStatusCode());
            log.debug("response body = {}", responseEntity.getBody());
            log.debug("********************************* ploonet api result *********************************");


        } catch (HttpClientErrorException ex) {

            log.error("********************************* ploonet api error *********************************");
            log.error("http status   = {}", ex.getStatusCode());
            log.error("response body = {}", ex.getResponseBodyAsString());
            log.error("********************************* ploonet api error *********************************");

        } catch (Exception ex) {

            log.error(":: API Error :: " + ex.getMessage());
        }

        return resultMap;

    }


    @Override
    public Map<String, Object> acsJobCommand(String dn, String command, String jobCode, String startTime, String [] reqNumbers) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        if(Common.isBlank(dn) || Common.isBlank(command) || Common.isBlank(jobCode)) {
            resultMap.put("messages", "필수값 없음!!!");
            return resultMap;
        }

        String jobName = jobCode;
        String numbers = null;

        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();

        if(command.equals("create") && (reqNumbers == null || reqNumbers.length == 0)) {
            resultMap.put("messages", "연락처 필수값 없음!!!");
            return resultMap;
        }else if(command.equals("create")) {
            String strArrayToString  = Arrays.toString(reqNumbers);
            strArrayToString = strArrayToString.replace("[", "");
            strArrayToString = strArrayToString.replace("]", "");
            body.put("numbers", strArrayToString);
        }


        body.put("command", command);
        body.put("jobCode", jobCode);
        body.put("jobName", jobName);
        body.put("svcType", "BULK_CALL");


        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

        Map<String, Object> param = new HashMap<>();

        param.put("fkCompany", loginInfoVo.getLoginCompanyPk());
        param.put("fk_company", loginInfoVo.getLoginCompanyPk());
        DataMap dnisDt = sendDao.selectDnis(param);

        DataMap defaultYn = channelDao.getFdNationwide(param);
        if(defaultYn != null) {
            body.put("callerId", defaultYn.get("bizPhoneNum"));
        }


        body.put("companySeq", dnisDt.get("fkCompany").toString());
        body.put("aiStaffSeq", dnisDt.get("fkCompanyStaffAi").toString());



        if(startTime != null) body.put("startTime", startTime);
        try {
            String apiUrl = voicegwApi + "/acs/" + dnisDt.get("fullDnis").toString();

            System.out.println("job!!!!!!!!!!!!!:" + apiUrl);
            System.out.println(body);

            response = Unirest.post(apiUrl)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
            System.out.println("결과 확인:" + response.getBody());
            resultMap.put("messages", response.getBody().getObject().get("cause").toString());
        } catch (Exception e) {
            System.out.println("!!!!!!ACS ERROR:" + e);
            log.error("!!!!!!ACS ERROR:" + e);
        }

        return resultMap;

    }

    @Override
    public Map<String, Object> acsJobStart(String dn, String command, String jobCode, String startTime, String [] reqNumbers, String reserveDt) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Thread.sleep(2000);
        if(Common.isBlank(dn) || Common.isBlank(command) || Common.isBlank(jobCode)) {
            resultMap.put("messages", "필수값 없음!!!");
            return resultMap;
        }

        String jobName = jobCode;
        String numbers = null;

        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();

        if(command.equals("create") && (reqNumbers == null || reqNumbers.length == 0)) {
            resultMap.put("messages", "연락처 필수값 없음!!!");
            return resultMap;
        }else if(command.equals("create")) {
            String strArrayToString  = Arrays.toString(reqNumbers);
            strArrayToString = strArrayToString.replace("[", "");
            strArrayToString = strArrayToString.replace("]", "");
            body.put("numbers", strArrayToString);
        }


        body.put("command", command);
        body.put("jobCode", jobCode);
        body.put("jobName", jobName);
        if(!reserveDt.equals("00000000000000")) body.put("startTime", reserveDt);
        body.put("svcType", "BULK_CALL");


        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

        Map<String, Object> param = new HashMap<>();

        param.put("fk_company", loginInfoVo.getLoginCompanyPk());
        param.put("fkCompany", loginInfoVo.getLoginCompanyPk());
        DataMap dnisDt = sendDao.selectDnis(param);

        DataMap defaultYn = channelDao.getFdNationwide(param);
        if(defaultYn != null) {
            body.put("callerId", defaultYn.get("bizPhoneNum"));
        }


        body.put("companySeq", dnisDt.get("fkCompany").toString());
        body.put("aiStaffSeq", dnisDt.get("fkCompanyStaffAi").toString());



        if(startTime != null) body.put("startTime", startTime);


        try {
            String apiUrl = voicegwApi + "/acs/" + dnisDt.get("fullDnis").toString();

            System.out.println("job!!!!!!!!!!!!!:" + apiUrl);
            System.out.println(body);

            response = Unirest.post(apiUrl)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
            System.out.println(response.getBody());
            resultMap.put("messages", response.getBody().getObject().get("cause").toString());
        } catch (Exception e) {
            System.out.println("!!!!!!ACS ERROR:" + e);
            log.error("!!!!!!ACS ERROR:" + e);
        }

        return resultMap;

    }

    @Override
    public Map<String, Object> acsJobPause(String dn, String command, String jobCode) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        if(Common.isBlank(dn) || Common.isBlank(command) || Common.isBlank(jobCode)) {
            resultMap.put("messages", "필수값 없음!!!");
            return resultMap;
        }

        String jobName = jobCode + command;

        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();

        body.put("command", "stop");
        body.put("jobCode", jobCode);
        body.put("jobName", jobName);
        body.put("svcType", "BULK_CALL");

        try {
            response = Unirest.post(voicegwApi + "/acs/" + dn)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
            resultMap.put("messages", response.getBody().getObject().get("cause").toString());
        } catch (Exception e) {
            System.out.println("!!!!!!ACS ERROR:" + e);
            log.error("!!!!!!ACS ERROR:" + e);
        }

        return resultMap;

    }


    @Override
    public JSONArray voiceNumbers(int type, String dnisType) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();
        body.put("command", "show");

        try {
            response = Unirest.post(configmanagerUrl + "/aice/configManager/dnis/numberPlan")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
            log.info("!!!!!!vocie numberPlan :" + response.getBody());
        } catch (Exception e) {
            System.out.println("!!!!!!VOICE NUMBERS:" + e);
            log.error("!!!!!!VOICE NUMBERS:" + e);
        }

        String resultMsg = response.getBody().getObject().get("status").toString();
        JSONObject dataObject = new JSONObject();
        JSONArray resultArr = new JSONArray();
        JSONArray useNumbers = new JSONArray();
        JSONObject numberPlansObj = new JSONObject();

        if(resultMsg.equals("success")) {
            dataObject = response.getBody().getObject().getJSONObject("data");
            resultArr = (JSONArray) dataObject.get("numberPlans");

            int resultSize = resultArr.length();
            int cnt = 0;
            for (int i = 0; i < resultSize; i++) {
                numberPlansObj = (JSONObject) resultArr.get(i);
                String inUse = numberPlansObj.getString("inUse");
                String voiceType = numberPlansObj.getString("type");

                if(inUse.equals("no") && voiceType.equals(dnisType)) {
                    useNumbers.put(numberPlansObj);
                    cnt ++;
                }
                if(type == 1 && cnt == 1) break;
            }
        }else {
            return null;
        }
        return useNumbers;
    }

    @Override
    public Map<String, Object> tempDnisRegist(String userType, long fkCompany, long fkCompanyStaffAi, JSONObject number) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();
        body.put("solution_type", "B11");
        body.put("user_type", userType);

        body.put("fk_company", fkCompany);
        body.put("fk_company_staff_ai", fkCompanyStaffAi);

        String fullDnis = number.getString("number");
        String vgwId = number.getString("vgwId");


        body.put("fd_dnis", fullDnis.substring(3));
        body.put("full_dnis", fullDnis);
        body.put("vgw_id", vgwId);

        try {
            response = Unirest.post(configmanagerUrl + "/aice/configManager/dnis/temp/regist")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
            System.out.println("response:" + response.getBody());
            log.info("!!!!!!Config manager response :" + response.getBody());
        } catch (Exception e) {
            System.out.println("!!!!!!Config manager Error :" + e);
            log.error("!!!!!!Config manager Error :" + e);
            resultMap = null;
        }


        return resultMap;
    }

    @Override
    public Map<String, Object> tempDnisDelete(String fullDnis, String dnis, long fkCompany, long fkCompanyStaffAi) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();


        body.put("fk_company", fkCompany);
        body.put("fk_company_staff_ai", fkCompanyStaffAi);
        body.put("full_dnis", fullDnis);

        JSONObject postBody = new JSONObject();
        postBody.put("command", "show");
        postBody.put("numberType", "temp");

        String numberType = null;
        try {
            response = Unirest.post(voicegwApi + "/voicegw/numbers/" + fullDnis)
                    .header("Content-Type", "application/json")
                    .body(postBody)
                    .asJson();

            JSONObject numbers =  response.getBody().getObject().getJSONObject("numbers");
            numberType = numbers.get("numberType").toString();
            System.out.println("!!!!!!!dnis temp 확인!!!!!!!!!!!:" + numberType);
        } catch (Exception e) {
            System.out.println("e" + e);
            numberType = null;
        }

        try {
            if(numberType != null && numberType.equals("temp")) {
                response = Unirest.delete(configmanagerUrl + "/aice/configManager/dnis/temp/" + dnis)
                        .header("Content-Type", "application/json")
                        .body(body)
                        .asJson();
                resultMap.put("status", response.getStatus());
                log.error("!!!!!!Config manager delete Error :" + configmanagerUrl + "/aice/configManager/dnis/temp/" + dnis);
            }
        } catch (Exception e) {
            System.out.println("!!!!!!Config manager delete Error body!!!!!!!:" + body);
            log.error("!!!!!!Config manager delete Error body!!!!!!!:" + body);

            System.out.println("!!!!!!Config manager delete Error url!!!!!!!:" + configmanagerUrl + "/aice/configManager/dnis/temp/" + dnis);
            log.error("!!!!!!Config manager delete Error :" + configmanagerUrl + "/aice/configManager/dnis/temp/" + dnis);

            System.out.println("!!!!!!Config manager delete Error :" + e);
            log.error("!!!!!!Config manager delete Error :" + e);
            resultMap = null;
        }


        return resultMap;
    }

    @Override
    public Map<String, Object> makeCallAPi(String command, String number, String destinationNumber, String vgwId, String channelType, String callType, String language, String svcType, String aiStaffSeq) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();
        if(vgwId != null ) body.put("vgwId", vgwId);
        body.put("command", command);

        body.put("destinationNumber", destinationNumber);
        body.put("exten", number);
        body.put("callType", callType);
        body.put("channelType", channelType);
        body.put("language", language);
        body.put("svcType", svcType);
        body.put("aiStaffSeq", aiStaffSeq);
        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
        body.put("companySeq", String.valueOf(loginInfoVo.getLoginCompanyPk()));


        System.out.println("!!!!!!!!!!!!!!!body:" + body);
        System.out.println("voicegw!!!!!!:" + voicegwApi + "/voicegw/devices/" + number);

        try {
            response = Unirest.post(voicegwApi + "/voicegw/devices/" + number)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();

            resultMap.put("status", response.getStatus());

            if(response.getStatus() == 200) {
                resultMap.put("messages", response.getBody().getObject().get("response").toString());
            }else {
                resultMap.put("messages", response.getBody().getObject().get("response").toString());
            }

        } catch (Exception e) {
            System.out.println("!!!!!!makeCallAPi manager Error :" + e);
            log.error("!!!!!!makeCallAPi manager Error :" + e);
            resultMap = null;
        }


        return resultMap;
    }


    @Override
    public Map<String, Object> dnisSave(String userType, long fkCompany, long fkCompanyStaffAi, String vgwId, String number) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();
        body.put("solution_type", "B11");
        body.put("user_type", userType);

        body.put("fk_company", fkCompany);
        body.put("fk_company_staff_ai", fkCompanyStaffAi);


        body.put("fd_dnis", number.substring(3));
        body.put("full_dnis", number);
        body.put("vgw_id", vgwId);
        body.put("interj_yn", "N");


        try {
            response = Unirest.post(configmanagerUrl + "/aice/configManager/dnis/save")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
            System.out.println("response:" + response);
            resultMap.put("status", response.getStatus());
            System.out.println(response.getStatus());

            //착신번호 설정 시 대표번호 자동등록
//            if(response.getStatus() == 200) {
//                PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
//
//                ChannelTranVo settingTranVo = new ChannelTranVo();
//                settingTranVo.setUser_type(loginInfoVo.getLoginUserType());
//                settingTranVo.setFd_use("Y");
//                settingTranVo.setFk_company(loginInfoVo.getLoginCompanyPk());
//                settingTranVo.setFd_nationwide("Y");
//                //settingTranVo.setFk_company_staff(fkCompanyStaff);
//                settingTranVo.setFd_tarn_num(number);
//                settingTranVo.setFd_tarn_name("기본발신번호");
//
//                settingTranVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
//                settingTranVo.setReg_status("COMPLETE");
//
//                Map<String, Object> paramMapInsert = new HashMap();
//                paramMapInsert.put("settingTranVo", settingTranVo);
//                int result = channelDao.tranRegist(paramMapInsert);
//
//            }

        } catch (Exception e) {
            System.out.println("!!!!!!Config manager Error :" + e);
            log.error("!!!!!!Config manager Error :" + e);
            resultMap = null;
        }


        return resultMap;
    }

    @Override
    public Map<String, Object> msgSend(long fkCompany, String noticeCheckType, String msgType, String templateCode, String idFrom, String idTo, String title, String bodyMsg, String channelType) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();

        body.put("fkCompany", fkCompany);
        body.put("solutionType", "B11");

        body.put("noticeCheckType", noticeCheckType);
        body.put("msgType", msgType);
        //body.put("templateCode", templateCode);
        body.put("channelType", channelType);
        body.put("refId", "");
        body.put("reserveDt", "00000000000000");


        body.put("idFrom", "0221931600");
        body.put("idTo", idTo);
        body.put("title", title);
        body.put("body", bodyMsg);
        body.put("payYn", "N");
        //body.put("svcType", "OJT_INTERVIEW");

        //System.out.println(body);
        try {
            response = Unirest.post(msgApiUrl + "/messages/v1/send/sms")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
            resultMap.put("status", response.getStatus());

        } catch (Exception e) {
            System.out.println("!!!!!!Config manager Error :" + e);
            log.error("!!!!!!Config manager Error :" + e);
            resultMap = null;
        }


        return resultMap;
    }


    @Override
    public Map<String, Object> msgBulkSend(long fkCompany, String noticeCheckType, String msgType, String templateCode, String idFrom, String [] idTo, String title, String bodyMsg, String channelType, String reserveDt, String jobCode) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();
        String jobName = jobCode;

        body.put("fkCompany", fkCompany);
        body.put("solutionType", "B11");

        body.put("noticeCheckType", noticeCheckType);
        body.put("msgType", msgType);
        //body.put("templateCode", templateCode);
        body.put("channelType", channelType);
        body.put("refId", "");
        body.put("reserveDt", reserveDt);

        body.put("idFrom", idFrom);
        body.put("idsTo", idTo);
        body.put("title", title);
        body.put("body", bodyMsg);
        body.put("svcType", "BULK_SMS");
        body.put("jobCode", jobCode);

        try {
            response = Unirest.post(msgApiUrl + "/messages/v1/send/bulk/sms")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asJson();
            resultMap.put("status", response.getStatus());

        } catch (Exception e) {
            System.out.println("!!!!!!Config manager Error :" + e);
            log.error("!!!!!!Config manager Error :" + e);
            resultMap = null;
        }


        return resultMap;
    }

    @Override
    /** type  
     *  1. 회사 정보 (info)
     *  2. 업무 메뉴얼 (menual)
     *	3. AI직원 업무시간 (operation)
     */
    public Map<String, Object> talkbotBrokerUpdate(long fkCompany, long fkCompanyStaffAi, String type) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        HttpResponse<JsonNode> response = null;
        JSONObject body = new JSONObject();

        body.put("companySeq", fkCompany);
        body.put("staffSeq", fkCompanyStaffAi);
        body.put("channel", "voice");
        // body.put("type", type);

        String url = "";
        System.out.println("@@@@@@@@@@@@@@:" + type);

        if (type != null && type.equals("1")) {
            body.put("type", "info");
            System.out.println("@@@@@info@@@@@:" + body);
        }

        if (type != null && type.equals("2")) {
            body.put("type", "menual");
            try {
                response = Unirest.put(talkbotApiUpdateUrl)
                        .header("Content-Type", "application/json")
                        .body(body)
                        .asJson();

            } catch (Exception e) {
                System.out.println("!!!!!!talkbotApiUrl Error :" + e);
                log.error("!!!!!!talkbotApiUrl Error :" + e);
                resultMap = null;
            }
        }

        return resultMap;
    }

}
