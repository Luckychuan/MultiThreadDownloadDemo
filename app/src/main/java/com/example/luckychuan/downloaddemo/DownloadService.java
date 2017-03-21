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

    class DownloadBinder extends Binder {

        /**
         * @param url 通过url从map里找到需要下载的任务
         */
        public void startDownload(String url) {
            Log.d(TAG, "startDownload: " + url);
      //      taskMap.get(url).execute(url);

        }

        public void newTask(String url) {
            final Task task = new Task(url);
            DownloadAsyncTask asyncTask = new DownloadAsyncTask(task, new DownloadAsyncTask.DownloadListener() {
                @Override
                public void DownloadResult(int result) {
                    if (result == DownloadAsyncTask.STATUS_SUCCEED) {
                        Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DownloadResult: "+task.toString());
                    }
                }
            });
            task.setUrl(url);
            task.setAsyncTask(asyncTask);
            taskMap.put(url, task);
            asyncTask.execute();
        }

        public void pauseDownload(String url) {
            Log.d(TAG, "pauseDownload: ");
            taskMap.get(url).getAsyncTask().setPause();
        }

        public void cancelDownload(String url) {
            Log.d(TAG, "cancelDownload: ");
            taskMap.get(url).getAsyncTask().setCanceled();
            taskMap.remove(url);
        }

        public boolean isDownloading(String url) {
            return taskMap.get(url).getAsyncTask().isDownloading();
        }

    }

}
