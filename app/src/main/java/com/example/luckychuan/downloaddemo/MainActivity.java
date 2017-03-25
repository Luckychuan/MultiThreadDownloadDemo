package com.example.luckychuan.downloaddemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final String URL1 = "http://www.imooc.com/mobile/imooc.apk";
    private static final String URL2 = "http://s1.music.126.net/download/android/CloudMusic_official_4.0.0_179175.apk";

    //绑定Service，实现Activity和Service通信
    private DownloadService.DownloadBinder mServiceBinder;

    private static ArrayList<Task> mTasks;

    private static RecyclerAdapter mAdapter;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ((Button) findViewById(R.id.new_task_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.new_task2_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.query)).setOnClickListener(this);

        mTasks = new ArrayList<>();
        //从数据库中获取数据
        List<TaskDB> taskList = DataSupport.findAll(TaskDB.class);
        if (taskList.size() != 0) {
            for (TaskDB taskDB : taskList) {
                mTasks.add(new Task(taskDB.getUrl(), taskDB.getName(), taskDB.getContentLength(), taskDB.getDownloadedLength()));
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter(mTasks, new RecyclerAdapter.OnItemButtonClickListener() {
            @Override
            public void onStartButtonClick(String url, boolean toStartDownload) {
                if (toStartDownload) {
                    mServiceBinder.startDownload(url);
                } else {
                    mServiceBinder.pauseDownload(url);
                }
            }

            @Override
            public void onCancelButtonClick(String url) {
                mServiceBinder.cancelDownload(url);
            }
        });
        recyclerView.setAdapter(mAdapter);

        //创建LitePal数据库
        Connector.getDatabase();

        //绑定Service
        Intent serviceIntent = new Intent(this, DownloadService.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_task_btn:
                newTask(URL1);
                break;

            case R.id.new_task2_btn:
                newTask(URL2);
                break;

            case R.id.query:
                List<TaskDB> taskList = DataSupport.findAll(TaskDB.class);
                if (taskList.size() == 0) {
                    Log.d(TAG, "onClick: 无数据");
                }
                for (TaskDB t : taskList) {
                    Log.d(TAG, "onClick: " + t.getUrl());
                    Log.d(TAG, "onClick: " + t.getName());
                    Log.d(TAG, "onClick: " + t.getDownloadedLength());
                    Log.d(TAG, "onClick: " + t.getContentLength());
                }
                break;
        }
    }

    private void newTask(String url) {
        Task task = new Task(url);
        mTasks.add(task);
        mAdapter.notifyDataSetChanged();
        mServiceBinder.newTask(task);
    }

    public static void updateProgress(String url){
        for (int i = 0; i <mTasks.size() ; i++) {
            Task task = mTasks.get(i);
            if(task.getUrl().equals(url)){
                mAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        stopService(new Intent(this, DownloadService.class));
    }


}
