package com.example.luckychuan.downloaddemo;

import android.os.AsyncTask;

/**
 * @String 需要下载文件的url
 *@Integer 下载的进度
 * @Integer 下载状态的提示
 */
public class DownloadAsyncTask extends AsyncTask<String,Integer,Integer> {

    private static final int STATUS_SUCCEED = 1;
    private static final int STATUS_FAILED = 2;
    private static final int STATUS_PUASED = 3;
    private static final int STATUS_CANCELED = 4;

    private DownloadListener mListener;

    private String mURL;
    private String mFileName;
    //文件大小
    private long mContentLength;
    //已下载大小
    private long mDownloadLength;

    public DownloadAsyncTask(String url,DownloadListener listener){
        mListener = listener;
        mURL = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //修改UI

    }


    @Override
    protected Integer doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }



    interface DownloadListener{
        void DownloadResult(int result);
    }


}
