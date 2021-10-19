package net.finch.calendar.Marks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.finch.calendar.DB;
import net.finch.calendar.Time;

public class DBMarks extends DB
 {
	public static final String DB_NAME = "marks";

    public DBMarks(Context context) {
		// конструктор суперкласса
		super(context, DB_NAME, null, 1, DB_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
		//Log.d(LOG_TAG, "--- onCreate database ---");
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void save(int y, int m, int d, int t, String note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("year", y);
        cv.put("month", m);
        cv.put("date", d);
        cv.put("time", t);
        cv.put("note", note);

        db.insert(DB_NAME, null, cv);
        db.close();
    }

    public boolean update(Mark mrk) {
        SQLiteDatabase db = getReadableDatabase();
        int sqlId = mrk.getDB_id();
        String select = "id = ?";
        String[] selArgs = {String.valueOf(sqlId)};

        Cursor cur = db.query(DB_NAME, null, select, selArgs, null, null, null);
        Integer d =null, m = null, y = null;
        if (cur.moveToFirst()) {
            d = cur.getInt(cur.getColumnIndex("date"));
            m = cur.getInt(cur.getColumnIndex("month"));
            y = cur.getInt(cur.getColumnIndex("year"));
        }
        cur.close();

        if (y != null) {
            ContentValues cv = new ContentValues();
            cv.put("year", y);
            cv.put("month", m);
            cv.put("date", d);
            cv.put("time", Time.toInt(mrk.getTime()));
            cv.put("note", mrk.getInfo());
            db.update(DB_NAME, cv, select, selArgs);
        }else return false;
        db.close();

        return true;
    }

    public Mark readMark(int sqlId) {
        SQLiteDatabase db = getReadableDatabase();
        String select = "id = ?";
        String[] selArgs = {String.valueOf(sqlId)};

        Cursor cur = db.query(DB_NAME, null, select, selArgs, null, null, null);
        Mark mrk = new Mark(sqlId, "", "");
        if (cur.moveToFirst()) {
            mrk.setTime(Time.toStr(cur.getInt(cur.getColumnIndex("time"))));
            mrk.setInfo(cur.getString(cur.getColumnIndex("note")));
        }
        cur.close();
        db.close();

        return mrk;
    }

//     public void delete(int id){
//         SQLiteDatabase db = getWritableDatabase();
//         String select = "id = ?";
//         String[] selArgs = {""+id};
//
//         db.delete(DB_NAME, select, selArgs);
//         db.close();
//     }
}
