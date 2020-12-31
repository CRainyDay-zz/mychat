package com.crainyday.mychat.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crainyday.mychat.R;
import com.crainyday.mychat.adapter.MessageAdapter;
import com.crainyday.mychat.entity.Message;
import com.crainyday.mychat.service.MessageService;
import com.crainyday.mychat.utils.HTTPUtil;
import com.crainyday.mychat.utils.ImageUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private String username = null;
    private String token = null;
    private String target_id = null;
    private String target_name = null;
    private String type = "all";

    private List<Message> msgList = new ArrayList<>();
    private List<Message> updateList = new ArrayList<>();

    private TextView my_title_bar;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText inputText;
    private Button sendBtn;
    private ImageView sendImg;

    private MyTask task = null;
    private boolean running = false;
    private int recordIsNull = 0;
    private boolean isLawfulToken = true;
    private int first = 1;
    private int length = 0;
    private MessageService.DownloadBinder downloadBinder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        token = intent.getStringExtra("token");
        target_id = intent.getStringExtra("target_id");
        target_name = intent.getStringExtra("target_name");
        if(!"20171222000".equals(target_id)){
            type = "single";
        }
        Intent bindIntent = new Intent(ChatActivity.this, MessageService.class);
        bindIntent.putExtra("username", username);
        bindIntent.putExtra("token", token);
        bindIntent.putExtra("type", type);
        bindIntent.putExtra("target_id", target_id);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
//        startService(bindIntent);

        my_title_bar = findViewById(R.id.my_title_tv);
        my_title_bar.setText(target_name);

        recyclerView = (RecyclerView)findViewById(R.id.msg_recycler_view);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new MessageAdapter(ChatActivity.this, msgList);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new MessageAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });

        adapter.setOnItemLongClickListener(new MessageAdapter.ItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                final  Message message = msgList.get(position);
                final int index = position;
                if(message.isMine()){
                    new AlertDialog.Builder(view.getContext())
                            .setMessage("是否撤回消息")
                            .setPositiveButton("是", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    withdrawMsg(username,token,message.getRecordGuid(), index);
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                }

            }
        });

        inputText = (EditText)findViewById(R.id.send_text);
        sendBtn = (Button)findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contents = inputText.getText().toString().trim();
                if(!"".equals(contents)){
                    Message message = new Message(true, username, username, "", contents, "text", new Date().toString(), null);
                    msgList.add(message);
                    // 发送消息
                    sendMsg(username, token,type, target_id, contents, "text");
                    adapter.notifyItemInserted(msgList.size() - 1);
                    recyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");
                }
            }
        });

        sendImg = (ImageView)findViewById(R.id.send_image);
        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 2);
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (MessageService.DownloadBinder)iBinder;
            length = downloadBinder.getAllList().size();
            running = true;
            task = new MyTask();
//            task.execute();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[0]);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private class MyTask extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            while (running){
                try {
                    if(downloadBinder.getRecordIsNull() == 1){
                        // 暂无消息
                        ++ recordIsNull;
                        publishProgress();
                    }else if(downloadBinder.getTokenFlag() == 1){
                        isLawfulToken = false;
                        publishProgress();
                    }else if(first == 1&&downloadBinder.getMsgFlag()==0){
                        downloadBinder.setMsgFlag(1);
                        // 第一次获取聊天记录
                        msgList.addAll(downloadBinder.getUpdateList());
                        downloadBinder.setNewMsg(0);
                        downloadBinder.setMsgFlag(0);
                        if(msgList.size()>0){
                            publishProgress();
                            first = 0;
                        }
                    }else if(downloadBinder.getNewMsg() == 1&&downloadBinder.getMsgFlag()==0){
                        // 有新消息
                        downloadBinder.setMsgFlag(1);
                        updateList.addAll(downloadBinder.getUpdateList());
                        if(updateList.size()>msgList.size()){
                            for (int i = msgList.size();i < updateList.size(); ++i){
                                msgList.add(updateList.get(i));
                            }
                        }else if(updateList.size()<msgList.size()){
                            // 撤回了消息
                            msgList.clear();
                            msgList.addAll(updateList);
                        }

                        updateList.clear();
                        downloadBinder.setMsgFlag(0);
                        downloadBinder.setNewMsg(0);
                        publishProgress();
                    }
                    Thread.sleep(3000);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            if(recordIsNull == 1){
                Toast.makeText(ChatActivity.this, "暂无消息", Toast.LENGTH_SHORT).show();
                ++ recordIsNull;
            }else if(!isLawfulToken){
                Toast.makeText(ChatActivity.this, "Token失效, 请返回重试", Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
//            recyclerView.scrollToPosition(msgList.size() - 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2&&data!=null){
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }catch (Exception e){
                e.printStackTrace();
            }

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = 600;
            int newHeight = 600;
            float scaleWidth = (float)newWidth / width;
            float scaleHeight = (float)newHeight / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap mBitmap = Bitmap.createBitmap(bitmap, 0, 0,width,height,matrix, true);
            String result = ImageUtil.BitmapToBase64(mBitmap);
            Message message = new Message(true, username, username, "", "","image","", mBitmap);
            msgList.add(message);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(msgList.size()-1);
            // 发送图片到服务器

            sendMsg(username, token, type, target_id, result, "image");

        }
    }
    private void sendMsg(final String clientId, final String token, final String type, final String target_id, final String contents, final String contentsType){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = "client_id="+clientId+"&token="+token+"&type="+type
                        +"&target_id="+target_id+"&contents="+contents+"&contentsType=" + contentsType;
                try {
                    String response = HTTPUtil.POST("sendmsg", data);
                    if(response!=null){
                        android.os.Message msg = new android.os.Message();
                        msg.what = 2;
                        Bundle bundle = new Bundle();
                        bundle.putString("response", response);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void withdrawMsg(final String clientId, final String token, final String resultId, final int position){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = "client_id="+clientId+"&token="+token+"&resultid=" + resultId;
                try {
                    String response = HTTPUtil.GET("withdrawmsg", data);
                    if(response!=null){
                        android.os.Message msg = new android.os.Message();
                        msg.what = 3;
                        Bundle bundle = new Bundle();
                        bundle.putString("response", response);
                        bundle.putInt("position", position);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 2){
                String response = msg.getData().getString("response");
                try{
                    JSONObject json = new JSONObject(response);
                    if(json.getString("code").equals("0")){
                        String resultId = json.getString("resultid");
                        Message lastMsg = msgList.get(msgList.size()-1);
                        lastMsg.setRecordGuid(resultId);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(msg.what==3){
                String response = msg.getData().getString("response");
                int position = msg.getData().getInt("position");

                try{
                    JSONObject json = new JSONObject(response);
                    String code = json.getString("code");
                    if("0".equals(code)){
                        msgList.remove(position);
                        adapter.notifyItemRemoved(position);
                        recyclerView.scrollToPosition(msgList.size()-1);
                        Toast.makeText(getApplicationContext(), "消息已撤回", Toast.LENGTH_SHORT).show();

                    }else if("100".equals(code)){
                        Toast.makeText(getApplicationContext(), "参数为空", Toast.LENGTH_SHORT).show();
                    }else if("101".equals(code)){
                        Toast.makeText(getApplicationContext(), "超时无法撤回", Toast.LENGTH_SHORT).show();
                    }else if("102".equals(code)){
                        Toast.makeText(getApplicationContext(), "token验证失败", Toast.LENGTH_SHORT).show();
                    }else if("103".equals(code)){
                        Toast.makeText(getApplicationContext(), "消息guid错误", Toast.LENGTH_SHORT).show();
                    }else if("104".equals(code)){
                        Toast.makeText(getApplicationContext(), "撤回消息失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(msg.what == 4){
                String response = msg.getData().getString("response");
                try {
                    JSONObject json = new JSONObject(response);
                    String error = json.getString("error");

                    if("0".equals(error)){
                        JSONArray messages = json.getJSONArray("msglist");
                        for (int i = 0;i<messages.length();++i){
                            JSONObject msgObj = messages.getJSONObject(i);
                            String sender = msgObj.getString("sender");
                            String senderId = msgObj.getString("sender_id");
                            String recordGuid = msgObj.getString("record_guid");
                            String contents = msgObj.getString("contents");
                            String contentsType = msgObj.getString("contentstype");
                            String sendTime = msgObj.getString("sendtime");
                            boolean isMine = false;
                            Bitmap bitmap = null;
                            Message message = null;

                            if(username.equals(senderId)){
                                isMine = true;
                                if ("image".equals(contentsType)){
                                    contents = contents.replaceAll(" ", "+");
                                    bitmap = ImageUtil.transitionContents(contents);
                                    contents = "";
                                }
                            }

                            message = new Message(isMine, sender, senderId, recordGuid, contents, contentsType, sendTime, bitmap);
                            updateList.add(message);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onStop() {
        if(task != null&&task.getStatus()!= AsyncTask.Status.RUNNING){
            task.cancel(true);
        }
        running = false;
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        if(task != null&&task.getStatus()!= AsyncTask.Status.FINISHED){
            task.cancel(true);
        }
        running = false;
        super.onDestroy();
    }
}