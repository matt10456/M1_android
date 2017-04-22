package fr.unice.visitcardapp;

import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Visitcard.db";
    static final String CONTACTS_TABLE_NAME = "contacts";
    static final String CONTACTS_COLUMN_ID = "id";
    static final String CONTACTS_COLUMN_NAME = "name";
    static final String CONTACTS_COLUMN_1 = "display1";
    static final String CONTACTS_COLUMN_2 = "display2";
    private HashMap hp;

    Database(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table contacts " +
                "(id integer primary key, name text, display1 text, display2 text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS contacts");
    }

    boolean insertContact (String name, String d1, String d2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME , name);
        contentValues.put(CONTACTS_COLUMN_1, d1);
        contentValues.put(CONTACTS_COLUMN_2, d2);
        db.insert("contacts", null, contentValues);
        return true;
    }

    Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from contacts where id=?", new String[]{Integer.toString(id)});
    }

    Cursor getDataByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        if(!name.equals("")) {
            res =  db.rawQuery("select * from contacts where "+CONTACTS_COLUMN_NAME+"=?", new String[]{name});
        }
       return res;
    }

    Cursor getLastContact() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from contacts ORDER BY id DESC", null);    }
}
