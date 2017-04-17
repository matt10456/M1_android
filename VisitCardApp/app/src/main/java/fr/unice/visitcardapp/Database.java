package fr.unice.visitcardapp;

import java.util.ArrayList;
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
    static final String CONTACTS_COLUMN_SURNAME = "surname";
    static final String CONTACTS_COLUMN_JOB = "job";
    static final String CONTACTS_COLUMN_PHONE = "phone";
    static final String CONTACTS_COLUMN_MAIL = "email";
    static final String CONTACTS_COLUMN_WEBSITE = "website";
    private HashMap hp;

    Database(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table contacts " +
                "(id integer primary key, name text,surname text,job text, phone text, email text, website text)");
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

    boolean insertContact (String name, String surname, String job, String phone,String email, String website) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        contentValues.put("job", job);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("website", website);
        db.insert("contacts", null, contentValues);
        return true;
    }

    Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id, null );
        return res;
    }

    Cursor getDataByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where "+CONTACTS_COLUMN_NAME+"=?", new String[]{name});
        return res;
    }

    Cursor getLastContact() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts ORDER BY id DESC", null );
        return res;
    }
}
