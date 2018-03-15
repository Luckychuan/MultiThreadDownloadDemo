package com.example.luckychuan.downloaddemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;



public class DownloadService extends Service {

    private DownloadManager mManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }

    class DownloadBinder extends Binder {

        private DownloadView mView;

        public void setDownloadView(DownloadView view){
            mView = view;
            mManager = new DownloadManager(mView);
        }





        public void startDownload(String url) {
            mManager.addDownloadTask(url);
        }

        public void pauseDownload(String url) {
            mManager.pauseDownload(url);
        }

        public void cancelDownload(String url) {
            mManager.cancelDownload(url);
        }


    }

}
