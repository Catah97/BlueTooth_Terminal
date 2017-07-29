package com.example.martin.bluetooth_terminal.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.martin.bluetooth_terminal.Other.Konstanty;

/**
 * Created by Martin on 01.04.2016.
 */
public class DatabaPairedDevices extends SQLiteOpenHelper {

    /**Slouží k přejmenování
     * spárovaných zařízeních
     */


    String TABLE_CREATOR = "("+Konstanty.ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+Konstanty.DEVICE_MAC+" STRING,"+Konstanty.DEVICE_NAME + " STRING);";

    public DatabaPairedDevices(Context context) {
        super(context, Konstanty.PAIRED_DEVICES_DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+Konstanty.PAIRED_DEVICES+TABLE_CREATOR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void PutData(String mac,String name){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.DEVICE_MAC,mac);
        contentValues.put(Konstanty.DEVICE_NAME,name);
        sqLiteDatabase.insert(Konstanty.PAIRED_DEVICES, null, contentValues);
        Log.e("data vlozana", contentValues.toString());
    }
    public Cursor GetData(){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cr =database.rawQuery("select * from " + Konstanty.PAIRED_DEVICES, null);
        return cr;
    }
    public Cursor GetID_Data(String mac){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cr =database.rawQuery("select * from " +Konstanty.PAIRED_DEVICES + " WHERE " + Konstanty.DEVICE_MAC+ " = ?",new String[]{mac});
        return cr;
    }


    public void Updata(String id,String name){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.DEVICE_NAME,name);
        databaOperations.update(Konstanty.PAIRED_DEVICES,contentValues, Konstanty.DEVICE_MAC+" = ?",new String[]{ id  });
    }
    public void DeleteItem(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(Konstanty.PAIRED_DEVICES, Konstanty.DEVICE_MAC+" = ?",new String[]{ id  });
    }
    public void CloseDatabase(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.close();
    }
}
