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

    private static Button mStartButton;
    private TextView mFileName;
    private static ProgressBar mProgressBar;
    private static TextView mProgressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartButton = (Button) findViewById(R.id.start_btn);
        mFileName = (TextView) findViewById(R.id.file_name);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        ((Button) findViewById(R.id.new_task)).setOnClickListener(this);
        mProgressText = (TextView) findViewById(R.id.progress_text);

        mStartButton.setOnClickListener(this);

        //绑定Service
        Intent serviceIntent = new Intent(this, DownloadService.class);
        startService(serviceIntent);
        bindService(serviceIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (DownloadService.DownloadBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:
                if (!mServiceBinder.isDownloading(URL)) {
                    mServiceBinder.startDownload(URL);
                    mStartButton.setText(getResources().getString(R.string.pause));
                } else {
                    mServiceBinder.pauseDownload(URL);
                    mStartButton.setText(getResources().getString(R.string.start));
                }
                break;
            case R.id.new_task:
                //初始化UI
                mFileName.setText(URL.substring(URL.lastIndexOf("/") + 1));
                mServiceBinder.newTask(URL);
                mStartButton.setText(getResources().getString(R.string.pause));
                break;
        }
    }

//    public static void setStartButtonText(boolean isDownloading) {
//        if (isDownloading) {
//            mStartButton.setText("暂停");
//        } else {
//            mStartButton.setText("继续");
//        }
//    }


    public static void showProgress(int progress) {
        mProgressBar.setProgress(progress);
        mProgressText.setText("已下载：" + progress);
    }

}
