package com.example.luckychuan.downloaddemo;

/**
 * Created by Luckychuan on 2018/3/12.
 */

public interface DownloadView {

    void onInitFinish(String url,String name,long contentLength);

    void onDownloadStart(String url);

    void onDownloadPause(String url);

    void updateProgress(String url, long downloadedLength);

    void onFail(String url);

    void onCancel(String url);

}
