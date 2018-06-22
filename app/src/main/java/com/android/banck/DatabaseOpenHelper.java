package com.android.banck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by anteneh on 9/2/2016.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper{
    public static String DatabaseName="cash";

    public static String bank_table="bank";
    public static String bank_id="bank_id";
    public static String bank_name="bank_name";
    public static String logo="logo";
    public static String visibility="visibility";

    public static String branch_table="branch";
    public static String branch_id="branch_id";
    public static String branch_name="branch_name";
    public static String atm="atm";
    public static String latitude="lat";
    public static String longtiude="lng";
    public static String tel="tel";


    String banck="CREATE TABLE IF NOT EXISTS "+bank_table+"(" +
            "" +bank_id    +" INTEGER PRIMARY KEY ,"+
            "" +bank_name  +" TEXT,"+
            "" +logo       +" TEXT,"+
            ""+visibility+" TEXT DEFAULT unchecked"+
            ")";

    String branch="CREATE TABLE IF NOT EXISTS "+branch_table+"(" +
            "" +branch_id    +" INTEGER PRIMARY KEY ,"+
            "" +branch_name  +" TEXT,"+
            "" +atm       +" TEXT,"+
            "" +latitude       +" TEXT,"+
            ""+longtiude+" TEXT ,"+
            ""+logo+" TEXT,"+
            ""+tel+" TEXT,"+
            ""+bank_id+" TEXT "+
            ")";


    public DatabaseOpenHelper(Context context){
        super(context,DatabaseName,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(banck);
        Log.i("bbb Database created: ","banck");
        db.execSQL(branch);
        Log.i("bbb Database Created: ","branch");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+bank_table);
        db.execSQL("DROP TABLE IF EXISTS "+branch_table);
    }
}
