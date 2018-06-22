package com.android.banck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anteneh on 9/2/2016.
 */
public class DataSource {

    static SQLiteOpenHelper dbhelper;
    static SQLiteDatabase database;

    private static final String[] bankColumns={DatabaseOpenHelper.bank_id,DatabaseOpenHelper.bank_name,
    DatabaseOpenHelper.logo,DatabaseOpenHelper.visibility
    };

    private static final String[] branchColumns={DatabaseOpenHelper.branch_id,DatabaseOpenHelper.branch_name,
            DatabaseOpenHelper.atm,DatabaseOpenHelper.latitude,DatabaseOpenHelper.longtiude,DatabaseOpenHelper.logo,DatabaseOpenHelper.tel,
            DatabaseOpenHelper.bank_id
    };

    public DataSource(Context context){
        dbhelper=new DatabaseOpenHelper(context);

    }

    public void open(){
        database=dbhelper.getWritableDatabase();
        Log.i("Database"," open");
    }
    public void close(){
        dbhelper.close();
        Log.i("Database"," Closed");
    }

    public void create_bank(Bank bank){
        ContentValues cv=new ContentValues();
        cv.put(DatabaseOpenHelper.bank_id,bank.getBank_id());
        cv.put(DatabaseOpenHelper.bank_name,bank.getBank_name());
        cv.put(DatabaseOpenHelper.logo,bank.getLogo());
        cv.put(DatabaseOpenHelper.visibility,bank.getVisibility());
        Log.i("inserted data: ",bank.getBank_name());
        database.insert(DatabaseOpenHelper.bank_table,null,cv);
    }

    public void create_branch(Branch org){
        ContentValues cv=new ContentValues();
        cv.put(DatabaseOpenHelper.branch_id,org.getBranch_id());
        cv.put(DatabaseOpenHelper.branch_name,org.getBranch_name());
        cv.put(DatabaseOpenHelper.atm,org.getAtm());
        cv.put(DatabaseOpenHelper.latitude,org.getLatitude());
        cv.put(DatabaseOpenHelper.longtiude,org.getLongtiude());
        cv.put(DatabaseOpenHelper.bank_id,org.getBank_id());
        cv.put(DatabaseOpenHelper.logo,org.getLogo());
        cv.put(DatabaseOpenHelper.tel,org.getTel());
        database.insert(DatabaseOpenHelper.branch_table,null,cv);
        Log.i("inserted data bra: ",org.getBranch_name());
    }

    public ArrayList<Bank> findFilteredBanck(String selection,String[] selectionargs,String groupby,String havingby,String orderby){
        ArrayList<Bank> data=new ArrayList<Bank>();
        Cursor cv=database.query(DatabaseOpenHelper.bank_table,bankColumns,selection,selectionargs,groupby,havingby,orderby);
        Log.i("bbb cv",cv.getCount()+"");
        if (cv.getCount()>0){
            while (cv.moveToNext()){
                Bank org=new Bank();
                org.setBank_id(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.bank_id)));
                org.setBank_name(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.bank_name)));
                org.setLogo(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.logo)));
                org.setVisibility(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.visibility)));
                data.add(org);
            }
        }
        return data;
    }
    public List<Bank> findOrgLogo(String id){
        List<Bank> data=new ArrayList<Bank>();
        Cursor cv=database.query(DatabaseOpenHelper.bank_table,bankColumns,DatabaseOpenHelper.bank_id+" = "+id,null,null,null,null);
        if (cv.getCount()>0){
            while (cv.moveToNext()){
                Bank org=new Bank();
                org.setBank_name(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.bank_name)));
                org.setLogo(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.logo)));
                data.add(org);
            }
        }
        return data;
    }

    public List<Branch> findFilteredBranch(String selection,String[] selectionargs,String groupby,String havingby,String orderby){
        List<Branch> data=new ArrayList<Branch>();
        Cursor cv=database.query(DatabaseOpenHelper.branch_table,branchColumns,selection,selectionargs,groupby,havingby,orderby);
        Log.i("bbb ","u are inserted into branch"+cv.getCount());
        if (cv.getCount()>0){
            while (cv.moveToNext()){
                Branch org=new Branch();
                org.setBranch_id(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.branch_id)));
                org.setBranch_name(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.branch_name)));
                org.setAtm(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.atm)));
                org.setLatitude(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.longtiude)));
                org.setLongtiude(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.latitude)));
                org.setBank_id(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.bank_id)));
                org.setTel(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.tel)));
                org.setLogo(cv.getString(cv.getColumnIndex(DatabaseOpenHelper.logo)));
                data.add(org);
            }
        }
        return data;
    }


}
