package com.example.luckychuan.downloaddemo;

import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Luckychuan on 2017/3/24.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";

    private ArrayList<Task> mTasks;
    private OnItemButtonClickListener mListener;

    public RecyclerAdapter(ArrayList<Task> tasks, OnItemButtonClickListener listener) {
        mTasks = tasks;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder((LayoutInflater.from(parent.getContext())).inflate(R.layout.recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = mTasks.get(position);

        //初始化
        holder.fileName.setText(task.getName());
        holder.progressBar.setProgress(0);

        //更新进度
        if (task.getProgress() != -1) {
            holder.progressBar.setProgress(task.getProgress());
            holder.progressText.setText("已下载：" + task.getProgress() + "%");
            holder.startButton.setVisibility(View.VISIBLE);
        } else {
            //当下载失败时
            holder.progressBar.setProgress(0);
            holder.progressText.setText("下载失败");
            holder.startButton.setVisibility(View.GONE);
        }

        //更新下载状态
        if (task.isDownloading()) {
            holder.startButton.setText("暂停");
        } else {
            holder.startButton.setText("开始");
        }

        //下载完成时
        if (task.getProgress() >= 100) {
            holder.startButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
            holder.openButton.setVisibility(View.VISIBLE);
        } else {
            holder.startButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.openButton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Button startButton;
        private TextView fileName;
        private ProgressBar progressBar;
        private TextView progressText;
        private Button cancelButton;
        private Button openButton;

        ViewHolder(View itemView) {
            super(itemView);
            cancelButton = ((Button) itemView.findViewById(R.id.cancel_btn));
            startButton = (Button) itemView.findViewById(R.id.start_btn);
            openButton = (Button) itemView.findViewById(R.id.open_btn);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            progressBar.setMax(100);
            progressText = (TextView) itemView.findViewById(R.id.progress_text);
            cancelButton.setOnClickListener(this);
            startButton.setOnClickListener(this);
            openButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Task task = mTasks.get(getLayoutPosition());
            switch (v.getId()) {
                case R.id.start_btn:
                    if (task.isDownloading()) {
                        startButton.setText("开始");
                        mListener.onStartButtonClick(task.getUrl(), false);
                    } else {
                        startButton.setText("暂停");
                        mListener.onStartButtonClick(task.getUrl(), true);
                    }
                    break;

                case R.id.cancel_btn:
                    mListener.onCancelButtonClick(task.getUrl());
                    break;

                case R.id.open_btn:
                    String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File file = new File(directory + task.getName());
                    if (!file.exists()) {

                    }
                    break;

            }
        }
    }

    interface OnItemButtonClickListener {
        void onStartButtonClick(String url, boolean toStartDownload);

        void onCancelButtonClick(String url);
    }


}
