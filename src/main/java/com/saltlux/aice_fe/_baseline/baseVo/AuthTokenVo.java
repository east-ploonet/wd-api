package com.saltlux.aice_fe._baseline.baseVo;

import com.saltlux.aice_fe._baseline.security.AESUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;

@Slf4j
@Component
public class AuthTokenVo {

    @Value("${auth.encrypt.key}")
    static String   authEncryptKey;
    @Value("${auth.encrypt.key}")
    private void setKey(String authEncryptKey){
	    AuthTokenVo.authEncryptKey = authEncryptKey;
    }

    @Value("${auth.encrypt.iv}")
    static String   authEncryptIv;
    @Value("${auth.encrypt.iv}")
    private void setIv(String authEncryptIv){
	    AuthTokenVo.authEncryptIv = authEncryptIv;
    }

    @Getter @Setter private String token;

    @Getter @Setter private String timeStamp;

    @Getter @Setter private String memberPk;

	@Getter @Setter private String signupCode;

    private AuthTokenVo() {
        this("");
    }

	private AuthTokenVo(String memberPk) {

		this.memberPk       = memberPk;
		this.timeStamp      = Long.toString( System.currentTimeMillis() );
	}

	private AuthTokenVo(String memberPk, String signupCode) {

		this.memberPk       = memberPk;
		this.signupCode     = signupCode;
		this.timeStamp      = Long.toString( System.currentTimeMillis() );
	}

    public static AuthTokenVo encodeToken(String memberId) {

        AuthTokenVo authTokenVo = new AuthTokenVo(memberId);
        authTokenVo.encode();

        return authTokenVo;
    }
	public static AuthTokenVo encodeToken(String memberId, String signupCode) {

		AuthTokenVo authTokenVo = new AuthTokenVo(memberId, signupCode);
		authTokenVo.encode();

		return authTokenVo;
	}

    public static AuthTokenVo decodeToken(String token) {

        AuthTokenVo authTokenVo = new AuthTokenVo();
        authTokenVo.decode(token);

	    return authTokenVo;
    }

    private void encode() {

	    String plainText    = "memberPk="   + toTokenValue(this.memberPk)
							+ "&signupCode=" + toTokenValue(this.signupCode)
							+ "&timeStamp=" + toTokenValue(this.timeStamp);
        try {
	        AESUtil aesUtil = new AESUtil(authEncryptKey, authEncryptIv);

	        this.token      = aesUtil.aesEncode( plainText);

        } catch (Exception ex) {
            log.error("********** authTokenVo.encode()", ex);
        }
    }

    private void decode(String token) {

	    try {
            this.token      = token;
		    AESUtil aesUtil = new AESUtil(authEncryptKey, authEncryptIv);

	        String plainText            = aesUtil.aesDecode(token);
            String[] plainTextTokens    = StringUtils.splitPreserveAllTokens(plainText, "&");

		    this.memberPk		        = fromTokenValue(StringUtils.splitPreserveAllTokens(plainTextTokens[0], "=")[1]);
		    this.signupCode		        = fromTokenValue(StringUtils.splitPreserveAllTokens(plainTextTokens[1], "=")[1]);
            this.timeStamp              = fromTokenValue(StringUtils.splitPreserveAllTokens(plainTextTokens[2], "=")[1]);

        } catch(Exception ex) {
		    log.error("********** authTokenVo.decode()", ex);
        }
    }

    private String toTokenValue(String value) {

        String result = null;

        try {
            result = URLEncoder.encode(StringUtils.defaultString(value), "UTF-8");

        } catch (Exception ex) {
	        log.error("********** authTokenVo.toTokenValue()", ex);
        }
        return result;
    }
    private String fromTokenValue(String value) {

        String result = null;

        try {
            result = URLDecoder.decode(value, "UTF-8");

        } catch(Exception ex) {
	        log.error("********** authTokenVo.fromTokenValue()", ex);
        }
        return result;
    }

    //토큰멤버 필수값 유무 체크
    public boolean tokenChk() {

        return     StringUtils.isNotBlank(this.memberPk)
		        && StringUtils.isNotBlank(this.signupCode)
                && StringUtils.isNotBlank(this.timeStamp);
    }

}
