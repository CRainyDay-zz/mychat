package com.crainyday.mychat.utils;

import android.content.Intent;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPUtil {
    private static String HOST = "http://android.dulix.cn/";

    public static String POST(String api, String data) throws IOException, JSONException {
        // 1.定义请求url
        URL url = new URL(HOST + api);
        // 2.建立一个http的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 3.设置一些请求的参数
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-length", "" + data.getBytes().length);
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Connection", "keep-alive");

        connection.setConnectTimeout(5000);// 设置连接超时时间
        connection.setReadTimeout(5000); // 设置读取的超时时间

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(data.getBytes());
        outputStream.close();

        if(HttpURLConnection.HTTP_OK == connection.getResponseCode()){
            StringBuffer buffer = new StringBuffer();
            String readLine;
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((readLine = responseReader.readLine()) != null) {
                buffer.append(readLine).append("\n");
            }
            responseReader.close();

            System.out.println("*********************");
            System.out.println(buffer.toString());
            System.out.println("*********************");

            return buffer.toString();
        }
        return null;
    }

    public static String GET(String api, String data) throws IOException, JSONException {
        // 1.定义请求url
        URL url = new URL(HOST + api + "?" + data);
        // 2.建立一个http的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 3.设置一些请求的参数
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        connection.setConnectTimeout(5000);// 设置连接超时时间
        connection.setReadTimeout(5000); // 设置读取的超时时间

        connection.connect();

        if(HttpURLConnection.HTTP_OK == connection.getResponseCode()){
            StringBuffer buffer = new StringBuffer();
            String readLine;
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((readLine = responseReader.readLine()) != null) {
                buffer.append(readLine).append("\n");
            }
            responseReader.close();
            System.out.println("*********************");
            System.out.println(buffer.toString());
            System.out.println("*********************");
            return buffer.toString();
        }
        return null;
    }
}
