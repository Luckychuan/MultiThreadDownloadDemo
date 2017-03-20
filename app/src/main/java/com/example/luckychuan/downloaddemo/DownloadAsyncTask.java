package com.example.luckychuan.downloaddemo;

import android.os.AsyncTask;

/**
 * @String 需要下载文件的url
 * @Integer 下载的进度
 * @Integer 下载状态的提示
 */
public class DownloadAsyncTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = "DownloadAsyncTask";

    public static final int STATUS_SUCCEED = 1;
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_PUASED = 3;
    public static final int STATUS_CANCELED = 4;

    private DownloadListener mListener;

    private String mURL;
    private String mFileName;
    //文件大小
    private long mContentLength;
    //已下载大小
    private long mDownloadLength;

    public DownloadAsyncTask(String url, DownloadListener listener) {
        mListener = listener;
        mURL = url;
        mFileName = mURL.substring(mURL.lastIndexOf("/")+1);

        //模拟长度
        mContentLength = 100;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //修改UI
        MainActivity.setStartButtonText(true);
//        MainActivity.initProgressBar(progress,100);

    }


    @Override
    protected Integer doInBackground(String... params) {
        mURL = params[0];

        //模拟下载
        int progress;
        for (int i = 0; i <=100; i++) {
            i ++;
            mDownloadLength = i;
            progress = (int)(mDownloadLength*100/mContentLength);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(progress);
        }
        //判断下载状态

        if(mDownloadLength == mContentLength){
            return STATUS_SUCCEED;
        }else{
            return STATUS_FAILED;
        }

    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.DownloadResult(integer);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        MainActivity.showProgress(values[0]);
    }


    interface DownloadListener {
        void DownloadResult(int result);
    }


}
