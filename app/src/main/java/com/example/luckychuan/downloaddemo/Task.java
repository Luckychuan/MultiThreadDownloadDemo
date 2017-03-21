package com.example.luckychuan.downloaddemo;

/**
 * java bean类.
 */
public class Task {

    private String url;
    private String name;
    //文件大小
    private long contentLength;
    //已下载大小
    private long downloadedLength;
    //下载的线程
    private DownloadAsyncTask asyncTask;

    public Task(String url){
        this.url = url;
    }

    public void setAsyncTask(DownloadAsyncTask asyncTask) {
        this.asyncTask = asyncTask;
    }

    public DownloadAsyncTask getAsyncTask() {
        return asyncTask;
    }

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
