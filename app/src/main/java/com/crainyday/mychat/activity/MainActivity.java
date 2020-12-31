package com.crainyday.mychat.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crainyday.mychat.R;
import com.crainyday.mychat.fragment.ContactsFragment;
import com.crainyday.mychat.fragment.MeFragment;
import com.crainyday.mychat.fragment.MessageFragment;
import com.crainyday.mychat.service.MessageService;
import com.crainyday.mychat.service.RefreshService;


// 首页Activity, 登录后跳转到此
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String token;
    private String username;
    private boolean running = false;

    private TextView my_title_bar;
    private RelativeLayout main_body;
    private TextView bottom_bar_text_1;
    private TextView bottom_bar_text_2;
    private TextView bottom_bar_text_3;
    private ImageView bottom_bar_image_1;
    private ImageView bottom_bar_image_2;
    private ImageView bottom_bar_image_3;

    private LinearLayout main_body_bar;
    private RelativeLayout bottom_bar_1_btn;
    private RelativeLayout bottom_bar_2_btn;
    private RelativeLayout bottom_bar_3_btn;

    private MessageFragment messageFragment;
    private ContactsFragment contactsFragment;
    private MeFragment meFragment;

    private RefreshService.RefreshBinder refreshBinder = null;
    private boolean isLawfulToken = true;
    private RefreshTask refreshTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        username = intent.getStringExtra("username");

        messageFragment = new MessageFragment();
        contactsFragment = new ContactsFragment();
        meFragment = new MeFragment();
        Bundle arguments = new Bundle();
        arguments.putString("token", token);
        arguments.putString("username", username);

        messageFragment.setArguments(arguments);
        contactsFragment.setArguments(arguments);
        meFragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, messageFragment).commitAllowingStateLoss();
        setSelectStatus(0);

        initFreshService();
    }

    private void initFreshService() {

        Intent bindIntent = new Intent(MainActivity.this, RefreshService.class);
        bindIntent.putExtra("username", username);
        bindIntent.putExtra("token", token);

        bindService(bindIntent, connection, BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            refreshBinder = (RefreshService.RefreshBinder) iBinder;
            running = true;
            refreshTask = new RefreshTask();
//            refreshTask.execute();
            refreshTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[0]);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private void setSelectStatus(int status) {
        switch (status){
            case 0:
                bottom_bar_text_1.setTextColor(Color.parseColor("#45C01A"));
                bottom_bar_image_1.setImageResource(R.drawable.message_press);

                bottom_bar_text_2.setTextColor(Color.parseColor("#464646"));
                bottom_bar_image_2.setImageResource(R.drawable.contacts_normal);
                bottom_bar_text_3.setTextColor(Color.parseColor("#464646"));
                bottom_bar_image_3.setImageResource(R.drawable.me_normal);
                break;
            case 1:
                bottom_bar_text_2.setTextColor(Color.parseColor("#45C01A"));
                bottom_bar_image_2.setImageResource(R.drawable.contacts_press);

                bottom_bar_text_1.setTextColor(Color.parseColor("#464646"));
                bottom_bar_image_1.setImageResource(R.drawable.message_normal);
                bottom_bar_text_3.setTextColor(Color.parseColor("#464646"));
                bottom_bar_image_3.setImageResource(R.drawable.me_normal);
                break;
            case 2:
                bottom_bar_text_3.setTextColor(Color.parseColor("#45C01A"));
                bottom_bar_image_3.setImageResource(R.drawable.me_press);

                bottom_bar_text_1.setTextColor(Color.parseColor("#464646"));
                bottom_bar_image_1.setImageResource(R.drawable.message_normal);
                bottom_bar_text_2.setTextColor(Color.parseColor("#464646"));
                bottom_bar_image_2.setImageResource(R.drawable.contacts_normal);
                break;
        }
    }
    private android.os.Handler handler = null;
    public void setHandler(android.os.Handler handler) {
        this.handler = handler;
    }
    private void initView() {

        my_title_bar = findViewById(R.id.my_title_tv);
        main_body = findViewById(R.id.main_body);
        bottom_bar_text_1 = findViewById(R.id.bottom_bar_text_1);
        bottom_bar_text_2 = findViewById(R.id.bottom_bar_text_2);
        bottom_bar_text_3 = findViewById(R.id.bottom_bar_text_3);
        bottom_bar_image_1 = findViewById(R.id.bottom_bar_image_1);
        bottom_bar_image_2 = findViewById(R.id.bottom_bar_image_2);
        bottom_bar_image_3 = findViewById(R.id.bottom_bar_image_3);

        main_body_bar = findViewById(R.id.main_body_bar);
        bottom_bar_1_btn = findViewById(R.id.bottom_bar_1_btn);
        bottom_bar_2_btn = findViewById(R.id.bottom_bar_2_btn);
        bottom_bar_3_btn = findViewById(R.id.bottom_bar_3_btn);

        bottom_bar_1_btn.setOnClickListener(this);
        bottom_bar_2_btn.setOnClickListener(this);
        bottom_bar_3_btn.setOnClickListener(this);
    }

    private class RefreshTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            while (running){
                try {
                    Thread.sleep(300000);
//                    Thread.sleep(3000);

                    if(refreshBinder.isNewToken()){
                        token = refreshBinder.getToken();
                        if(token!=null){
                            refreshBinder.setIsNewToken(false);
                            isLawfulToken = true;
                        }else {
                            isLawfulToken = false;
                        }
                        publishProgress();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            if(!isLawfulToken){
                Toast.makeText(MainActivity.this, "登录失效, 请退出后重新登录", Toast.LENGTH_LONG).show();
            }else{
                if (handler != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            /* 更新传递数据到Fragment */
                            Message message = new Message();
                            message.what = 2;
                            Bundle bundle = new Bundle();
                            bundle.putString("token", token);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bottom_bar_1_btn:
                my_title_bar.setText("在线聊天");
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, messageFragment).commitAllowingStateLoss();
                setSelectStatus(0);
                break;
            case R.id.bottom_bar_2_btn:
                my_title_bar.setText("通讯录");
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, contactsFragment).commitAllowingStateLoss();
                setSelectStatus(1);
                break;
            case R.id.bottom_bar_3_btn:
                my_title_bar.setText("我");
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, meFragment).commitAllowingStateLoss();
                setSelectStatus(2);
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
//        Intent intent = new Intent(MainActivity.this, RefreshService.class);
//        stopService(intent);

        super.onDestroy();
    }
}