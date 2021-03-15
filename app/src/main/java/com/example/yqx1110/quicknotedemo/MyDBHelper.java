package com.example.yqx1110.quicknotedemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.util.HashMap;

public class MyDBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase readableDatabase;
    private Context mContext;

    MyDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    /*这个方法插入了一条默认记录*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table notes(" +
                " _id integer primary key autoincrement, " +
                " date TEXT, " +
                " title TEXT, " +
                " content TEXT " +
                ");";
        db.execSQL(sql);
        db.execSQL("insert into notes('date', 'title', 'content') " +
                "values('12月1日', '欢迎使用Quick Note', '这是一个示例');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    /*这个方法接收_id, 并返回包含数据库中对应数据的HashMap*/
     HashMap<String, Object> queryByID(int _id) {
        readableDatabase= this.getReadableDatabase();
        String query = "select * from notes where _id = ?;";
        Cursor cursor = readableDatabase.rawQuery(query, new String[]{String.valueOf(_id)});
        cursor.moveToFirst();
        HashMap<String, Object> map = new HashMap<>();
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String date = cursor.getString(cursor.getColumnIndex("date"));
        String content = cursor.getString(cursor.getColumnIndex("content"));
        map.put("_id", _id);
        map.put("title", title);
        map.put("date", date);
        map.put("content", content);
        cursor.close();
        return map;
    }

    /*这个方法接收_id, 并删除数据库中对应的记录*/
    void deleteByID(int _id) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        writableDatabase.delete("notes", "_id == ?",
                new String[]{String.valueOf(_id)});
        Toast.makeText(mContext, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
    }
}
