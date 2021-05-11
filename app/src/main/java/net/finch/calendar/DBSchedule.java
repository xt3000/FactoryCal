package net.finch.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSchedule extends SQLiteOpenHelper {
    protected static final String DB_NAME = "schedule";

    public DBSchedule(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями

        // TODO:  *******************************
        db.execSQL("create table "+DB_NAME+" ("
                + "id integer primary key autoincrement,"
                + "date integer,"
                + "month integer,"
                + "year integer,"
                + "name varchar(16),"
                + "schedule varchar(31)"
                + ");");
    }
    

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    Boolean saveSchedule(int y, int m, int d, String name, String schedule){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("year", y);
        cv.put("month", m);
        cv.put("date", d);
        cv.put("time", name);
        cv.put("note", schedule);

        db.insert(DB_NAME, null, cv);
        db.close();
        return true;
    }
}
