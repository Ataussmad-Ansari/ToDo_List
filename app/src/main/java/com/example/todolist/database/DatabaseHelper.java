package com.example.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.todolist.TaskModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2;

    // Table name and columns
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STATUS = "status";

    // SQL statement to create the tasks table
    private static final String SQL_CREATE_TABLE_TASKS =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_STATUS + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if needed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        // Recreate tables
        onCreate(db);
    }

    /* public long insertTask(String name, String time, String date) {
         SQLiteDatabase db = this.getWritableDatabase();
         ContentValues values = new ContentValues();
         values.put(COLUMN_NAME, name);
         values.put(COLUMN_TIME, time);
         values.put(COLUMN_DATE, date);
         db.insert(TABLE_TASKS, null, values);
         db.close();
         return 0;
     }*/
    public long insertTask(String name, String time, String date, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_STATUS, status ? 1 : 0); // Convert boolean to integer
        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    public Cursor getInfo() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from tasks", null);
        return cursor;
    }

    public Cursor getTasksForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tasks WHERE date = ?", new String[]{date});
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();
    }
    public TaskModel getTask(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_TIME, COLUMN_DATE, COLUMN_STATUS}, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)}, null, null, null, null);
        if (cursor!= null) {
            cursor.moveToFirst();
        }
        TaskModel taskModel = new TaskModel(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Boolean.parseBoolean(cursor.getString(4)));
        cursor.close();
        return taskModel;
    }
    public void updateTask(int taskId, String name, String time, String date, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_STATUS, status? 1 : 0);
        db.update(TABLE_TASKS, values, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();
    }
    public void updateStatus(int taskId, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status ? 1 : 0);
        db.update(TABLE_TASKS, values, COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
    }
}

