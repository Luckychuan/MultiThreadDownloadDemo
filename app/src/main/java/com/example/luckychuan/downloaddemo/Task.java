package com.example.luckychuan.downloaddemo;

import java.io.Serializable;

/**
 * java bean类.
 */
public class Task implements Serializable {

    private String url;
    private String name;
    private int progress;

    private boolean isDownloading;

    public Task(String url, String name) {
        this.url = url;
        this.name = name;
        isDownloading = true;
    }


    /**
     * 以下为自动生成的方法
     */

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
