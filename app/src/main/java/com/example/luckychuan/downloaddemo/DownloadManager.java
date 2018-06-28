package com.example.luckychuan.downloaddemo;

import android.os.AsyncTask;
import android.util.Log;
import java.util.HashMap;

/**
 * 管理AsyncTask多任务下载
 */

public class DownloadManager extends DownloadModel {

    private HashMap<String, DownloadAsyncTask> mMap = new HashMap<>();
    private DownloadView mView;

    public DownloadManager(DownloadView view) {
        mView = view;
    }


    @Override
    public void addDownloadTask(final String url) {
        DownloadAsyncTask asyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownLoadListener() {

            @Override
            public void onDownloadPause() {
                mMap.remove(url);
                mView.onDownloadPause(url);
            }

            @Override
            public void updateProgress(int progress) {
                mView.updateProgress(url, progress);
            }

            @Override
            public void onFail() {
                mMap.remove(url);
                mView.onFail(url);
            }

            @Override
            public void onCancel() {
                mMap.remove(url);
                mView.onCancel(url);

            }

            @Override
            public void onFinish() {
                mMap.remove(url);
            }
        });
        mMap.put(url, asyncTask);
        //实现多任务下载,开始任务
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    @Override
    public void pauseDownload(String url) {
        DownloadAsyncTask task = mMap.get(url);
        task.setPause();
        mMap.remove(task);
    }

    @Override
    public void cancelDownload(String url) {
        DownloadAsyncTask task = mMap.get(url);
        //当未下载时点击取消，要新建AsyncTask
        if (task == null) {
            addDownloadTask(url);
            task = mMap.get(url);
        }
        task.setCancel();
        mMap.remove(task);
    }

}
