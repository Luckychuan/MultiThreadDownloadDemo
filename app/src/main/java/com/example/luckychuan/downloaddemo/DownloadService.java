package com.example.luckychuan.downloaddemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;


public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    //任务列表
    private HashMap<String,DownloadAsyncTask> taskMap;

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

    class DownloadBinder extends Binder{

        /**
         *
         * @param url 通过url从map里找到需要下载的任务
         */
        public void startDownload(String url){
            Log.d(TAG, "startDownload: " + url);
            taskMap.get(url).execute(url);

        }

        public void newTask(String url){
            DownloadAsyncTask task = new DownloadAsyncTask(url,new DownloadAsyncTask.DownloadListener() {
                @Override
                public void DownloadResult(int result) {
                    if(result == DownloadAsyncTask.STATUS_SUCCEED){
                        Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            taskMap.put(url,task);
            startDownload(url);
        }

        public void pauseDownload(String url){
            Log.d(TAG, "pauseDownload: ");
        }

        public void cancelDownload(String url){
            Log.d(TAG, "cancelDownload: ");
        }
    }

}
