package com.crainyday.mychat.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crainyday.mychat.R;
import com.crainyday.mychat.utils.HTTPUtil;
import org.json.JSONObject;


// 登录Activity, 登录成功后跳转到 首页 MainActivity
public class LoginActivity extends AppCompatActivity {
    private EditText usernameET;
    private EditText passwordET;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameET = (EditText) findViewById(R.id.usernameEditView);
        passwordET = (EditText) findViewById(R.id.passwordEditView);
        // 登录按钮
        Button loginBtn = (Button) this.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin(view);
            }
        });
//        // 跳转到 MainActivity
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        // 传递参数
//        intent.putExtra("token", "123456");
//        intent.putExtra("username", username);
//        startActivity(intent);
    }

    // 获取用户名密码, 进行登录
    private void doLogin(View view) {
        // 在子线程中进行网络IO操作
        new Thread() {
            public void run() {
                username = usernameET.getText().toString().trim();
                password = passwordET.getText().toString().trim();

                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "用户名、密码不能为空", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    try {
                        String request = "client_id=" + username + "&client_pwd=" + password + "&client_tag=1234-5678-ABCD-EFG";
                        String resp = HTTPUtil.GET("authorize", request);
                        JSONObject response = new JSONObject(resp);

                        if (response.getString("error").equals("0")) {
                            // 登录成功
                            final String token = response.getString("token");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
                                    // 跳转到 MainActivity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    // 传递参数
                                    intent.putExtra("token", token);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else if (response.getString("error").equals("100")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "学号不存在", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (response.getString("error").equals("101")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "年度id不正确", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (response.getString("error").equals("102")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "请求参数有空值", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "服务器异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }.start();
    }
}