package com.example.luckychuan.downloaddemo;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * 用于 1.管理AsyncTask多任务下载；2.操作数据保存下载进度
 */

public class DownloadManager implements DownloadModel {

    private HashMap<String, DownloadAsyncTask> mMap = new HashMap<>();
    private DownloadView mView;

    public DownloadManager(DownloadView view) {
        mView = view;
    }


    @Override
    public void newTask(final String url) {
        DownloadAsyncTask asyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownLoadListener() {
            @Override
            public void onInitFinish(String name) {
                // TODO: 2018/3/14 数据库，insert
//                TaskDB taskDB = new TaskDB();
//                taskDB.setUrl(url);
//                taskDB.setName(name);
//                taskDB.setDownloadedLength(0);
//                taskDB.save();
                mView.onInitFinish(url, name);
            }

            @Override
            public void onDownloadStart() {
                mView.onDownloadStart(url);
            }

            @Override
            public void onDownloadPause() {
                mView.onDownloadPause(url);
            }

            @Override
            public void updateProgress(int progress) {
                mView.updateProgress(url,progress);
            }

            @Override
            public void onFail() {
                mView.onFail(url);
            }

            @Override
            public void onCancel() {
                mMap.remove(url);
                // TODO: 2018/3/14 数据库操作
                //        DataSupport.deleteAll(TaskDB.class, "url=?", url);
                mView.onCancel(url);

            }

            @Override
            public void onFinish() {
                mMap.remove(url);
                // TODO: 2018/3/14 数据库操作
                //        DataSupport.deleteAll(TaskDB.class, "url=?", url);

            }
        });
        mMap.put(url, asyncTask);
        //实现多任务下载,开始任务
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void startDownload(String url) {
        mMap.get(url).execute();
    }

    @Override
    public void pauseDownload(String url) {
        mMap.get(url).setPause();
    }

    @Override
    public void cancelDownload(String url) {
        mMap.get(url).setCancel();
    }

    @Override
    public void saveProgress(long downloadedLength) {
        // TODO: 2018/3/14 数据库 update
    }
}
