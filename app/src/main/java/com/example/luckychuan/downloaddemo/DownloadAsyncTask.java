package com.example.luckychuan.downloaddemo;

import android.os.AsyncTask;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * @Integer 下载的进度
 * @Integer 下载状态的提示
 */
public class DownloadAsyncTask extends AsyncTask<Void, Integer, Integer> {

    private static final String TAG = "DownloadAsyncTask";

    private int mStatus;
    public static final int STATUS_DOWNLOADING = 0;
    public static final int STATUS_SUCCEED = 1;
    public static final int STATUS_PAUSED= 2;
    public static final int STATUS_CANCELED = 3;


    private Task task;

    private DownloadListener mListener;

    public DownloadAsyncTask(Task t, DownloadListener listener) {
        mListener = listener;

        task = t;
        String url = task.getUrl();
        String name = url.substring(url.lastIndexOf("/") + 1);
        task.setName(name);

        //判断任务是否已经创建到数据库中
        List<TaskDB> taskList = DataSupport.where("url=?",url).find(TaskDB.class);
        if(taskList.size() ==0){
            long contentLength = 100;

            TaskDB taskDB = new TaskDB();
            taskDB.setUrl(url);
            taskDB.setName(name);
            taskDB.setDownloadedLength(0);
            taskDB.setContentLength(contentLength);
            taskDB.save();

            task.setDownloadedLength(0);
            task.setContentLength(contentLength);
            task.setContentLength(contentLength);
        }else {
            TaskDB taskDB= taskList.get(0);
            task.setDownloadedLength(taskDB.getDownloadedLength());
            task.setContentLength(taskDB.getContentLength());
        }

    }

    @Override
    protected Integer doInBackground(Void... params) {
        mStatus = STATUS_DOWNLOADING;

        //模拟下载
        //获取已下载的进度
        int progress = (int) (task.getDownloadedLength() * 100 / task.getContentLength());
        for (int i = progress; i <= 100; i++) {
            if (mStatus == STATUS_PAUSED) {
                //更新进度到数据库中
                TaskDB taskDB = new TaskDB();
                taskDB.setDownloadedLength(task.getDownloadedLength());
                taskDB.updateAll("url=?",task.getUrl());
                return mStatus;
            }else if(mStatus == STATUS_CANCELED){
                //从数据库中删除本条记录
                DataSupport.deleteAll(TaskDB.class,"url=?",task.getUrl());
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
        MainActivity.updateProgress();
    }

    public void pauseDownload() {
        mStatus = STATUS_PAUSED;
        mListener.DownloadResult(STATUS_PAUSED);
    }
    public void cancelDownload(){
        mStatus = STATUS_CANCELED;
        mListener.DownloadResult(STATUS_CANCELED);
    }

    interface DownloadListener {
        void DownloadResult(int result);
    }


}
