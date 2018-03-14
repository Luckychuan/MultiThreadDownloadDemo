package com.example.luckychuan.downloaddemo;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Integer 下载的进度
 * @Integer 下载状态的提示
 */
public class DownloadAsyncTask extends AsyncTask<Void, Integer, Integer> {

    private static final String TAG = "DownloadAsyncTask";

    private int mStatus;
    private static final int STATUS_DOWNLOADING = 0;
    private static final int STATUS_SUCCEED = 1;
    private static final int STATUS_PAUSED = 2;
    private static final int STATUS_CANCELED = 3;
    private static final int STATUS_FAILED = 4;


    private String mUrl;
    private long mDownloadedLength;
    private long mContentLength;

    private DownLoadListener mListener;

    public DownloadAsyncTask(String url,DownLoadListener listener) {
        mListener = listener;

        task = t;
        String url = task.getUrl();
        String name = url.substring(url.lastIndexOf("/") + 1);
        task.setName(name);



    }

    public String geturl() {
        return mUrl;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        mStatus = STATUS_DOWNLOADING;

        //初始化文件
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File file = new File(directory + task.getName());
        long contentLength = getContentLength(task.getUrl());
        if (file.exists()) {
            task.setDownloadedLength(file.length());
        }else{

        }

        if (contentLength == 0) {
            mStatus = STATUS_FAILED;
            return STATUS_FAILED;
        }
        task.setContentLength(contentLength);
        TaskDB tDB = new TaskDB();
        tDB.setContentLength(contentLength);
        tDB.updateAll("url = ?",task.getUrl());
        int progress;

        //发送http请求下载
        InputStream is = null;
        RandomAccessFile saveFile = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().
                addHeader("RANGE", "bytes=" + task.getDownloadedLength() + "-") //指定从哪一个字节下载
                .url(task.getUrl()).build();
        try {
            Response response = client.newCall(request).execute();
            //写入到本地
           if(response!=null){
               Log.d(TAG, "doInBackground: response not null");
               is = response.body().byteStream();
               saveFile = new RandomAccessFile(file, "rw");
               saveFile.seek(task.getDownloadedLength());
               int len;
               byte[] buffer = new byte[1024];
               while ((len = is.read(buffer)) != -1l) {
                   if (mStatus == STATUS_PAUSED) {
                       //更新进度到数据库中
                       TaskDB taskDB = new TaskDB();
                       taskDB.setDownloadedLength(task.getDownloadedLength());
                       taskDB.updateAll("url=?", task.getUrl());
                       return STATUS_PAUSED;
                   } else if (mStatus == STATUS_CANCELED) {
                       //从数据库中删除本条记录
                       DataSupport.deleteAll(TaskDB.class, "url=?", task.getUrl());
                       if (file != null) {
                           file.delete();
                       }
                       return STATUS_CANCELED;
                   }
                   //获取已下载的进度
                   saveFile.write(buffer, 0, len);
                   task.setDownloadedLength(task.getDownloadedLength() + len);
                   progress = (int) (task.getDownloadedLength() * 100 / task.getContentLength());
                   publishProgress(progress);
               }

               response.body().close();
           }else{
               Log.d(TAG, "doInBackground:  response null");
           }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (saveFile != null) {
                    saveFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        if(task.getDownloadedLength()>=task.getContentLength()){
            mStatus = STATUS_SUCCEED;
            //更新进度到数据库中
            TaskDB taskDB = new TaskDB();
            taskDB.setDownloadedLength(task.getDownloadedLength());
            taskDB.updateAll("url=?", task.getUrl());
        }
        return mStatus;
    }

    private long getContentLength(String url) {
        long contentLength = 0;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.isSuccessful()) {
            contentLength = response.body().contentLength();
            response.close();
        }else{
            Log.d(TAG, "getContentLength: response null");
        }
        return contentLength;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.DownloadResult(integer);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        MainActivity.updateProgress(task.getUrl());
    }

    public void setPause() {
        mStatus = STATUS_PAUSED;
    }

    public void setCancel() {
        mStatus = STATUS_CANCELED;
    }


    interface DownLoadListener{
        void onInitFinish(String name,long contentLength);
        void onDownloadStart();
        void onDownloadPause();
        void updateProgress(long downloadedLength);
        void onFail();
        void onCancel(String name);
    }

}
