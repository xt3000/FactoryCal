package net.finch.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBMarks extends SQLiteOpenHelper
 {
	protected static final String DB_NAME = "marks";

    public DBMarks(Context context) {
		// конструктор суперкласса
		super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
		//Log.d(LOG_TAG, "--- onCreate database ---");
		// создаем таблицу с полями
		db.execSQL("create table "+DB_NAME+" ("
                + "id integer primary key autoincrement,"
                + "date integer,"
                + "month integer,"
                + "year integer,"
                + "time integer,"
                + "note varchar(255)"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    Boolean saveDayMark(int y, int m, int d, int t, String note){
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

     Boolean saveSchedule(){


         return false;
     }

     Boolean deleteDayMark(int y, int m, int d){
         SQLiteDatabase db = getWritableDatabase();
         String select = "year = ? and month = ? and date = ?";
         String[] selArgs = {""+y, ""+m, ""+d};

         db.delete(DBMarks.DB_NAME, select, selArgs);
         db.close();
         return true;
     }
}
