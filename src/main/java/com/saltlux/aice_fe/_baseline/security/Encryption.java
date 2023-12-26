package com.saltlux.aice_fe._baseline.security;

import com.saltlux.aice_fe._baseline.commons.Common;

import java.security.MessageDigest;

public final class Encryption {
	
	/**
	 * @MethodName  : EncryptSha256
	 * @Description : SHA-256 암호화. (단방향 암호화)
	 * @History     : 
	 * @param strData
	 * @return
	 */
	public static String EncryptSha256(String strData) {
		
//		StringBuffer strENCData = new StringBuffer();
		StringBuilder strENCData = new StringBuilder();

		if( strData != null && ! Common.isBlank(strData) ) {
			
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(strData.getBytes());

				byte[] digest = md.digest();

				for (byte b : digest)
//					strENCData.append( Integer.toHexString(b & 0xFF).toUpperCase() );
					strENCData.append(String.format("%02x",b).toUpperCase());

			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return strENCData.toString();
	}
}
