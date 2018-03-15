package com.example.luckychuan.downloaddemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;



public class DownloadService extends Service {

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



        public void newTask(String url) {
            // TODO: 2018/3/12 新建任务

        }

        public void startDownload(String url) {
            // TODO: 2018/3/14
        }

        public void pauseDownload(String url) {
            // TODO: 2018/3/14
        }

        public void cancelDownload(String url) {
            // TODO: 2018/3/14
        }


    }

}
