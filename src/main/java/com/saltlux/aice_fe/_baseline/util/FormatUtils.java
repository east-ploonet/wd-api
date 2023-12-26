package com.saltlux.aice_fe._baseline.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils
{
    public static String textToMobile(String inStr, char delimiter){

        String	outStr = "-";
        String	tmpStr;
        String	num1;
        String	num2;
        String	num3;

        if (inStr == null || inStr.equals("")) {
            return outStr;
        }

        //구분자 제거
        tmpStr = inStr.replaceAll("[^0-9]", "");

        switch( tmpStr.length() ){
            case 10:
                num1 = tmpStr.substring(0, 3);
                num2 = tmpStr.substring(3, 6);
                num3 = tmpStr.substring(6, 10);
                outStr = num1 + delimiter + num2 + delimiter + num3;
                break;

            case 11:
                num1 = tmpStr.substring(0, 3);
                num2 = tmpStr.substring(3, 7);
                num3 = tmpStr.substring(7, 11);
                outStr = num1 + delimiter + num2 + delimiter + num3;
                break;
        }

        return outStr;
    }

    public static String textToBirth(String inStr, char delimiter){

        String	outStr = "-";
        String	tmpStr;
        String	num1;
        String	num2;
        String	num3;

        if (inStr == null || inStr.equals("")) {
            return outStr;
        }

        //구분자 제거
        tmpStr = inStr.replaceAll("[^0-9]", "");

        switch( tmpStr.length() ){
            case 6:
                num1 = tmpStr.substring(0, 2);
                num2 = tmpStr.substring(2, 4);
                num3 = tmpStr.substring(4, 6);
                outStr = num1 + delimiter + num2 + delimiter + num3;
                break;

            case 8:
                num1 = tmpStr.substring(0, 4);
                num2 = tmpStr.substring(4, 6);
                num3 = tmpStr.substring(6, 8);
                outStr = num1 + delimiter + num2 + delimiter + num3;
                break;
        }

        return outStr;
    }


    public static String dateToString(Date date, char delimiter){

        //yyyy-MM-dd hh:mm:ss:SSS

        String	outStr = "-";

        if (date == null) {
            return outStr;
        }

        outStr = new SimpleDateFormat("yyyy" + delimiter + "MM" + delimiter + "dd").format(date);

        return outStr;
    }
    
    	
    public static String dateToFomat(String pattern) {   	

    	//pattern ex> yyMMdd, yy-MM-dd ...etc       	
        String outStr = new SimpleDateFormat(pattern).format(new Date());
        return outStr;
    }
    
    
    public static String dateToStringCustomize(Date date, String pattern){

        //yyyy-MM-dd hh:mm:ss:SSS

        String	outStr = "-";

        if (date == null || pattern == null || pattern.equals("")) {
            return outStr;
        }

        outStr = new SimpleDateFormat(pattern).format(date);

        return outStr;
    }

    public static Date stringToDate(String dateStr, String pattern){

        //yyyy-MM-dd hh:mm:ss:SSS

        Date outDate;

        if(dateStr == null || dateStr.equals("") || pattern == null || pattern.equals("")){
            return null;
        }

        try {
            outDate = new SimpleDateFormat(pattern).parse(dateStr);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }

        return outDate;
    }

    public static String sexToString(String sex){

        String	outStr = "-";

        if (sex == null) {
            return outStr;
        }

        outStr = sex.equals("M") ? "남" : "여";

        return outStr;
    }

    public static String openYnToString(String openYn){

        String	outStr = "-";

        if (openYn == null) {
            return outStr;
        }

        outStr = openYn.equals("Y") ? "게시" : "숨김";

        return outStr;
    }

    public static String stringToMasking(String str, int maskingCnt){
        String outStr = "-";

        if(str == null){
            return outStr;
        }

        if(str.length() <= maskingCnt){
            return str;
        }

        outStr = str.substring(0, str.length()-maskingCnt);

        for(int i=0 ; i < maskingCnt ; i++){
            outStr = outStr + "*";
        }

        return outStr;
    }

    public static String mobileToMasking(String inStr, char delimiter){

        String	outStr = "-";
        String	tmpStr;
        String	num1;
        String	num2;
        String	num3;

        if (inStr == null || inStr.equals("")) {
            return outStr;
        }

        //구분자 제거
        tmpStr = inStr.replaceAll("[^0-9]", "");

        switch( tmpStr.length() ){
            case 10:
                num1 = tmpStr.substring(0, 3);
                num2 = tmpStr.substring(3, 4) + "**";
                num3 = "*" + tmpStr.substring(7, 10);
                outStr = num1 + delimiter + num2 + delimiter + num3;
                break;

            case 11:
                num1 = tmpStr.substring(0, 3);
                num2 = tmpStr.substring(3, 5) + "**";
                num3 = "*" + tmpStr.substring(8, 11);
                outStr = num1 + delimiter + num2 + delimiter + num3;
                break;
        }

        return outStr;

    }

    public static String codeToOs(String code){

        String	outStr;

        switch (code) {
            case "1302" :
                outStr = "Android";
                break;

            case "1303" :
                outStr = "IOS";
                break;

            default:
                outStr = "공통 OS";
                break;
        }

        return outStr;
    }
    
	public static String maskCardNumber(String cardNumber) {

	    // format the number
	    int index = 0;
	    String mask = "####xxxxxxxx####";
	    
	    StringBuilder maskedNumber = new StringBuilder();
	    for (int i = 0; i < mask.length(); i++) {
	        char c = mask.charAt(i);
	        if (c == '#') {
	            maskedNumber.append(cardNumber.charAt(index));
	            index++;
	        } else if (c == 'x') {
	            maskedNumber.append(c);
	            index++;
	        } else {
	            maskedNumber.append(c);
	        }
	    }

	    // return the masked number
	    return maskedNumber.toString();
	}    

}
