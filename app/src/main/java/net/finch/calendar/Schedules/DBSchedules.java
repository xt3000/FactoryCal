package net.finch.calendar.Schedules;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSchedules extends SQLiteOpenHelper {
    public static final String DB_NAME = "schedule";

    public DBSchedules(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями

        // TODO:  *******************************
        db.execSQL("create table "+DB_NAME+" ("
                + "id integer primary key autoincrement,"
                + "date integer NOT NULL,"
                + "month integer NOT NULL,"
                + "year integer NOT NULL,"
                + "name varchar(16) UNIQUE,"
                + "sdl varchar(31) NOT NULL,"
                + "prime boolean NOT NULL"
                + ");");
    }
    

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public Boolean saveSchedule(int y, int m, int d, String name, String schedule, boolean prime){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (prime) {
            String sel = "UPDATE schedule SET prime = 0";
            String[] args = {"FALSE"};
            db.execSQL(sel);
        }

        cv.put("year", y);
        cv.put("month", m);
        cv.put("date", d);
        cv.put("name", name);
        cv.put("sdl", schedule);
        cv.put("prime", prime);

        db.replace(DB_NAME, null, cv);
        db.close();
        return true;
    }
}
