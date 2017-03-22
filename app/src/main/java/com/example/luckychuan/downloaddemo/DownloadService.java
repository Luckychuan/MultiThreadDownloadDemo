package com.example.luckychuan.downloaddemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    //任务列表
    private HashMap<String, Task> mTaskMap;

    @Override
    public void onCreate() {
        super.onCreate();
        mTaskMap = new HashMap<>();
        List<TaskDB> taskList = DataSupport.findAll(TaskDB.class);
        for (TaskDB taskData : taskList) {
            Task task = new Task(taskData.getUrl(), taskData.getName(), taskData.getContentLength(), taskData.getDownloadedLength());
            mTaskMap.put(task.getUrl(), task);
        }
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

        /**
         * @param url 通过url从map里找到需要下载的任务
         */
        public void startDownload(String url) {
            Log.d(TAG, "startDownload: " + url);
            Task task = mTaskMap.get(url);
            task.start();
        }

        public void newTask(String url) {
            Task task = new Task(url);
            task.start();
            mTaskMap.put(url, task);
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

        public boolean isDownloading(String url) {
            return mTaskMap.get(url).isDownloading();
        }

    }

}
