package com.saltlux.aice_fe.pc.issue.util;

import org.apache.commons.lang3.StringUtils;

public class Util {

    public static String detectChannelByPhone(String phoneNumber) {
        if (StringUtils.isNotBlank(phoneNumber)) {
            if (phoneNumber.startsWith("010")) {
                return Constants.CONTACT_CHANNEL.CELL_PHONE;
            }
        }
        return Constants.CONTACT_CHANNEL.LAND_PHONE;
    }
}
