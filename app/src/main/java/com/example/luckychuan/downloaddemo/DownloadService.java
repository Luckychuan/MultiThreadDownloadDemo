package com.example.luckychuan.downloaddemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;


public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    //任务列表
    private HashMap<String, Task> taskMap;

    @Override
    public void onCreate() {
        super.onCreate();
        taskMap = new HashMap<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }

    class DownloadBinder extends Binder  {

        /**
         * @param url 通过url从map里找到需要下载的任务
         */
        public void startDownload(String url) {
            Log.d(TAG, "startDownload: " + url);
            Task task = taskMap.get(url);
            task.start();
        }

        public void newTask(String url) {
            Task task = new Task(url);
            task.start();
            taskMap.put(url, task);
        }

        public void pauseDownload(String url) {
            Log.d(TAG, "pauseDownload: ");
            Task task = taskMap.get(url);
            task.pause();
        }

//        public void cancelDownload(String url) {
//            Log.d(TAG, "cancelDownload: ");
//            taskMap.get(url).getAsyncTask().setCanceled();
//            taskMap.remove(url);
//        }

        public boolean isDownloading(String url) {
            return taskMap.get(url).isDownloading();
        }

    }

}
