package com.saltlux.aice_fe._baseline.commons;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.pop3.POP3SSLStore;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;

/**
 * 
 * @FileName : Common.java
 * @Date : 2015. 2. 24.
 * @Author : Simpson
 * @Description :
 * @History :
 */
public class Common {

	private static final Logger LOGGER = LoggerFactory.getLogger(Common.class);
	private static final String encodedgetBrandIdpw = Base64.getEncoder().encodeToString(("brand:Member2023!").toString().getBytes());
	private static final String encodedgetIdpw = Base64.getEncoder().encodeToString(("admin:admin123!").toString().getBytes());
	
	
	/**
	 *
	 * @MethodName : NVL
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @History :
	 * @param rValue
	 * @return
	 */
	public static String NVL(String rValue) {

		return NVL(rValue, null);

	}

	/**
	 *
	 * @MethodName : NVL
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @History :
	 * @param rValue
	 * @param nullValue
	 * @return
	 */
	public static String NVL(Object rValue, String nullValue) {

		if (rValue == null) {

			if (nullValue != null)
				return nullValue;
			else
				return "";

		}

		String result = "";
		//
		if (rValue instanceof String) {

			String temp = rValue.toString();

			if (temp != null && !isBlank(temp)) {

				result = temp.trim();

			} else {

				if (nullValue != null && nullValue.length() > 0) {

					result = nullValue;

				} else {

					result = "";

				}

			}

		}
		//
		return result;

	}

	/**
	 *
	 * @MethodName : isBlank
	 * @Date : 2016. 2. 2.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param str
	 * @return
	 */
	public static boolean isBlank(final String str) {

		boolean result = true;
		int strLen = 0;
		//
		if (str == null || str.length() == 0) {

			result = true;

		} else {

			strLen = str.length();
			//
			for (int i = 0; i < strLen; i++) {

				if (!Character.isWhitespace(str.charAt(i))) {

					result = false;
					//
					break;

				}
			}

		}
		//
		return result;

	}

	/**
	 *
	 * @MethodName : parseInt
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @History :
	 * @param rValue
	 * @return
	 */
	public static int parseInt(Object rValue) {

		return parseInt(rValue, 0);

	}

	/**
	 *
	 * @MethodName : parseInt
	 * @Date : 2015. 4. 17.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param rValue
	 * @param rDefault
	 * @return
	 */
	public static int parseInt(Object rValue, int rDefault) {

		int result = 0;

		try {

			if (rValue instanceof Double)
				result = (int) ((double) rValue);

			else if (rValue instanceof Float)
				result = (int) ((float) rValue);

			else if (rValue instanceof Long)
				result = (int) ((long) rValue);

			else if (rValue instanceof Integer)
				result = (int) rValue;

			else
				result = Integer.parseInt(rValue.toString().trim());

		} catch (Exception ex) {

			result = rDefault;

		}

		return result;

	}

	/**
	 *
	 * @MethodName : parseFloat
	 * @Date : 2015. 11. 3.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param rValue
	 * @return
	 */
	public static float parseFloat(Object rValue) {

		return parseFloat(rValue, 0.0f);

	}

	/**
	 *
	 * @MethodName : parseFloat
	 * @Date : 2015. 11. 3.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param rValue
	 * @param rDefault
	 * @return
	 */
	public static float parseFloat(Object rValue, float rDefault) {

		float result = 0.0f;

		try {

			result = Float.parseFloat(rValue.toString().trim());

		} catch (Exception ex) {

			result = rDefault;

		}

		return result;

	}

	/**
	 *
	 * @MethodName : parseDouble
	 * @Date : 2015. 11. 3.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param rValue
	 * @return
	 */
	public static double parseDouble(Object rValue) {

		return parseDouble(rValue, 0.0d);

	}

	/**
	 *
	 * @MethodName : parseDouble
	 * @Date : 2015. 11. 3.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param rValue
	 * @param rDefault
	 * @return
	 */
	public static double parseDouble(Object rValue, double rDefault) {

		double result = 0.0d;

		try {

			result = Double.parseDouble(rValue.toString().trim());

		} catch (Exception ex) {

			result = rDefault;

		}

		return result;

	}

	/**
	 *
	 * @MethodName : parseLong
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @History :
	 * @param rValue
	 * @return
	 */
	public static long parseLong(Object rValue) {

		return parseLong(rValue, 0L);

	}

	/**
	 *
	 * @MethodName : parseLong
	 * @Date : 2015. 4. 17.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param rValue
	 * @param rDefault
	 * @return
	 */
	public static long parseLong(Object rValue, long rDefault) {

		long result = 0L;

		try {

			result = Long.parseLong(rValue.toString().trim());

		} catch (Exception ex) {

			result = rDefault;

		}

		return result;

	}

	/**
	 *
	 * @MethodName : getRequestApiName
	 * @Date : 2016. 3. 23.
	 * @Author : jhwoo
	 * @Description :
	 * @History : 2016. 3. 23. 최초작성.
	 * @param request
	 * @return
	 */
	public static String getRequestApiName(HttpServletRequest request) {

		final String reqUri = request.getRequestURI();
		String apiName = null; // reqUri.substring( reqUri.lastIndexOf("/") + 1 );
		//
		final String arrReqUri[] = reqUri.split("/");
		//
		if (arrReqUri != null && arrReqUri.length > 1) {

			apiName = arrReqUri[arrReqUri.length - 1];

		}
		//
		return apiName;

	}

	/**
	 *
	 * @MethodName : getQueryString
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param request
	 * @return
	 */
	public static String getQueryString(HttpServletRequest request) {

		StringBuffer buf = new StringBuffer();

		try {

			Enumeration<String> keys = request.getParameterNames();
			//
			while (keys.hasMoreElements()) {

				String key = keys.nextElement();
				String value = request.getParameter(key);
				//
				buf.append(String.format("%s%s=%s", buf.length() > 0 ? "&" : "?", encodeUTF8(key), encodeUTF8(value)));

			}

		} catch (Exception ex) {

			LOGGER.error(ex.getMessage(), ex);

		}

		return buf.toString();

	}

	/**
	 *
	 * @MethodName : getCookieValue
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param request
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request) {

		if (request == null)
			return null;

		String temp = Common.NVL(request.getHeader("cookie"), "");
		//
		if (temp != null && !Common.isBlank(temp) && temp.toUpperCase().indexOf("JSESSIONID") >= 0)
			temp = "";
		//
		return temp;

	}

	/**
	 *
	 * @MethodName : getUserAgentValue
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param request
	 * @return
	 */
	public static String getUserAgentValue(HttpServletRequest request) {

		if (request == null)
			return null;

		return Common.NVL(request.getHeader("user-agent"), "");

	}

	/**
	 *
	 * @MethodName : pageStartEnd
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param totalCount
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public static int[] pageStartEnd(int totalCount, int pageSize, int pageIndex) {

		int[] result = { 0, 0 };

		// -- --//

		try {

			int totalPage = 0;
			//
			if (totalCount % pageSize > 0)
				totalPage = (totalCount / pageSize) + 1;
			else
				totalPage = totalCount / pageSize;
			//
			if (pageIndex > totalPage)
				return result;
			//
			result[0] = (pageIndex - 1) * pageSize;
			result[1] = pageIndex * pageSize;

		} catch (Exception ex) {

			LOGGER.error(ex.getMessage(), ex);

		}

		return result;

	}

	/**
	 *
	 * @MethodName : convertText2HTML
	 * @Date : 2016. 2. 29.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param rStr
	 * @return
	 */
	public static String convertText2HTML(String rStr) {

		String result = rStr;
		//
		if (rStr != null && !isBlank(rStr)) {

			result = result.replaceAll("\'    ".trim(), "'   ".trim());
			result = result.replaceAll("&quot;".trim(), "\"  ".trim());
			result = result.replaceAll("&amp; ".trim(), "&  ".trim());
			result = result.replaceAll("&lt;  ".trim(), "<  ".trim());
			result = result.replaceAll("&gt;  ".trim(), ">  ".trim());
			// result = result.replaceAll( "\n ".trim(), "<br>".trim() );

		}
		//
		return result;

	}

	/**
	 *
	 * @MethodName : encodeUTF8
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param rTarget
	 * @return
	 */
	public static String encodeUTF8(String rTarget) {

		String result = rTarget;
		//
		if (rTarget != null && !Common.isBlank(rTarget)) {

			try {

				result = URLEncoder.encode(rTarget, "UTF-8");

			} catch (UnsupportedEncodingException ex) {

				LOGGER.error(ex.getMessage(), ex);

			}

		}
		//
		return result;

	}

	/**
	 *
	 * @MethodName : decodeUTF8
	 * @Date : 2015. 2. 24.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param rTarget
	 * @return
	 */
	public static String decodeUTF8(String rTarget) {

		String result = rTarget;
		//
		if (rTarget != null && !Common.isBlank(rTarget)) {

			try {

				result = URLDecoder.decode(rTarget, "UTF-8");

			} catch (UnsupportedEncodingException ex) {

				LOGGER.error(ex.getMessage(), ex);

			}

		}
		//
		return result;

	}

	/**
	 *
	 * @MethodName : getTimeStampString
	 * @Date : 2016. 2. 2.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @return
	 */
	public static String getTimeStampString() {

		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA);
		//
		return formatter.format(new Date());

	}

	/**
	 * 
	 * @MethodName : startPage
	 * @Date : 2016. 2. 2.
	 * @Author : jhwoo
	 * @History : 2016. 2. 2. 최초작성.
	 * @param current_page
	 * @param page_size
	 * @return
	 */
	public static int startPage(int currentPage, int pageSize) {

		int result = 0;

		try {

			result = (currentPage - 1) * pageSize;

		} catch (Exception ex) {

			result = 0;

		}

		return result;

	}

	/**
	 * 
	 * @MethodName : convertJsonStringToMap
	 * @Date : 2016. 4. 28.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param receiveData
	 * @return
	 */
	public static HashMap<String, Object> convertJsonStringToMap(final String receiveData) {

		HashMap<String, Object> paramMap = null;
		//
		if (receiveData != null && !Common.isBlank(receiveData)) {

			try {

				paramMap = new HashMap<String, Object>();
				//
				Type type = new TypeToken<HashMap<String, Object>>() {
				}.getType();
				Gson obj = new Gson();
				//
				paramMap = obj.fromJson(receiveData, type);

			} catch (Exception e) {

				return null;

			}

		}
		//
		return paramMap;

	}

	/**
	 * 
	 * @MethodName : convertJsonStringToObject
	 * @Date : 2016. 4. 28.
	 * @Author : Simpson
	 * @Description :
	 * @History :
	 * @param receiveData
	 * @param classTypeName
	 * @return
	 */
	public static Object convertJsonStringToObject(final String receiveData, final String classTypeName) {

		Object paramMap = null;
		//
		if (receiveData != null && !Common.isBlank(receiveData)) {

			try {

				paramMap = new HashMap<String, Object>();
				//
				Gson obj = new Gson();
				//
				paramMap = obj.fromJson(receiveData, Class.forName(classTypeName));

			} catch (Exception e) {

				return null;

			}

		}
		//
		return paramMap;

	}

	/**
	 * 
	 * @MethodName : getStringSort
	 * @Date : 2016. 6. 13.
	 * @Author : jhwoo
	 * @History : 2016. 6. 13. 최초작성.
	 * @param str
	 * @return
	 */
	public static String getStringSort(String[] str) {

		if (str == null || str.length <= 0)
			return null;

		Arrays.sort(str, String.CASE_INSENSITIVE_ORDER);
		//
		return str[0];

	}

	/**
	 * @param request
	 * @return
	 */
	public static String getRealIp(HttpServletRequest request) {

		String ip = null;
		//
		if (request != null) {

			ip = request.getHeader("X-Forwarded-For");
			//
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}

		}
		//
		return ip;

	}

	/**
	 * @param szText
	 * @param nLength
	 * @return
	 */
	public static String strCut(final String szText, final int nLength) {

		if (szText == null || isBlank(szText))
			return null;

		if (nLength <= 0)
			return szText;

		String r_val = szText;

		int oL = 0, rF = 0, rL = 0;

		try {

			byte[] bytes = r_val.getBytes("UTF-8");

			int j = rF;
			//
			while (j < bytes.length) {

				if ((bytes[j] & 0x80) != 0) {

					if (oL + 2 > nLength)
						break;
					//
					oL += 2;
					rL += 3;
					j += 3;

				} else {

					if (oL + 1 > nLength)
						break;
					//
					++oL;
					++rL;
					++j;

				}
			}

			r_val = new String(bytes, rF, rL, "UTF-8");

		} catch (Exception ex) {

			return null;

		}

		return r_val;

	}

	public static String changeCamelCase(final String rKey) {

		String key = rKey;

		if (key != null && !Common.isBlank(key))
			key = key.toLowerCase();
		else
			return null;

		// -- --//

		boolean isCamel = false;
		//
		int findCamelCount = 0;
		//
		StringBuilder camelCaseKey = new StringBuilder();
		//
		for (int i = 0; i < key.length(); i++) {

			char ch = key.charAt(i);
			//
			if (ch == '_') {

				isCamel = true;
				//
				findCamelCount += 1;
				//
				continue;

			}
			//
			if (isCamel)
				ch = Character.toUpperCase(ch);
			//
			camelCaseKey.append(ch);
			//
			isCamel = false;

		}
		//
		if (findCamelCount == 0)
			camelCaseKey = new StringBuilder(rKey);

		// -- --//

		boolean isNumber = false;
		//
		int findNumberCount = 0;
		//
		key = camelCaseKey.toString();
		//
		camelCaseKey = new StringBuilder();
		//
		for (int i = 0; i < key.length(); i++) {

			char ch = key.charAt(i);
			//
			if (ch >= 48 && ch <= 57) { // 0 ~ 9 check

				isNumber = true;
				//
				findNumberCount += 1;

			}
			//
			if (isNumber && findNumberCount == 1)
				camelCaseKey.append("_").append(ch);
			else
				camelCaseKey.append(ch);
			//
			// isNumber = false;

		}
		//
		if (findCamelCount == 0 && findNumberCount == 0)
			camelCaseKey = new StringBuilder(rKey);

		return camelCaseKey.toString();

	}

	public static String jsonMap2Str(Map<String, Object> mapJson) {

		String result = null;
		//
		Gson gson = (new GsonBuilder()).disableHtmlEscaping().create();
		//
		try {

			result = gson.toJson(mapJson);

		} catch (Exception ex) {

		}
		//
		return result;

	}

	public static String convertCamelCase(String str) {
		if (isBlank(str) || str.indexOf(95) < 0 && Character.isLowerCase(str.charAt(0))) {
			return str;
		}
		StringBuffer result = new StringBuffer();
		boolean nextUpper = false;
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char currentChar = str.charAt(i);
			if (currentChar == '_') {
				nextUpper = true;
			} else if (nextUpper) {
				result.append(Character.toUpperCase(currentChar));
				nextUpper = false;
			} else {
				result.append(Character.toLowerCase(currentChar));
			}
		}
		return result.toString();
	}

	public static String getRestDataApi(String remoteURL, String token, String method) throws Exception {
		StringBuffer sb=new StringBuffer();
		  try {

				URL url = new URL(remoteURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				
				conn.setRequestMethod(method);
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("content-type", "application/json;charset=UTF-8");
				if(token != null) conn.setRequestProperty("auth-token", token);
				
				conn.setDoInput(true);
				conn.setDoOutput(true);
				JSONObject apiJsonInfo = new JSONObject();
		    	
				PrintWriter postReq = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
			    postReq.write(apiJsonInfo.toString());
                postReq.flush();
                
				if (conn.getResponseCode() != 200) {
					return null;
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));

				String output;
				
				
				while ((output = br.readLine()) != null) {
					sb.append(output);
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;

			  } catch (IOException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;
			  }
		  return sb.toString();
	}
	
	public static String getRestDataApiGet(String remoteURL, String token, String method) throws Exception {
		StringBuffer sb=new StringBuffer();
		  try {

				URL url = new URL(remoteURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				
				conn.setRequestMethod(method);
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("content-type", "application/json;charset=UTF-8");
				if(token != null) conn.setRequestProperty("auth-token", token);
				
				conn.setDoInput(true);
				conn.setDoOutput(true);
                
				if (conn.getResponseCode() != 200) {
					System.out.println("conn.getResponseCode() : "+conn.getResponseCode());
					return null;		//405error?
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));

				String output;
				
				
				while ((output = br.readLine()) != null) {
					sb.append(output);
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;

			  } catch (IOException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;
			  }
		  return sb.toString();
	}
	
	public static Map<String, Object> getRestDataApiPatch(String remoteURL, JSONObject apiJsonInfo) throws Exception {
		  try {
			  HttpResponse<JsonNode> response = null;
			  response = Unirest.patch(remoteURL).header("Content-Type", "application/json").body(apiJsonInfo).asJson();
			  JSONObject jsonObject = response.getBody().getObject();
			  Map<String, Object> result = jsonObject.toMap();
			  	  return result;
			  }catch (Exception e) {
				  e.printStackTrace();
				  System.out.println("e:" + e);
				  LOGGER.error(e.getMessage(), e);
				  return null;
			 }
	}
	
	
	
	//seed암호화
	public static String getPrivEncrypt(String str) {
    	if(isBlank(str)) return null;
    	
		String key = "plnt23wko06sa7320";
		System.out.println("key:" + key);
		String encStr = null;
		try {
			int[] seedKey = SEEDUtil.getSeedRoundKey(key);
			encStr = SEEDUtil.getSeedEncrypt(str, seedKey);
		} catch(Exception e){System.out.println("e:" + e);}
		return encStr;
	}
	
	//seed복호화
	public static String getPrivDecrypt(String str) {
		if(isBlank(str)) return null;
    	
		// seed 복호화
    	String key = "plnt23wko06sa7320";
		String decStr = null;
		
		try {
			int[] seedKey = SEEDUtil.getSeedRoundKey(key);
			decStr = SEEDUtil.getSeedDecrypt(str, seedKey);
		} catch(Exception e){}
		
		return decStr;
		
	}
	
	//smtp 메일 발송
	public static boolean sendMail(String fromMail, String fromName,String mailHost, String mailPort, String mailPassword, String mailUser,String startTls, String ssl,String toMail, String toName, String title, String content){

		Properties props = new Properties();
		props.put("mail.smtp.host", mailHost);
		props.put("mail.smtp.port", mailPort);

		Authenticator authenticator = null;
		if(!Common.isBlank(mailUser) && !Common.isBlank(mailPassword))
		{
			props.put("mail.smtp.auth", true);
			authenticator = new javax.mail.Authenticator() {
		        protected javax.mail.PasswordAuthentication getPasswordAuthentication(){
		            return new javax.mail.PasswordAuthentication(mailUser, mailPassword);
		        }
		    };
		}
		
		if(startTls.equals("Y")) props.put("mail.smtp.starttls.enable", true);
		if(ssl.equals("Y")){
			props.put("mail.smtp.ssl.enable", true);
			props.put("mail.smtp.ssl.trust", mailHost);
		}
		
		try{
			Session mailSession = Session.getInstance(props, authenticator);
			
		
			MimeMessage mailMessage = new MimeMessage(mailSession);
			mailMessage.setFrom(new InternetAddress(fromMail)); // 보내는 EMAIL (정확히 적어야 SMTP 서버에서 인증 실패되지 않음)
			mailMessage.setRecipients(Message.RecipientType.TO, toMail ); //수신자 셋팅
			
			// Message Setting
			mailMessage.setSubject(MimeUtility.encodeText(title,"UTF-8", "B"));
		    
			mailMessage.setContent(content, "text/html; charset=UTF-8");
		    //utf-8
			mailMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
	    	
			Transport.send(mailMessage);
		
		}catch(UnsupportedEncodingException | MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	
	//smtp 메일파일 발송
	public static boolean sendMailFiles(String fromMail, String fromName,String mailHost, String mailPort, String mailPassword, String mailUser,String startTls, String ssl,String toMail, String toName, String title, String content, MultipartFile[] uploadFiles){

		Properties props = new Properties();
		props.put("mail.smtp.host", mailHost);
		props.put("mail.smtp.port", mailPort);
		
//		System.out.println("mailUser"+mailUser);
//		System.out.println("mailPassword"+mailPassword);

		Authenticator authenticator = null;
		if(!Common.isBlank(mailUser) && !Common.isBlank(mailPassword))
		{
			props.put("mail.smtp.auth", true);
			authenticator = new javax.mail.Authenticator() {
		        protected javax.mail.PasswordAuthentication getPasswordAuthentication(){
		            return new javax.mail.PasswordAuthentication(mailUser, mailPassword);
		        }
		    };
		}
		
		if(startTls.equals("Y")) props.put("mail.smtp.starttls.enable", true);
		if(ssl.equals("Y")){
			props.put("mail.smtp.ssl.enable", true);
			props.put("mail.smtp.ssl.trust", mailHost);
		}
		
		try{
			Session mailSession = Session.getInstance(props, authenticator);
			
		
			MimeMessage mailMessage = new MimeMessage(mailSession);
			MimeBodyPart messageBodyPart = new MimeBodyPart();  
			
			messageBodyPart.setContent(content,"text/html; charset=UTF-8");
			mailMessage.setHeader("Content-Type","text/html");
			mailMessage.setSubject(title,"UTF-8");
			mailMessage.setFrom(new InternetAddress(fromMail)); // 보내는 EMAIL (정확히 적어야 SMTP 서버에서 인증 실패되지 않음)
			mailMessage.setRecipients(Message.RecipientType.TO, toMail ); //수신자 셋팅
			
	
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			if(uploadFiles != null && uploadFiles.length > 0){
				for(MultipartFile filePath : uploadFiles) {
					MimeBodyPart attachPart = new MimeBodyPart();
					try {
						filePath.transferTo(new File(filePath.getOriginalFilename()).getAbsoluteFile());
						attachPart.attachFile(filePath.getOriginalFilename());
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					multipart.addBodyPart(attachPart);
				}
			}
			 
			mailMessage.setContent(multipart, "text/plain; charset=UTF-8");
			  
			Transport.send(mailMessage);
		
		}catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param type pop, imap 구분
	 * @param host 
	 * @param username
	 * @param password
	 * @param port
	 * @param ssl 1:사용 그외 미사용
	 * @return
	 * @throws NoSuchProviderException
	 */
	public boolean receiveMailImapPop(String type, String host, String username, String password, String port, String ssl) throws NoSuchProviderException {
		
		String sslFactory = "javax.net.ssl.SSLSocketFactory";
        if(type.equals("pop")) type = "pop3";
        else if(type.equals("imap")) type = "imaps";
        
        
        Properties props = new Properties();
        
        props.setProperty("mail.store.protocol", type);
        if(ssl.equals("Y")) {
        	props.setProperty("mail." + type + ".socketFactory.class", sslFactory);
        	props.setProperty("mail." + type + ".socketFactory.fallback", "false");
        }
        
        props.setProperty("mail." + type + ".port",  port);
        props.setProperty("mail." + type + ".socketFactory.port", port);
        
        
        URLName url = new URLName(type, host, Common.parseInt(port), "", username, password);
        
        Session session = Session.getInstance(props, null);
        Store store = null;
        if(type.equals("pop3")) new POP3SSLStore(session, url);
        else store = new IMAPSSLStore(session, url);
        try {
			store.connect();
			if(store.isConnected()) return true;
			else return false;
			
		} catch (MessagingException e) {
			System.out.println("receiveMailImapPop error:" + e);
		}finally {
			try {if(store != null) store.close();} catch (MessagingException e) {}
		}
		
		return false;
	}
				
	public static String getBasicApi(String remoteURL, String method, String parameterStr) throws Exception {
		StringBuffer sb=new StringBuffer();
		
		  try {
				URL url = new URL(remoteURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				
				conn.setRequestMethod(method);
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("content-type", "application/json;charset=UTF-8");
				
				conn.setDoOutput(true); 
		    	
				PrintWriter postReq = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
			    postReq.write(parameterStr);
                postReq.flush();
                
                
				if (conn.getResponseCode() != 200) {
					System.out.println("-----------");
					System.out.println("conn.getResponseCode() : "+conn.getResponseCode());
					System.out.println("!!!!!!!!!!!conn.getContent() : "+conn.getContent());
					return null;		//401error?
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));

				String output;
				
				
				while ((output = br.readLine()) != null) {
					sb.append(output);
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;

			  } catch (IOException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;
			  }
		  return sb.toString();
	}
	
	
	public static String getRestBrandLoginApi(String remoteURL) throws Exception {
		StringBuffer sb=new StringBuffer();
		  try {

		        
				URL url = new URL(remoteURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("content-type", "application/json;charset=UTF-8");
				conn.setRequestProperty("Authorization", "Basic " + encodedgetBrandIdpw);
				
				
				//conn.setDoInput(true);
				conn.setDoOutput(true); 
				JSONObject apiJsonInfo = new JSONObject();
				apiJsonInfo.put("email", "ploonet@ploonet.com");
				apiJsonInfo.put("password", "WlVqaThvUzdLOEgzaWFoMw==");
			    	
				PrintWriter postReq = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
			    postReq.write(apiJsonInfo.toString());
                postReq.flush();
                
                //System.out.println("메소드값 : "+conn.getRequestMethod()); // <- GET 선언해도 POST 탐.
				if (conn.getResponseCode() != 200) {
					System.out.println("conn.getResponseCode() : "+conn.getResponseCode());
					return null;		//405error?
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));

				String output;
				
				
				while ((output = br.readLine()) != null) {
					sb.append(output);
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;

			  } catch (IOException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;
			  }
		  return sb.toString();
	}
	
	public static Map<String, Object> getRestDataBrandApi(String remoteURL, JSONObject apiJsonInfo, int gubun, String brandApi, String path, String paramName) throws Exception {
		
		org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();
		String adminToken = Common.getRestBrandLoginApi(brandApi + "/api/if/sign/signin/aab0951b-0214-37b1-b0ba-8b2fd8fda93b");
		org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject)jsonParser.parse(adminToken);
		org.json.simple.JSONObject data = (org.json.simple.JSONObject) jsonObj.get("data");
		String accessToken = data.get("accessToken").toString();
		//System.out.println("accessToken:" + accessToken);
		//String result = "success";
		HttpResponse<JsonNode> response = null;
		  try {
			  	System.out.println(remoteURL);
			  	
			  		if(gubun == 0) {
			  			System.out.println("patch!!!!");
			  			if(path != null) {
			  				response = Unirest.patch(remoteURL)
						  			.basicAuth("brand", "Member2023!")
						  			.header("auth-token-if", accessToken)
						  			.field(paramName, new File(path))
						  			.field("data", apiJsonInfo)
									.asJson();
			  			}else {
			  				response = Unirest.patch(remoteURL)
						  			.basicAuth("brand", "Member2023!")
						  			.header("auth-token-if", accessToken)
						  			.field("data", apiJsonInfo)
									.asJson();
			  			}
				  		
				  	}else if(gubun == 3) {
			  			System.out.println("patch!!!!");
			  			response = Unirest.patch(remoteURL)
					  			.basicAuth("brand", "Member2023!")
					  			.header("auth-token-if", accessToken)
					  			.header("Content-Type", "application/json")
					  			.body(apiJsonInfo)
								.asJson();	
				  		
				  	}else if(gubun == 4) {
			  			System.out.println("patch!!!!");
			  			System.out.println("accessToken:" + accessToken);
			  			System.out.println("apiJsonInfo:" + apiJsonInfo);
			  			System.out.println("patch!!!!");
			  			
			  			response = Unirest.post(remoteURL)
					  			.basicAuth("brand", "Member2023!")
					  			.header("auth-token-if", accessToken)
					  			.header("Content-Type", "application/json")
					  			.body(apiJsonInfo)
								.asJson();	
				  		
				  	}else {
				  		System.out.println("post!!!!");
				  		if(path != null) {
				  			response = Unirest.post(remoteURL)
						  			.basicAuth("brand", "Member2023!")
						  			.header("auth-token-if", accessToken)
						  			.field(paramName, new File(path))
						  			.field("data", apiJsonInfo)
									.asJson();
				  		}else {
				  			response = Unirest.post(remoteURL)
						  			.basicAuth("brand", "Member2023!")
						  			.header("auth-token-if", accessToken)
						  			.field("data", apiJsonInfo)
									.asJson();	
				  		}
				  		
				  	}
			  	System.out.println("apiJsonInfo:" + apiJsonInfo);
			  	System.out.println("!!!!!!!getStatus:" + response.getStatus());
			  	System.out.println("!!!!!!!getMsg:" + response.getStatusText());
			  	System.out.println("!!!!!!!!!!!!body:" + response.getBody());
				JSONObject jsonObject = response.getBody().getObject();
				Map<String, Object> result = jsonObject.toMap();
			  	return result;
			  }catch (Exception e) {
				  e.printStackTrace();
				  System.out.println("e:" + e);
				  LOGGER.error(e.getMessage(), e);
				  return null;
			  }
		  
	}
	
	public static String getRestDataUnionApiPost(String remoteURL, JSONObject apiJsonInfo, String billingUrl) throws Exception {
		StringBuffer sb=new StringBuffer();
		
		String apiResult = getRestDataApiGet(billingUrl + "/account/administrator/svc/authentication/MTY3MmUxZjEtNGVhZC00YzVjLTgzZDAtYzMxZTM4YzkwMWU5?password=a3Nsc2llMjEh", null, "GET");
		JSONParser jsonParser = new JSONParser();
		org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject)jsonParser.parse(apiResult);
    	String authToken =  jsonObj.get("authToken").toString();
		System.out.println("authToken:" + authToken);
		  try {
				URL url = new URL(remoteURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("content-type", "application/json;charset=UTF-8");
				conn.setRequestProperty("auth-token", authToken);
				conn.setRequestProperty("Authorization", "Basic " + encodedgetIdpw);
				//conn.setDoInput(true);
				conn.setDoOutput(true); 
		    	
				PrintWriter postReq = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
			    postReq.write(apiJsonInfo.toString());
                postReq.flush();
                
                //System.out.println("메소드값 : "+conn.getRequestMethod()); // <- GET 선언해도 POST 탐.
				if (conn.getResponseCode() != 200) {
					System.out.println("conn.getResponseCode() : "+conn.getResponseCode());
					return null;		//401error?
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));

				String output;
				
				
				while ((output = br.readLine()) != null) {
					sb.append(output);
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;

			  } catch (IOException e) {
				  LOGGER.error(e.getMessage(), e);
				  return null;
			  }
		  return sb.toString();
	}
	
	public static String getRestDataUnionApiGet(String remoteURL, String authToken, String billingUrl) throws Exception {
		StringBuffer sb=new StringBuffer();
		  try {
			  System.out.println("getRestDataUnionApiGet : " + remoteURL);
			  
			  String apiResult = getRestDataApiGet(billingUrl + "/account/administrator/svc/authentication/MTY3MmUxZjEtNGVhZC00YzVjLTgzZDAtYzMxZTM4YzkwMWU5?password=a3Nsc2llMjEh", null, "GET");
				JSONParser jsonParser = new JSONParser();
				org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject)jsonParser.parse(apiResult);
		    	authToken =  jsonObj.get("authToken").toString();
			  
			  System.out.println(authToken);
				URL url = new URL(remoteURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("content-type", "application/json;charset=UTF-8");
				conn.setRequestProperty("auth-token", authToken);
				conn.setRequestProperty("Authorization", "Basic " + encodedgetIdpw);
				
				
				conn.setDoInput(true);
				conn.setDoOutput(true);
                
				if (conn.getResponseCode() != 200) {
					System.out.println("remoteURL : "+ remoteURL);
					
					System.out.println("conn.getResponseCode() : "+conn.getResponseCode());
					
					return null;		//405error?
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));

				String output;
				
				
				while ((output = br.readLine()) != null) {
					sb.append(output);
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {
				  System.out.println(" getRestDataUnionApiGet MalformedURLException:" + e);
				  LOGGER.error(e.getMessage(), e);
				  return null;

			  } catch (IOException e) {
				  System.out.println(" getRestDataUnionApiGet IOException:" + e);
				  LOGGER.error(e.getMessage(), e);
				  return null;
			  }
		  return sb.toString();
	}
	

}
