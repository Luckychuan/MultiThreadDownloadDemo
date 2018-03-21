package com.example.luckychuan.downloaddemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luckychuan on 2018/3/16.
 */

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";
    private TaskSQLiteHelper mHelper;
    private static DatabaseManager mDatabaseManager;
    
    private DatabaseManager(Context context) {
        mHelper = new TaskSQLiteHelper(context, "Task.db", 1);
    }

    public static DatabaseManager getInstance(Context context) {
        if (mDatabaseManager == null) {
            mDatabaseManager = new DatabaseManager(context);
        }
        return mDatabaseManager;
    }

    public List<Task> query() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<Task> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from task", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int progress = cursor.getInt(cursor.getColumnIndex("progress"));
                Task task = new Task(url, name, progress);
                Log.d(TAG, "query: "+task.toString());
                list.add(task);
            }
            cursor.close();
        }
        db.close();

        Log.d(TAG, "query: list.size = "+ list.size());

        return list;
    }

    public void delete(String url) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("delete from task where url = ?", new String[]{url});
        db.close();
        Log.d(TAG, "delete: ");
    }

    public void insert(Task task) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("insert into task(url,name,progress) values(?,?,?)",
                new String[]{task.getUrl(), task.getName(), task.getProgress() + ""});
        db.close();
        Log.d(TAG, "insert: ");
    }

    public void update(String url, int progress) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("update  task set progress = ? where url=?", new String[]{url, progress + ""});
        db.close();
        Log.d(TAG, "update: ");
    }

    public Task query(String url) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from task where url =?", new String[]{url});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int progress = cursor.getInt(cursor.getColumnIndex("progress"));
                return new Task(url, name, progress);
            }
            cursor.close();
        }

        db.close();
        Log.d(TAG, "query: ");
        return null;
    }


}
