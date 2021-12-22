package net.finch.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public abstract class DB extends SQLiteOpenHelper {
    public final String DB_NAME;

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, String db_name) {
        super(context, name, factory, version);
        this.DB_NAME = db_name;
    }

    @Override
    public abstract void onCreate(SQLiteDatabase db);

    @Override
    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public void delete(int id){
        SQLiteDatabase db = getWritableDatabase();
        String select = "id = ?";
        String[] selArgs = {""+id};

        db.delete(DB_NAME, select, selArgs);
        db.close();
    }
}
