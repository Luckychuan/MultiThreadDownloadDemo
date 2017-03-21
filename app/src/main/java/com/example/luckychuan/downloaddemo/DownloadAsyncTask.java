package com.example.luckychuan.downloaddemo;

import android.os.AsyncTask;

/**
 * @Integer 下载的进度
 * @Integer 下载状态的提示
 */
public class DownloadAsyncTask extends AsyncTask<Void, Integer, Integer> {

    private static final String TAG = "DownloadAsyncTask";

    private int mStatus;
    public static final int STATUS_DOWNLOADING = 0;
    public static final int STATUS_SUCCEED = 1;
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_PAUSED = 3;
    public static final int STATUS_CANCELED = 4;

    private Task task;

    private DownloadListener mListener;

    public DownloadAsyncTask(Task t, DownloadListener listener) {
        mListener = listener;

        task = t;
        String url = task.getUrl();
        task.setName(url.substring(url.lastIndexOf("/") + 1));

        //模拟长度
        task.setContentLength(100);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //修改UI
        MainActivity.setStartButtonText(true);
//        MainActivity.initProgressBar(progress,100);

    }


    @Override
    protected Integer doInBackground(Void... params) {
        mStatus = STATUS_DOWNLOADING;

        //模拟下载
        //获取已下载的进度
        int progress = (int) (task.getDownloadedLength() * 100 / task.getContentLength());
        for (int i = progress; i <= 100; i++) {
            if (mStatus == STATUS_PAUSED || mStatus == STATUS_CANCELED) {
                return mStatus;
            }
            i++;
            task.setDownloadedLength(i);
            progress = (int) (task.getDownloadedLength() * 100 / task.getContentLength());
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(progress);
        }

        if (task.getDownloadedLength() >= task.getContentLength()) {
            mStatus = STATUS_SUCCEED;
        } else {
            mStatus = STATUS_FAILED;
        }
        return mStatus;

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

    public void setPause() {
        MainActivity.setStartButtonText(false);
        mStatus = STATUS_PAUSED;
    }

    public void setCanceled() {
        mStatus = STATUS_CANCELED;
    }

    public boolean isDownloading() {
        if (mStatus == STATUS_DOWNLOADING) {
            return true;
        } else {
            return false;
        }
    }


    interface DownloadListener {
        void DownloadResult(int result);
    }


}
