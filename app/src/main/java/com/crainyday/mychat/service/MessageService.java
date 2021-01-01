package com.crainyday.mychat.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.crainyday.mychat.activity.LoginActivity;
import com.crainyday.mychat.entity.Message;
import com.crainyday.mychat.utils.ImageUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MessageService extends Service {
    private String username;
    private String token;
    private String target_id = "";
    private String type = "all";
    private boolean running = true;
    // 信号量, 用来实现互斥
    private int msgFlag = 0;
    // 标记新消息
    private int newMsg = 0;
    // 标记token是否过期
    private int tokenFlag = 0;
    // 标记暂无聊天信息
    private int recordIsNull = 0;

    private MyThread myThread;

    private List<Message> msgList = new ArrayList<>();
    private List<Message> updateList = new ArrayList<>();
    private DownloadBinder binder = new DownloadBinder();

    public MessageService() {
    }

    public class DownloadBinder extends Binder {
        public List<Message> getAllList(){return msgList;}
        public List<Message> getUpdateList(){return updateList;}
        public void setMsgFlag(int flag){msgFlag = flag;}
        public int getMsgFlag(){return msgFlag;}
        public void setNewMsg(int flag){newMsg = flag;}
        public int getNewMsg(){return newMsg;}
        public int getRecordIsNull(){return recordIsNull;}
        public int getTokenFlag(){return tokenFlag;}
    }

    @Override
    public IBinder onBind(Intent intent) {
        username = intent.getStringExtra("username");
        token = intent.getStringExtra("token");
        target_id = intent.getStringExtra("target_id");
        type = intent.getStringExtra("type");

        this.myThread = new MyThread();
        this.myThread.start();
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        username = intent.getStringExtra("username");
//        token = intent.getStringExtra("token");
//
//        this.myThread = new MyThread();
//        this.myThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        this.running =false;
        super.onDestroy();
    }

    private class MyThread extends Thread{
        @Override
        public void run() {
            // 1.定义请求url
            URL url = null;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                while (running){
                    String api = "http://android.dulix.cn/getmsglist?client_id="+ username + "&token=" + token+"&type="+type+"&target_id="+target_id;
                    url = new URL(api);
                    // 2.建立一个http的连接
                    connection = (HttpURLConnection) url.openConnection();
                    // 3.设置一些请求的参数
                    connection.setDoInput(true);
                    connection.setDoOutput(false);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Connection", "keep-alive");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    connection.setConnectTimeout(20000);// 设置连接超时时间
                    connection.setReadTimeout(20000); // 设置读取的超时时间

                    connection.connect();

                    if(HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                        StringBuffer buffer = new StringBuffer();
                        String readLine = null;
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((readLine = reader.readLine()) != null) {
                            buffer.append(readLine).append("\n");
                        }
                        reader.close();
                        JSONObject response = new JSONObject(buffer.toString());
                        System.out.println("*********************");
                        System.out.println(buffer.toString());
                        System.out.println("*********************");
                        String error = response.getString("error");

                        if("0".equals(error)){
                            tokenFlag = 0;
                            recordIsNull = 0;
                            JSONArray msgs = response.getJSONArray("msglist");
                            if(msgs.length() > 0&&msgList.size()!=msgs.length()&&msgFlag==0){
                                // 存在聊天记录, 并且 当前聊天记录 和 新获取的聊天记录不一致, 并且没有其他线程操作 List
                                // 此种情况: 有人撤回了消息或有人发送了消息
                                msgFlag = 1;
                                newMsg = 1;
                                updateList.clear();
                                for (int i = 0;i<msgs.length();++i){
                                    JSONObject msg = msgs.getJSONObject(i);
                                    String sender = msg.getString("sender");
                                    String senderId = msg.getString("sender_id");
                                    String recordGuid = msg.getString("record_guid");
                                    String contents = msg.getString("contents");
                                    String contentsType = msg.getString("contentstype");
                                    String sendTime = msg.getString("sendtime");
                                    boolean isMine = false;
                                    Bitmap bitmap = null;
                                    Message message = null;
                                    if(username.equals(senderId)){
                                        isMine = true;
                                    }
                                    if ("image".equals(contentsType)){
                                        contents = contents.replaceAll(" ", "+");
                                        bitmap = ImageUtil.transitionContents(contents);
                                        contents = "";
                                    }
                                    message = new Message(isMine, sender, senderId, recordGuid, contents, contentsType, sendTime, bitmap);
                                    updateList.add(message);
                                }
                                msgFlag = 0;
                            }
                        }else if ("101".equals(error)){
                            // tokenFlag 过期
                            tokenFlag = 1;
                            running = false;
                        }else if("102".equals(error)){
                            // 暂无聊天记录
                            recordIsNull = 1;
                        }
                    }
                    Thread.sleep(10000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
