package com.saltlux.aice_fe._baseline.push;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


public class FCMSender implements Runnable {
    final static private String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    final static private int MAX_CNT = 900;
    //final static private int MAX_CNT = 1;
    
    //private ArrayList<String> clients = null;
    List<Map<String, Object>> clientList = null;
    List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    List<Map<String, Object>> errList = new ArrayList<Map<String, Object>>();
    
    private Thread t = null;
    private String simplePayload;
    private String serverKey;
    
    private int tot = 0;
    private int success = 0;
    private int failure = 0;
    private PushResultHandler callback = null;
    
    private int mseq = 0;
    private int paseq = 0;
    
    public FCMSender(List<Map<String, Object>> clients, String message, String serverKey, int mseq, int paseq)
    {
        this.clientList = clients;
        this.simplePayload = message;
        this.serverKey = serverKey;
        this.tot = clients.size();
        this.mseq = mseq;
        this.paseq = paseq;
    }
    
    public void send(boolean andWait) throws InterruptedException{
        this.t = new Thread(this);
        this.t.start();
        if(andWait){
            t.join();
        }
    }
    
    

    public void setCallback(PushResultHandler callback) {
        this.callback = callback;
    }
    
    @Override
    public void run() {
        int nCount  = this.clientList.size();
        if (this.clientList != null && this.clientList.isEmpty() == false)
        {
            if (nCount <= MAX_CNT)
            {
                sendFcm(this.clientList);
            }
            else
            {
                int nFrom = 0;
                int nTo   = MAX_CNT;
                int loop = nCount / MAX_CNT + (nCount % MAX_CNT == 0 ? 0 : 1);
                for (int i=0; i<loop; i++)
                {
                    sendFcm(this.clientList.subList(nFrom, nTo));
                    nFrom = nTo;
                    nTo  += MAX_CNT;
                    if (nTo > nCount)
                        nTo = nCount;
                }
            }
        }
        
        if (this.callback != null)
            this.callback.endSendPushNotification(this.clientList, this.mseq, this.paseq);
    }
    
    private void sendFcm(List<Map<String, Object>> list) {
        HttpURLConnection conn;
        URL url;
        try {
            url = new URL(FCM_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization","key="+this.serverKey);
            conn.setRequestProperty("Content-Type","application/json");
            
            JSONObject obj = new JSONObject(this.simplePayload);
            JSONArray reps = new JSONArray();
            for (int i=0; i<list.size(); i++)
            {
                Map<String, Object> tMap = list.get(i);
	            reps.put(tMap.get("TOKEN"));
	            reps.put(tMap.get("pkPush"));
            }
            obj.put("registration_ids", reps);
            
            OutputStream os = conn.getOutputStream();
            
            // 서버에서 날려서 한글 깨지는 사람은 아래처럼  UTF-8로 인코딩해서 날려주자
            os.write(obj.toString().getBytes("UTF-8"));
            os.flush();
            os.close();
            if (conn != null)
            {
                //int responseCode = conn.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
 
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                System.out.println(response.toString());


                JSONObject result = new JSONObject(response.toString());
                String succ = result.get("success") == null ? "" : result.get("success").toString();
                String fail = result.get("failure") == null ? "" : result.get("failure").toString();
                
                int nsucc = succ.isEmpty() == true ? 0 : Integer.parseInt(succ);
                int nfail = fail.isEmpty() == true ? 0 : Integer.parseInt(fail);
                
                success += nsucc;
                failure += nfail;
                
                JSONArray array = (JSONArray)result.get("results");
                for (int i=0; i<list.size(); i++)
                {
                    JSONObject retjson = array.getJSONObject(i);
                    Map <String, Object> resultMap = new HashMap<String, Object>();
                    Map <String, Object> reasonMap = new HashMap<String, Object>();
                    resultMap.put("result", "S");
                    
                    if (retjson.has("error"))
                    {
                        String reason = retjson.get("error").toString();
                        reasonMap.put("reason", reason);
                        resultMap.put("result", "F");
                    }
                    
                    list.get(i).putAll(resultMap);
                    list.get(i).putAll(reasonMap);
                }
                
            }
            resultList.addAll(list);
            //int status = conn != null ? conn.getResponseCode() : 0;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
