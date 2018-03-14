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

public class MainActivity extends AppCompatActivity implements DownloadView, RecyclerAdapter.OnItemButtonClickListener {

    private static final String TAG = "MainActivity";
    private static final String URL1 = "http://m.down.sandai.net/MobileThunder/Android_5.34.2.4700/XLWXguanwang.apk";
    private static final String URL2 = "http://s1.music.126.net/download/android/CloudMusic_official_4.0.0_179175.apk";

    //RecyclerView的数据源
    private static ArrayList<Task> mTasks = new ArrayList<>();;
    private static RecyclerAdapter mAdapter;

    //绑定Service，实现Activity和Service通信
    private DownloadService.DownloadBinder mServiceBinder;
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



    /**
     * 以下为获取读写权限，建立Activity与Service通信连接,初始化UI部分
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定Service
        Intent serviceIntent = new Intent(this, DownloadService.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);

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
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        stopService(new Intent(this, DownloadService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //当权限获得时
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initUI();
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

    private void initUI() {
//        //从数据库中获取数据
//        List<TaskDB> taskList = DataSupport.findAll(TaskDB.class);
//        if (taskList.size() != 0) {
//            for (TaskDB taskDB : taskList) {
//                mTasks.add(new Task(taskDB.getUrl(), taskDB.getName(), taskDB.getContentLength(), taskDB.getDownloadedLength()));
//            }
//        }

        //        //创建LitePal数据库
//        Connector.getDatabase();
        // TODO: 2018/3/12
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //防止进度刷新时闪烁
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new RecyclerAdapter(mTasks, this);
        recyclerView.setAdapter(mAdapter);
    }



    /**
     *  以下为UI操控逻辑部分
     *
     */

    private void newTask(String url) {
        //判断任务是否已经存在
        for (Task t : mTasks) {
            if (t.getUrl().equals(url)) {
                Toast.makeText(MainActivity.this, "任务已经存在", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        mServiceBinder.newTask(url);
    }

    @Override
    public void onStartButtonClick(Task task, boolean toStartDownload) {
        if (toStartDownload) {
            mServiceBinder.startDownload(task.getUrl(),task.getDownloadedLength(),task.getContentLength());
        } else {
            mServiceBinder.pauseDownload(task.getUrl());
        }
    }

    @Override
    public void onCancelButtonClick(String url) {
        mServiceBinder.cancelDownload(url);
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


    /**
     * 以下为逻辑模块回调更新UI
     * @param url
     * @param name
     * @param contentLength
     */
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

    /**
     * 通过url找到当前task在list的position
     * @param url
     * @return
     */
    private int getPosition(String url) {
        for (int i = 0; i < mTasks.size(); i++) {
            Task task = mTasks.get(i);
            if(url.equals(task.getUrl())){
                return i;
            }
        }
        return -1;
    }


}
