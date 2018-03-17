package com.example.luckychuan.downloaddemo;

/**
 * Created by Luckychuan on 2018/3/12.
 */

public interface DownloadView {

    void onDownloadStart(String url);

    void onDownloadPause(String url);

    void updateProgress(String url, int progress);

    void onFail(String url);

    void onCancel(String url);

    void onFinish();

}
