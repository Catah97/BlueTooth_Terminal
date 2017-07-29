package com.example.martin.bluetooth_terminal.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.martin.bluetooth_terminal.Other.Konstanty;


/**
 * Created by Martin on 1. 9. 2015.
 * Tato databázová třída je určená k ukládání informací o
 * jednotvých položkách
 */
public class DatabaOperations extends SQLiteOpenHelper {

    /**
     * Na pozici 0 je id položky
     * Na pozici 1 je informace o jaky druh itemu se jedna
     * na pozici 2 je X souradnice
     * na pozici 3 je y souradnice
     * na pozici 4 je viditelnost
     * na pozici 5 je hodnota která se odesílá u tlačitka
     *             je hodnota pro rovný smer volant
     *             je nulova pozice u Plynu
     *             je max hbodnota u posuvniku
     *
     * na pozici 6 je text u tlacitko
     *             je barva hranic posuvniku
     * na pozici 7 je barva pro text u tlacitko
     *             je barva posunu u posuvniku
     * na pozici 8 je barva pro pozadi
     *
     * na pozici 9 je hodnota pro zatačeni VLEVO u volantu (první stupen)
     *              je hodnota pro 1. stupně u posuvníku
     * na pozici 10 je hodnota pro zatačení VLEVO u volantu (druhy stupen)
     *              je hodnota pro 2. stupně u posuvníku
     * na pozici 11 je hodnota pro zataceni VLEVO u volantu (treti stupen)
     *              je hodnota pro 3. stupně u posuvníku
     * na pozici 12 je hodnota pro zatečení VPRAVO u volantu (prvni stupen)
     *              je hodnota pro 4. stupně u posuvníku
     * na pozici 13 je hodnota pro zatačeni VPRAVO u volantu (druhy stupen)
     *              je hodnota pro 5. stupně u posuvníku
     * na pozici 14 je hodnota pro zatačení VPRAVO u volantu (treti stupen)
     *
     * na pozici 15 je hodnota určující jeslti se mají data posílat furt nebo jenom pri zmene
     * pokud jenom při zmene je nastavená na false pokud porad pak je na true
     *
     * na pozici 16 je hordnota určující jeslti se bude object vracet do výchozí polohy
     * pokud true potom se vrací pokud false potom se nevrací
     */


    public final String TABLE_CREATOR= "("+Konstanty.ID+" STRING PRIMARY KEY ,"+
            Konstanty.ItemType+" STRING NOT NULL ,"+
            Konstanty.X+" FLOAT,"+ Konstanty.Y+" FLOAT ," +Konstanty.VISIBLE+" STRING NOT NULL ,"+
            Konstanty.VALUES+" STRING ,"+Konstanty.TEXT_HRANICEcolor +" STRING ,"+Konstanty.TEXT_COLOR_POSUNcolor +" STRING , "+
            Konstanty.BACKGROUND_COLOR+" STRING , "+Konstanty.VALUES9+" STRING , "+Konstanty.VALUES10+" STRING , "+
            Konstanty.VALUES11+" STRING,"+Konstanty.VALUES12 +" STRING ,"+Konstanty.VALUES13 +" STRING , "+
            Konstanty.VALUES14 +" STRING,"+Konstanty.BLUE_TOOTH_SENDING_STILL +" STRING , "+Konstanty.BACK_ANIMATION+" STRING);";

    public DatabaOperations(Context context){
        super(context, Konstanty.MAIN_DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void CreateTable() throws Exception{
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("CREATE TABLE "+ Konstanty.TABLE_NAME+TABLE_CREATOR);
    }
    public void DeleleTable(String table){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE " + table + ";");
    }

    public void PutData(String id,String itemType,String visible){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,id);
        contentValues.put(Konstanty.ItemType,itemType);
        contentValues.put(Konstanty.VISIBLE,visible);
        sqLiteDatabase.insert(Konstanty.TABLE_NAME, null, contentValues);
        Log.e("data vlozana", contentValues.toString());
    }
    public Cursor GetData(){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cr =database.rawQuery("select * from " + Konstanty.TABLE_NAME, null);
        return cr;
    }
    public Cursor GetID_Data(String id){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cr =database.rawQuery("select * from " + Konstanty.TABLE_NAME + " WHERE " + Konstanty.ID+ " =  "+id, null);
        return cr;
    }


    public void Updata(String id,float x, float y,String visible){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,id);
        if (x == Konstanty.NULL)
            contentValues.put(Konstanty.X, (byte[]) null);
        else if (x != Integer.parseInt(Konstanty.NEZMENENO))
            contentValues.put(Konstanty.X,x);
        if (y == Konstanty.NULL)
            contentValues.put(Konstanty.Y, (byte[]) null);
        else if (y !=Integer.parseInt(Konstanty.NEZMENENO))
            contentValues.put(Konstanty.Y,y);
        contentValues.put(Konstanty.VISIBLE, visible);
        databaOperations.update(Konstanty.TABLE_NAME,contentValues, Konstanty.ID+" = ?",new String[]{ id });
    }
    /*public void UpdateAll(String[] data,float x,float y){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,data[0]);
        if (x == Konstanty.NULL)
            contentValues.put(Konstanty.X,(byte[]) null);
        else
            contentValues.put(Konstanty.X,x);
        if (y == Konstanty.NULL)
            contentValues.put(Konstanty.Y,(byte[]) null);
        else
            contentValues.put(Konstanty.Y,y);
        contentValues.put(Konstanty.VISIBLE, data[4]);
        contentValues.put(Konstanty.VALUES,data[5]);
        contentValues.put(Konstanty.TEXT_HRANICEcolor,data[6]);
        contentValues.put(Konstanty.TEXT_COLOR_POSUNcolor,data[7]);
        contentValues.put(Konstanty.BACKGROUND_COLOR,data[8]);
        contentValues.put(Konstanty.VALUES9,data[9]);
        contentValues.put(Konstanty.VALUES10,data[10]);
        contentValues.put(Konstanty.VALUES11,data[11]);
        contentValues.put(Konstanty.VALUES12,data[12]);
        contentValues.put(Konstanty.VALUES13,data[13]);
        contentValues.put(Konstanty.VALUES14,data[14]);
        databaOperations.update(Konstanty.TABLE_NAME,contentValues, Konstanty.ID+" = ?",new String[]{ data[0] });
    }*/
    /**
     * Na pozici 0 je id položky
     * Na pozici 1 je informace o jaky druh itemu se jedna
     * na pozici 2 je X souradnice
     * na pozici 3 je y souradnice
     * na pozici 4 je viditelnost
     * na pozici 5 je hodnota která se odesílá u tlačitka
     *             je hodnota pro rovný smer volant
     *             je nulova pozice u Plynu
     *             je max hbodnota u posuvniku
     *
     * na pozici 6 je text u tlacitko
     *             je barva hranic posuvniku
     * na pozici 7 je barva pro text u tlacitko
     *             je barva posunu u posuvniku
     * na pozici 8 je barva pro pozadi
     *
     * na pozici 9 je hodnota pro zatačeni VLEVO u volantu (první stupen)
     *              je hodnota pro 1. stupně u posuvníku
     * na pozici 10 je hodnota pro zatačení VLEVO u volantu (druhy stupen)
     *              je hodnota pro 2. stupně u posuvníku
     * na pozici 11 je hodnota pro zataceni VLEVO u volantu (treti stupen)
     *              je hodnota pro 3. stupně u posuvníku
     * na pozici 12 je hodnota pro zatečení VPRAVO u volantu (prvni stupen)
     *              je hodnota pro 4. stupně u posuvníku
     * na pozici 13 je hodnota pro zatačeni VPRAVO u volantu (druhy stupen)
     *              je hodnota pro 5. stupně u posuvníku
     * na pozici 14 je hodnota pro zatačení VPRAVO u volantu (treti stupen)
     *
     * na pozici 15 je hodnota určující jeslti se mají data posílat furt nebo jenom pri zmene
     * pokud jenom při zmene je nastavená na false pokud porad pak je na true
     *
     * na pozici 16 je hordnota určující jeslti se bude object vracet do výchozí polohy
     * pokud true potom se vrací pokud false potom se nevrací
     */
    public void UpdatePoznamky (String id,String text,String colorText,String colorBackground,String textSize){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,id);
        if (text == null)
            contentValues.put(Konstanty.TEXT_HRANICEcolor,(byte[]) null);
        else
            contentValues.put(Konstanty.TEXT_HRANICEcolor,text);
        if (colorText == null)
            contentValues.put(Konstanty.TEXT_COLOR_POSUNcolor,(byte[]) null);
        else
            contentValues.put(Konstanty.TEXT_COLOR_POSUNcolor,colorText);
        if (colorBackground == null)
            contentValues.put(Konstanty.BACKGROUND_COLOR,(byte[]) null);
        else
            contentValues.put(Konstanty.BACKGROUND_COLOR,colorBackground);
        if (textSize == null)
            contentValues.put(Konstanty.VALUES9,(byte[])null);
        else
            contentValues.put(Konstanty.VALUES9,textSize);
        databaOperations.update(Konstanty.TABLE_NAME,contentValues, Konstanty.ID+" = ?",new String[]{ id });
    }

    public void UpdateButton(String id,String blueTooth,String text,String colorText,String colorBackground){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,id);
        if (blueTooth == null)
            contentValues.put(Konstanty.VALUES,(byte[]) null);
        else if (!blueTooth.equals(Konstanty.NEZMENENO))
            contentValues.put(Konstanty.VALUES,blueTooth);
        if (text == null)
            contentValues.put(Konstanty.TEXT_HRANICEcolor,(byte[]) null);
        else
            contentValues.put(Konstanty.TEXT_HRANICEcolor,text);
        if (colorText == null)
            contentValues.put(Konstanty.TEXT_COLOR_POSUNcolor,(byte[]) null);
        else
            contentValues.put(Konstanty.TEXT_COLOR_POSUNcolor,colorText);
        if (colorBackground == null)
            contentValues.put(Konstanty.BACKGROUND_COLOR,(byte[]) null);
        else
            contentValues.put(Konstanty.BACKGROUND_COLOR,colorBackground);
        databaOperations.update(Konstanty.TABLE_NAME,contentValues, Konstanty.ID+" = ?",new String[]{ id });
    }

    public void UpdateSwitch(String id,String on,String off){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,id);
        if (on == null)
            contentValues.put(Konstanty.VALUES,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES,on);
        if (off == null)
            contentValues.put(Konstanty.VALUES9,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES9,off);
        databaOperations.update(Konstanty.TABLE_NAME,contentValues, Konstanty.ID+" = ?",new String[]{ id });
    }

    public void UpdateVolant_BlueTooth(String id,String rovne,String prava1,String prava2,String prava3,
                                       String leva1,String leva2,String leva3,String bluetoothSending,String animback){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,id);
        if (rovne == null)
            contentValues.put(Konstanty.VALUES,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES,rovne);

        /**PRAVA*/
        if (prava1 == null)
            contentValues.put(Konstanty.VALUES12,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES12,prava1);
        if (prava2 == null)
            contentValues.put(Konstanty.VALUES13,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES13,prava2);
        if (prava3 == null)
            contentValues.put(Konstanty.VALUES14,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES14,prava3);

        /**LEVA*/
        if (leva1 == null)
            contentValues.put(Konstanty.VALUES9,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES9,leva1);
        if (leva2 == null)
            contentValues.put(Konstanty.VALUES10,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES10,leva2);
        if (leva3 == null)
            contentValues.put(Konstanty.VALUES11,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES11,leva3);

        /**styl odesílání bajtu*/
        /**je defaultne nastaveno na odesilat pri zmene*/
        if (bluetoothSending == null)
            contentValues.put(Konstanty.BLUE_TOOTH_SENDING_STILL,(byte[]) null);
        else
        contentValues.put(Konstanty.BLUE_TOOTH_SENDING_STILL,bluetoothSending);

        /**animace BAck*/
        if (bluetoothSending == null)
            contentValues.put(Konstanty.BACK_ANIMATION,(byte[]) null);
        else
            contentValues.put(Konstanty.BACK_ANIMATION,animback);

        databaOperations.update(Konstanty.TABLE_NAME,contentValues, Konstanty.ID+" = ?",new String[]{ id });
    }

    public void UpdatePlyn(String id, String hraniceColor, String posunColor, String colorBackground){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,id);
        if (posunColor == null)
            contentValues.put(Konstanty.TEXT_HRANICEcolor,(byte[]) null);
        else
            contentValues.put(Konstanty.TEXT_HRANICEcolor,hraniceColor);
        if (hraniceColor == null)
            contentValues.put(Konstanty.TEXT_COLOR_POSUNcolor,(byte[]) null);
        else
            contentValues.put(Konstanty.TEXT_COLOR_POSUNcolor,posunColor);
        if (colorBackground == null)
            contentValues.put(Konstanty.BACKGROUND_COLOR,(byte[]) null);
        else
            contentValues.put(Konstanty.BACKGROUND_COLOR,colorBackground);
        databaOperations.update(Konstanty.TABLE_NAME,contentValues, Konstanty.ID+" = ?",new String[]{ id });
    }
    public void UpdatePlyn_BlueTooth(String id, String nulovaHodnata, String stupen1, String stupen2,
                                     String stupen3, String stupen4, String supen5, String bluetoothSending,String animback){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,id);
        if (nulovaHodnata == null)
            contentValues.put(Konstanty.VALUES,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES,nulovaHodnata);
        if (stupen1 == null)
            contentValues.put(Konstanty.VALUES9,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES9,stupen1);
        if (stupen2 == null)
            contentValues.put(Konstanty.VALUES10,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES10,stupen2);
        if (stupen3 == null)
            contentValues.put(Konstanty.VALUES11,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES11,stupen3);
        if (stupen4 == null)
            contentValues.put(Konstanty.VALUES12,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES12,stupen4);
        if (supen5 == null)
            contentValues.put(Konstanty.VALUES13,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES13,supen5);
        /**styl odesílání bajtu*/
        /**je defaultne nastaveno na odesilat pri zmene*/
        if (bluetoothSending == null)
            contentValues.put(Konstanty.BLUE_TOOTH_SENDING_STILL,(byte[]) null);
        else
            contentValues.put(Konstanty.BLUE_TOOTH_SENDING_STILL,bluetoothSending);

        /**animace BAck*/
        if (bluetoothSending == null)
            contentValues.put(Konstanty.BACK_ANIMATION,(byte[]) null);
        else
            contentValues.put(Konstanty.BACK_ANIMATION,animback);
        databaOperations.update(Konstanty.TABLE_NAME,contentValues, Konstanty.ID+" = ?",new String[]{ id });

    }

    public void UpdatePosuvnik(String id,String maxProgress){
        SQLiteDatabase databaOperations = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Konstanty.ID,id);
        if (maxProgress == null)
            contentValues.put(Konstanty.VALUES,(byte[]) null);
        else
            contentValues.put(Konstanty.VALUES,maxProgress);
        databaOperations.update(Konstanty.TABLE_NAME,contentValues, Konstanty.ID+" = ?",new String[]{ id });
    }
}
