package com.saltlux.aice_fe._baseline.util;

import java.util.UUID;

public class IdGenerator {
	
	public static String generateUniqueId(String prifix) {        

        return prifix + generateUniqueId();
    }
	
	public static String generateUniqueId() {

        UUID uuid           = UUID.randomUUID();
        String str          = "" + uuid;
        int uid             = str.hashCode();
        String filterStr    = "" + uid;
        str                 = filterStr.replaceAll("-", "");
        str = String.format("%010d", Integer.parseInt(str));

        return str;
    }
}
