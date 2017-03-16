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
    private HashMap<String,DownloadAsyncTask> taskMap;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }

    class DownloadBinder extends Binder{

        public void startDownload(String url){
            Log.d(TAG, "startDownload: ");
            //判断是否是新任务
        }

        public void pauseDownload(String url){
            Log.d(TAG, "pauseDowload: ");
        }

        public void cancelDownload(String url){
            Log.d(TAG, "cancelDownload: ");
        }
    }

}
