package net.finch.calendar.settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSettings extends SQLiteOpenHelper {
    public static final String DB_NAME = "settings";

    public DBSettings(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями
        db.execSQL("create table "+DB_NAME+" ("
                + "id integer primary key autoincrement,"
                + "date integer NOT NULL,"
                + "month integer NOT NULL,"
                + "year integer NOT NULL,"
                + "time integer,"
                + "note varchar(255)"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void setOption(String option, Object arg) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (arg.getClass() == Integer.class) cv.put(option, (Integer)arg);
        if (arg.getClass() == String.class) cv.put(option, (String) arg);

        db.replace(DB_NAME, null, cv);
        db.close();
    }


// TODO:  удалить метод saveDayMark()!!
    public Boolean saveDayMark(int y, int m, int d, int t, String note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("year", y);
        cv.put("month", m);
        cv.put("date", d);
        cv.put("time", t);
        cv.put("note", note);

        db.insert(DB_NAME, null, cv);
        db.close();
        return true;
    }
}
