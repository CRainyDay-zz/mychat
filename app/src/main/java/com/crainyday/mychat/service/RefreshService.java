package com.crainyday.mychat.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
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
import java.util.List;

public class RefreshService extends Service {
    private String clientId;
    private String token = null;
    private String newToken = null;

    private boolean isNewToken = false;
    private boolean running = true;

    private RefreshThread refreshThread;
    private RefreshBinder binder = new RefreshBinder();

    public RefreshService() {

    }

    public class RefreshBinder extends Binder {
        public void setIsNewToken(boolean flag){isNewToken = flag;}
        public boolean isNewToken(){return isNewToken;}
        public String getToken(){return newToken;}
    }

    @Override
    public IBinder onBind(Intent intent) {
        clientId = intent.getStringExtra("username");
        token = intent.getStringExtra("token");

        this.refreshThread = new RefreshThread();
        this.refreshThread.start();

        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        clientId = intent.getStringExtra("username");
//        token = intent.getStringExtra("token");
//
//
//        this.refreshThread = new RefreshThread();
//        this.refreshThread.start();
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

    private class RefreshThread extends Thread{
        @Override
        public void run() {
            URL url = null;
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                long expires_in = 590;
//                long expires_in = 9;

                while (running){
                    Thread.sleep(expires_in*1000);

                    // 1.定义请求url
                    String api = "http://android.dulix.cn/loginrefresh?client_id="+ clientId + "&token=" + token;
                    url = new URL(api);
                    // 2.建立一个http的连接
                    connection = (HttpURLConnection) url.openConnection();
                    // 3.设置一些请求的参数
                    connection.setDoInput(true);
                    connection.setDoOutput(false);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Connection", "keep-alive");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    connection.setConnectTimeout(8000);// 设置连接超时时间
                    connection.setReadTimeout(8000); // 设置读取的超时时间

                    connection.connect();

                    if(HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                        StringBuffer buffer = new StringBuffer();
                        String readLine;
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((readLine = reader.readLine()) != null) {
                            buffer.append(readLine).append("\n");
                        }
                        reader.close();
                        JSONObject response = new JSONObject(buffer.toString());
                        String error = response.getString("error");

                        System.out.println("*********************");
                        System.out.println(buffer.toString());
                        System.out.println("*********************");

                        if("0".equals(error)){
                            newToken = response.getString("token");
                            expires_in = Long.parseLong(response.getString("expres_in"));
                        }else {
                            newToken = null;
                        }
                        isNewToken = true;
                        token = newToken;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
