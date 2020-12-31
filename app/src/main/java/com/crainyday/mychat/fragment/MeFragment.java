package com.crainyday.mychat.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


import com.crainyday.mychat.R;
import com.crainyday.mychat.activity.LoginActivity;
import com.crainyday.mychat.activity.MainActivity;


public class MeFragment extends Fragment {
    private Button switch_btn;
    private Button exit_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        switch_btn = (Button)view.findViewById(R.id.switch_btn);
        exit_btn = (Button)view.findViewById(R.id.exit_btn);
        switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setMessage("是否退出登录")
                        .setPositiveButton("是", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(mainActivity, LoginActivity.class);
                                startActivity(intent);
                                mainActivity.finish();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setMessage("是否退出程序")
                        .setPositiveButton("是", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
        });

        return view;
    }
}