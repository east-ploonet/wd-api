package com.saltlux.aice_fe.ecApi.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saltlux.aice_fe.ecApi.service.NiceApiService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NiceApiServiceImpl extends BaseServiceImpl implements NiceApiService {

	@Autowired
	ThrowException throwException;

	@Value("${nice.api.base.url}")
	private String niceApiBaseUrl;

	@Value("${nice.api.crypto.token.url}")
	private String cryptoTokenUrl;

	@Value("${nice.api.client-id}")
	private String clientId;

	@Value("${nice.api.client-secret}")
	private String clientSecret;

	@Value("${nice.api.product-id}")
	private String productId;

	@Value("${nice.api.request.access-token}")
	private String accessToken;


	@Override
	public Map<String, Object> getCryptoToken() throws Exception {

		Map<String, Object> requestBody = new HashMap<>();
		Map<String, Object> dataHeader  = new HashMap<>();
		Map<String, Object> dataBody    = new HashMap<>();

		//-- request header --//

		StringBuffer sb = new StringBuffer();
		sb.append(accessToken).append(":").append(new Date().getTime()/1000).append(":").append(clientId);
		//
		final String encToken = "bearer " + Base64.encodeBase64String(sb.toString().getBytes());

		//-- request body --//

		dataHeader.put	( "CNTY_CD"		, "Ko" 		 );	// 이용언어 : ko, en, cn ...
		dataBody.put	( "req_dtim"	, FormatUtils.dateToFomat("yyyyMMddHHmmss") );	// 요청일시(YYYYMMDDHH24MISS)
		dataBody.put	( "req_no"		, FormatUtils.dateToFomat("yyyyMMddHHmmss") );	// 요청고유번호
		dataBody.put	( "enc_mode"	, "1" 		 );	// 사용할 암복호화 구분 (1:AES128/CBC/PKCS7)
		//
		requestBody.put	( "dataHeader"	, dataHeader );
		requestBody.put	( "dataBody"	, dataBody   );

		//-- --//

		Map<String, Object> resultMap = null;

		try {
			ResponseEntity<String> responseEntity = this.getWebClient()
					.method(HttpMethod.POST)
					.uri(uriBuilder -> uriBuilder.path(cryptoTokenUrl).build())
				    .header(HttpHeaders.AUTHORIZATION, encToken)
				    .header("ProductID", productId)
					.bodyValue(requestBody)
					.retrieve()
					.onStatus( httpStatus -> httpStatus.value() != 200, r -> Mono.empty())
					.toEntity(String.class)
					.block();

			Gson gson = new Gson();
			resultMap = gson.fromJson( responseEntity.getBody(), new TypeToken<Map<String, Object>>(){}.getType() );
			resultMap.putAll(dataBody);

			log.debug("********************************* nice api result *********************************");
			log.debug("request url   = {}", niceApiBaseUrl + cryptoTokenUrl);
			log.debug("http status   = {}", responseEntity.getStatusCode());
			log.debug("response body = {}", responseEntity.getBody());
			log.debug("********************************* nice api result *********************************");


		} catch (HttpClientErrorException ex) {

			log.error("********************************* nice api error *********************************");
			log.error("http status   = {}", ex.getStatusCode());
			log.error("response body = {}", ex.getResponseBodyAsString());
			log.error("********************************* nice api error *********************************");

		} catch (Exception ex) {

			log.error( ":: API Error :: " + ex.getMessage() );
		}

		return resultMap;

	}


	private WebClient getWebClient() {

		HttpClient httpClient = HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.responseTimeout(Duration.ofMillis(5000))
				.doOnConnected(conn ->
						conn.addHandlerLast(new ReadTimeoutHandler (5000, TimeUnit.MILLISECONDS))
							.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

		return WebClient.builder()
				.baseUrl(niceApiBaseUrl)
				.clientConnector(new ReactorClientHttpConnector(httpClient))
			    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();

	}
}
