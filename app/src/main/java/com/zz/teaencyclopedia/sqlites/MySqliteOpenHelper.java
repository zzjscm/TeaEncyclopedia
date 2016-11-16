package com.zz.teaencyclopedia.sqlites;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/11/14.
 */

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    public MySqliteOpenHelper(Context context) {
        super(context, "data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table collection(_id integer primary key autoincrement," +
                "id vachar,title vachar,source vachar,wap_thumb vachar,create_time vachar," +
                "nickname vachar)");

        db.execSQL("create table history(_id integer primary key autoincrement," +
                "id vachar,title vachar,source vachar,wap_thumb vachar,create_time vachar," +
                "nickname vachar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
