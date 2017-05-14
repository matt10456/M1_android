package fr.unice.visitcardapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class SQLDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Visitcard.db";
    private static final String CONTACTS_TABLE_NAME = "contacts";
    private static final String CONTACTS_COLUMN_ID = "id";
    private static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_1 = "display1";
    public static final String CONTACTS_COLUMN_2 = "display2";
    public static final String CONTACTS_COLUMN_3 = "display3";
    private static final String REQUEST_CREATE = "create table "+CONTACTS_TABLE_NAME+" ("+CONTACTS_COLUMN_ID+" integer primary key, "+
            CONTACTS_COLUMN_NAME+" text, " +""+CONTACTS_COLUMN_1+" text, "+CONTACTS_COLUMN_2+" text, "+CONTACTS_COLUMN_3+" text)";
    private static final String DROP_REQUEST = "DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME;
    private static final String REQUEST_GETDATABYNAME = "select * from contacts where "+CONTACTS_COLUMN_NAME+"=? ORDER BY id DESC";
    private static final String REQUEST_GETDATA ="select * from contacts where id=?";
    private static final String REQUEST_GETLASTCONTACT = "select * from contacts ORDER BY id DESC";

    public SQLDatabase(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REQUEST_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_REQUEST);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_REQUEST);
    }

    public boolean insertContact (String name, String d1, String d2, String d3) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME , name);
        contentValues.put(CONTACTS_COLUMN_1, d1);
        contentValues.put(CONTACTS_COLUMN_2, d2);
        contentValues.put(CONTACTS_COLUMN_3, d3);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getDataByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        if(!name.equals("")) {
            res =  db.rawQuery(REQUEST_GETDATABYNAME, new String[]{name});
        }
       return res;
    }

    Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(REQUEST_GETDATA, new String[]{Integer.toString(id)});
    }

    Cursor getLastContact() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(REQUEST_GETLASTCONTACT, null);
    }
}
