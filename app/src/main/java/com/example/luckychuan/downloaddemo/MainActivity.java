package com.example.luckychuan.downloaddemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String URL = "http://www.imooc.com/mobile/imooc.apk";

    //绑定Service，实现Activity和Service通信
    private DownloadService.DownloadBinder mServiceBinder;

    private Button mStartButton;
    private TextView mFileName;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartButton = (Button)findViewById(R.id.start_btn);
        mFileName = (TextView) findViewById(R.id.file_name);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        ((Button)findViewById(R.id.new_task)).setOnClickListener(this);

        mStartButton.setOnClickListener(this);

        //绑定Service
        Intent serviceIntent = new Intent(this,DownloadService.class);
        startService(serviceIntent);
        bindService(serviceIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (DownloadService.DownloadBinder)service;
                mServiceBinder.startDownload("test");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_btn:
                mServiceBinder.startDownload(URL);
                break;
            case R.id.new_task:
                //初始化UI
                mFileName.setText(URL.lastIndexOf("/"));
                mStartButton.setText(getResources().getString(R.string.pause));
                mServiceBinder.startDownload("test");
        }
    }
}
