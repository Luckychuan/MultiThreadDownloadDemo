package com.example.luckychuan.downloaddemo;

/**
 * Created by Luckychuan on 2018/3/14.
 */

public interface DownloadModel {

    void addDownloadTask(String url);
    void pauseDownload(String url);
    void cancelDownload(String url);
    void saveProgress(long downloadedLength);

}
