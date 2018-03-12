package com.example.luckychuan.downloaddemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DownloadView {

    private static final String TAG = "MainActivity";
    private static final String URL1 = "http://m.down.sandai.net/MobileThunder/Android_5.34.2.4700/XLWXguanwang.apk";
    private static final String URL2 = "http://s1.music.126.net/download/android/CloudMusic_official_4.0.0_179175.apk";

    //绑定Service，实现Activity和Service通信
    private DownloadService.DownloadBinder mServiceBinder;

    //RecyclerView的数据源
    private static ArrayList<Task> mTasks = new ArrayList<>();;
    private static RecyclerAdapter mAdapter;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBinder = (DownloadService.DownloadBinder) service;
            mServiceBinder.setDownloadView(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取权限
        int readStorageCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readStorageCheck == PackageManager.PERMISSION_GRANTED) {
            afterPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //当权限获得时
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            afterPermissionGranted();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //弹出对话框提示用户接收权限
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("程序要获得权限后才能运行");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //请求读取手机存储的权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                });
                dialog.create().show();
            }
        }
    }


    private void afterPermissionGranted() {
//        ((Button) findViewById(R.id.new_task_btn)).setOnClickListener(this);
//        ((Button) findViewById(R.id.new_task2_btn)).setOnClickListener(this);
//        ((Button) findViewById(R.id.query)).setOnClickListener(this);

//        //从数据库中获取数据
//        List<TaskDB> taskList = DataSupport.findAll(TaskDB.class);
//        if (taskList.size() != 0) {
//            for (TaskDB taskDB : taskList) {
//                mTasks.add(new Task(taskDB.getUrl(), taskDB.getName(), taskDB.getContentLength(), taskDB.getDownloadedLength()));
//            }
//        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //防止进度刷新时闪烁
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new RecyclerAdapter(mTasks, new RecyclerAdapter.OnItemButtonClickListener() {
            @Override
            public void onStartButtonClick(Task task, boolean toStartDownload) {
                if (toStartDownload) {
                    mServiceBinder.startDownload(task);
                } else {
                    mServiceBinder.pauseDownload(task.getUrl());
                }
            }

            @Override
            public void onCancelButtonClick(String url) {
                //将Task从list中移除
                for (int i = 0; i < mTasks.size(); i++) {
                    if (mTasks.get(i).getUrl().equals(url)) {
                        mTasks.remove(i);
                        mAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
                mServiceBinder.cancelDownload(url);
            }
        });
        recyclerView.setAdapter(mAdapter);

//        //创建LitePal数据库
//        Connector.getDatabase();

        //绑定Service
        Intent serviceIntent = new Intent(this, DownloadService.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.new_task_btn:
//                newTask(URL1);
//                break;
//
//            case R.id.new_task2_btn:
//                newTask(URL2);
//                break;
//
//            case R.id.query:
//                List<TaskDB> taskList = DataSupport.findAll(TaskDB.class);
//                if (taskList.size() == 0) {
//                    Log.d(TAG, "onClick: 无数据");
//                }
//                for (TaskDB t : taskList) {
//                    Log.d(TAG, "onClick: " + t.getUrl());
//                    Log.d(TAG, "onClick: " + t.getName());
//                    Log.d(TAG, "onClick: " + t.getDownloadedLength());
//                    Log.d(TAG, "onClick: " + t.getContentLength());
//                }
//                break;
//        }
//    }

//    private void newTask(String url) {
//        for (Task t : mTasks) {
//            if (t.getUrl().equals(url)) {
//                Toast.makeText(MainActivity.this, "任务已经存在", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//        Task task = new Task(url);
//        mTasks.add(task);
//        mAdapter.notifyItemInserted(mTasks.size() - 1);
//        mServiceBinder.newTask(task);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        stopService(new Intent(this, DownloadService.class));
    }


    private int getPosition(String url) {
        for (int i = 0; i < mTasks.size(); i++) {
            Task task = mTasks.get(i);
            if(url.equals(task.getUrl())){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onInitFinish(String url, String name,long contentLength) {
        Task task = new Task(url,name,contentLength);
        mTasks.add(task);
        mAdapter.notifyItemInserted(mTasks.size()-1);
    }

    @Override
    public void onDownloadStart(String url) {
        int position = getPosition(url);
        Task task = mTasks.get(position);
        task.setDownloading(true);
        mAdapter.notifyItemChanged(position,false);
    }

    @Override
    public void onDownloadPause(String url) {
        int position = getPosition(url);
        Task task = mTasks.get(position);
        task.setDownloading(false);
        mAdapter.notifyItemChanged(position,false);
    }

    @Override
    public void updateProgress(String url, long downloadedLength) {
        int position = getPosition(url);
        Task task = mTasks.get(position);
        task.setDownloadedLength(downloadedLength);
        mAdapter.notifyItemChanged(position,false);
    }

    @Override
    public void onFail(String url) {
        int position = getPosition(url);
        Task task = mTasks.get(position);
        task.setContentLength(0);
        mAdapter.notifyItemChanged(position,false);
    }

    @Override
    public void onCancel(String url) {
        int position = getPosition(url);
        mAdapter.notifyItemRemoved(position);
        mTasks.remove(position);
    }
}
