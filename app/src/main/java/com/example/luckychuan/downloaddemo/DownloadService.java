package com.example.luckychuan.downloaddemo;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    //任务列表
    private HashMap<String, Task> mTaskMap;


    @Override
    public void onCreate() {
        mTaskMap = new HashMap<>();
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Map.Entry<String, Task> entry : mTaskMap.entrySet()) {
            entry.getValue().pause();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }

    class DownloadBinder extends Binder {

        private DownloadView mView;

        public void setDownloadView(DownloadView view){
            mView = view;
        }


        public void startDownload(Task task) {
            //用于判断map里是否有相同的任务
            boolean isContain = false;
            for (Map.Entry<String, Task> entry : mTaskMap.entrySet()) {
                //map里面已经有相同的任务
                if(entry.getValue().getUrl().equals(task.getUrl())){
                    isContain = true;
                    break;
                }
            }
            if(!isContain){
                mTaskMap.put(task.getUrl(),task);
            }
            task.start();
        }

        public void newTask(Task task) {
            task.start();
            mTaskMap.put(task.getUrl(), task);
        }

        public void pauseDownload(String url) {
            Log.d(TAG, "pauseDownload: ");
            Task task = mTaskMap.get(url);
            task.pause();
        }

        public void cancelDownload(String url) {
            Log.d(TAG, "cancelDownload: ");
            mTaskMap.get(url).cancel();
            mTaskMap.remove(url);
        }


    }

}
