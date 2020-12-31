package com.crainyday.mychat.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crainyday.mychat.R;
import com.crainyday.mychat.activity.ChatActivity;
import com.crainyday.mychat.activity.LoginActivity;
import com.crainyday.mychat.activity.MainActivity;
import com.crainyday.mychat.adapter.UserAdapter;
import com.crainyday.mychat.entity.User;
import com.crainyday.mychat.utils.HTTPUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {
    private View view;
    private String token;
    private String username;

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        token = getArguments().getString("token");
        username = getArguments().getString("username");

        // 初始化所有的联系人信息
        if(userList.size()==0)
            initUserList();
        // 初始化滚动列表
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView)view.findViewById(R.id.online_list_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new UserAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, User user) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("token", token);
                intent.putExtra("target_id", user.getClientId());
                intent.putExtra("target_name", user.getName());
                startActivity(intent);
            }
        });
    }

    private void initUserList() {
        User user = new User("Android开发技术交流", "20171222000", "" + R.drawable.group);
        userList.add(user);

        // 在子线程中进行网络IO操作
        new Thread() {
            public void run() {
                try {
                    String request = "client_id=" + username + "&token=" + token + "&type=online&target_id=";
                    String response = HTTPUtil.GET("getuserlist", request);
                    if (response != null) {
                        Message msg = new Message();
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("response", response);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.setHandler(handler);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                String response = msg.getData().getString("response");
                try{
                    JSONObject json = new JSONObject(response);
                    if(json.getString("error").equals("0")){
                        JSONArray users = json.getJSONArray("userlist");
                        for (int i = 0;i < users.length(); ++i){
                            JSONObject user = users.getJSONObject(i);
                            String name = user.getString("name");
                            String clientId = user.getString("client_id");
                            String image = user.getString("image");

                            userList.add(new User(name, clientId, "" + R.drawable.avatar));
                        }
                        // 通知更新
                        adapter.notifyItemInserted(userList.size() - 1);
                    }else{
                        Toast.makeText(getActivity(), "Token失效", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(msg.what == 2){
                token = msg.getData().getString("token");
            }
        }
    };

}