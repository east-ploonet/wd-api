package com.saltlux.aice_fe._baseline.push;

import java.util.List;
import java.util.Map;

public interface PushResultHandler {
    public void endSendPushNotification(List<Map<String, Object>> clients, int mseq, int paseq);
    public void insertPushResult(Map<String, Object> client, int mseq, int paseq);
    public void updatePushResult(Map<String, Object> client, int mseq, int paseq);
    public void endSendPushNotificationIos(int mseq, int paseq);
}
