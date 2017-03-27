package com.example.luckychuan.downloaddemo;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.Serializable;

/**
 * java bean类.
 */
public class Task implements Serializable {

    private static final String TAG = "Task";

    private String url;
    private String name;
    //文件大小
    private long contentLength;
    //已下载大小
    private long downloadedLength;
    //下载的线程
    private DownloadAsyncTask asyncTask;
    private boolean isDownloading;

    public Task(String url) {
        this.url = url;
    }

    public Task(String url, String name, long contentLength, long downloadedLength) {
        this.url = url;
        this.name = name;
        this.contentLength = contentLength;
        this.downloadedLength = downloadedLength;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void start() {
        isDownloading = true;
        DownloadAsyncTask asyncTask = new DownloadAsyncTask(this, new DownloadAsyncTask.DownloadListener() {
            @Override
            public void DownloadResult(int result) {
                if (result == DownloadAsyncTask.STATUS_SUCCEED) {
                    Log.d("Result", "DownloadResult: " + "下载成功");
                }else if(result == DownloadAsyncTask.STATUS_CANCELED) {
                    Log.d("Result", "DownloadResult: 取消");
                }else if(result == DownloadAsyncTask.STATUS_FAILED){
                    Log.d("Result", "DownloadResult: 失败");
                }
            }
        });
        this.asyncTask = asyncTask;
        //实现并行下载
        this.asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void pause() {
        isDownloading = false;
        asyncTask.pauseDownload();
        asyncTask = null;
    }

    public void cancel() {
        if(asyncTask !=null ){
            asyncTask.cancelDownload();
        }else{
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            File file = new File(directory + name);
            if (file.exists()){
                file.delete();
            }
        }
        DataSupport.deleteAll(TaskDB.class, "url=?", url);
    }


    /**
     * 以下为自动生成的方法
     */

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getDownloadedLength() {
        return downloadedLength;
    }

    public void setDownloadedLength(long downloadedLength) {
        this.downloadedLength = downloadedLength;
    }

    @Override
    public String toString() {
        return "Task{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", contentLength=" + contentLength +
                ", downloadedLength=" + downloadedLength +
                ", asyncTask=" + asyncTask +
                '}';
    }
}
