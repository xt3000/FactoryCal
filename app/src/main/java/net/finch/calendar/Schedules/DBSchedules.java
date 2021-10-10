package net.finch.calendar.Schedules;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.finch.calendar.DB;
import net.finch.calendar.Marks.DBMarks;

import static net.finch.calendar.CalendarVM.TAG;

public class DBSchedules extends DB {
    public static final String DB_NAME = "schedule";

    public DBSchedules(Context context) {
        super(context, DB_NAME, null, 1, DB_NAME);
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


    public boolean isPrime(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "id = ?";
        String[] selArgs = {""+id};
        Cursor cur = db.query(DB_NAME, null, select, selArgs, null, null, null);

        boolean isPrime = false;
        if (cur.moveToFirst()) {
            isPrime = cur.getInt(cur.getColumnIndex("prime")) != 0;
            cur.close();
            db.close();
        }

        return isPrime;
    }

    public String readSdlName(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "id = ?";
        String[] selArgs = {""+id};

        Cursor cur = db.query(DB_NAME, null, select, selArgs, null, null, null);
        String name = "";
        if (cur.moveToFirst()) {
            name = cur.getString(cur.getColumnIndex("name"));
            cur.close();
            db.close();
        }
        db.close();
        return name;
    }

    public void save(String name, String sdl, boolean prime){
        SQLiteDatabase db = getWritableDatabase();
        String select = "name = ?";
        String[] selArgs = {name};
        Integer y = null;
        Integer m = null;
        Integer d = null;

        Cursor cur = db.query(DB_NAME, null, select, selArgs, null, null, null);
        if (cur.moveToFirst()) {
            y = cur.getInt(cur.getColumnIndex("year"));
            m = cur.getInt(cur.getColumnIndex("month"));
            d = cur.getInt(cur.getColumnIndex("date"));
            cur.close();
            db.close();
        }

        if (y != null) save(y, m, d, name, sdl, prime);
    }

    public void save(int y, int m, int d, String name, String sdl, boolean prime){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (prime) {
            String sel = "UPDATE schedule SET prime = 0";
            db.execSQL(sel);
        }

        cv.put("year", y);
        cv.put("month", m);
        cv.put("date", d);
        cv.put("name", name);
        cv.put("sdl", sdl);
        cv.put("prime", prime);

        db.replace(DB_NAME, null, cv);
        db.close();
    }

    public void delete(int id){

        SQLiteDatabase db = getWritableDatabase();
        String select = "id = ?";
        String[] selArgs = {""+id};

        db.delete(DB_NAME, select, selArgs);
        db.close();
    }
}
