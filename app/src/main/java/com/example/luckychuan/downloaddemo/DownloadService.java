package com.example.luckychuan.downloaddemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DownloadService extends Service implements DownloadView {

    private static final String TAG = "DownloadService";
    private DownloadManager mDownloadManager;
    private static ArrayList<Task> mTasks;
    private OnTaskDataChangeListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        if (mDownloadManager == null) {
            Log.d(TAG, "onCreate: init mDownloadManager");
            mDownloadManager = new DownloadManager(this);
        }

        //后台下载
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText("下载");
        builder.setContentTitle("下载");
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(0, notification);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, notification);

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mListener = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDownloadStart(String url) {
        int position = getPosition(url);
        Task task = mTasks.get(position);
        task.setDownloading(true);

        if (mListener != null) {
            mListener.onDataChange(position);
        }
    }

    @Override
    public void onDownloadPause(String url) {
        int position = getPosition(url);
        Task task = mTasks.get(position);
        task.setDownloading(false);

        if (mListener != null) {
            mListener.onDataChange(position);
        }
    }

    @Override
    public void updateProgress(String url, int progress) {
        int position = getPosition(url);
        Task task = mTasks.get(position);
        task.setProgress(progress);

        if (mListener != null) {
            mListener.onDataChange(position);
        }

        Log.d(TAG, "updateProgress: " + progress);
    }

    @Override
    public void onFail(String url) {
        int position = getPosition(url);
        Task task = mTasks.get(position);
        task.setProgress(-1);
        task.setDownloading(false);

        if (mListener != null) {
            mListener.onDataChange(position);
        }
    }

    @Override
    public void onCancel(String url) {
        int position = getPosition(url);
        mTasks.remove(position);

        if (mListener != null) {
            mListener.onDataRemove(position);
        }
    }

    /**
     * 判断是否所有任务已完成，关闭service
     */
    @Override
    public void onFinish() {
        Log.d(TAG, "onFinish: ");
        for (Task task : mTasks) {
            if (task.getProgress() < 100) {
                return;
            }
        }
        //所有任务已完成
        stopForeground(true);
        stopSelf();
        Log.d(TAG, "onFinish: stop service");
    }

    /**
     * 通过url找到当前task在list的position
     *
     * @param url
     * @return
     */
    private int getPosition(String url) {
        for (int i = 0; i < mTasks.size(); i++) {
            Task task = mTasks.get(i);
            if (url.equals(task.getUrl())) {
                return i;
            }
        }
        return -1;
    }


    class DownloadBinder extends Binder {

        public void setListener(OnTaskDataChangeListener listener) {
            mListener = listener;

            if (mTasks == null) {
                mTasks = new ArrayList<>();
                mTasks.addAll(mDataBaseManager.query());
            }
            mListener.onInitFinish(mTasks);

        }

        public void startDownload(String url) {
            mDownloadManager.addDownloadTask(url);
        }

        public void pauseDownload(String url) {
            mDownloadManager.pauseDownload(url);
        }

        public void cancelDownload(String url) {
            mDownloadManager.cancelDownload(url);
        }

        public void newTask(String url) {
            //判断任务是否已经存在
            for (Task t : mTasks) {
                if (t.getUrl().equals(url)) {
                    Toast.makeText(getApplicationContext(), "任务已经存在", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String name = url.substring(url.lastIndexOf("/") + 1);
            Task task = new Task(url, name);
            mTasks.add(task);

            if (mListener != null) {
                mListener.onDataInsert(mTasks.size() - 1);
            }

            startDownload(url);
        }
    }

    interface OnTaskDataChangeListener {
        void onInitFinish(List<Task> list);

        void onDataInsert(int position);

        void onDataChange(int position);

        void onDataRemove(int position);
    }

}
