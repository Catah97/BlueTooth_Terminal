package com.example.martin.bluetooth_terminal.Other;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.SetDatabase.Item_Check;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Martin on 6. 10. 2015.
 * Všechno methody jsou staticke, aby je bylo možno volat odkudkoliv
 */
public class Methody implements TextWatcher,View.OnFocusChangeListener {

    public static byte[] fromBinary( String s )
    {
        int sLen = s.length();
        byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
        char c;
        for( int i = 0; i < sLen; i++ )
            if( (c = s.charAt(i)) == '1' )
                toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
            else if ( c != '0' )
                throw new IllegalArgumentException();
        return toReturn;
    }
    public static String toBinary(int dec){
        String final_string = "";
        for (int i = 128; i>0;i-=i/2){
            final_string = (dec >= i) ? final_string + "1" : final_string + "0";
            dec = (dec >= i) ? dec - i : dec;
            if (i == 1)
                break;
        }
        return final_string;
    }

    public static String toBinary( byte[] bytes ){
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++) {
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        }
        return sb.toString();
    }

    public static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    /**database*/
    public static void DeleteTable(Context context, String control, Handler handler){
        String readData = Save_Load.LoadedData(context, Konstanty.TABLES_FILE);
        String[] seznamTabulek = readData.split(";");
        readData = Save_Load.LoadedData(context,Konstanty.ORIENTATION_FILE);
        String[] seznamOrientation = readData.split(";");
        String tablesToSave= "" , orienstationToSave = "";
        DatabaOperations databaOperations = new DatabaOperations(context);
        databaOperations.DeleleTable(control);
        for (int i = 0;i<seznamTabulek.length;i++){
            if (!control.equals(seznamTabulek[i])){
                tablesToSave = tablesToSave + seznamTabulek[i]+";";
                orienstationToSave = orienstationToSave + seznamOrientation[i]+";";
            }
        }
        Save_Load.SaveData(context,"",tablesToSave,Konstanty.TABLES_FILE);
        Save_Load.SaveData(context,"",orienstationToSave,Konstanty.ORIENTATION_FILE);
        Message msg = new Message();
        msg.arg1 = Konstanty.DELETE;
        handler.sendMessage(msg);
    }

    public static boolean CheckBin(String text){
        ArrayList<Boolean> hodnoty = new ArrayList<Boolean>();
        char c;
        for (int i = 0;i<text.length();i++) {
            c = text.charAt(i);
            if (c == '1')
                hodnoty.add(true);
            else if (c == '0')
                hodnoty.add(true);
            else {
                hodnoty.add(false);
            }
        }
        for (int i = 0;i<hodnoty.size();i++)
        {
            Boolean b = hodnoty.get(i);
            if (!b)
                return false;
        }
        return true;
    }

    int beforeTXTleng;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeTXTleng = s.length();

    }

    /** k určení aby bylo jasno u kterého Textu opravovat*/
    public static EditText txt;
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
            txt = (EditText) v;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 4 && beforeTXTleng !=5) {
            txt.setText(s + " ");
            txt.setSelection(5);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /**nastavovani barev*/
    public static Bitmap SetColor (String s,int width,int height){
        String[] jednotliveBarvy = s.split(",");
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(Integer.parseInt(jednotliveBarvy[0]), Integer.parseInt(jednotliveBarvy[1]), Integer.parseInt(jednotliveBarvy[2]), Integer.parseInt(jednotliveBarvy[3]));
        return output;
    }

    public static Bitmap SetColor (Cursor cursor,int dataColumIndex,int width,int height){
        /**
         * na pozici 6 je text u tlacitko
         *             je barva hranic posuvniku
         * na pozici 7 je barva pro text u tlacitko
         *             je barva posunu u posuvniku
         * na pozici 8 je barva pro pozadi
         */
        String color = cursor.getString(dataColumIndex);
        String[] jednotliveBarvy = color.split(",");
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(Integer.parseInt(jednotliveBarvy[0]), Integer.parseInt(jednotliveBarvy[1]), Integer.parseInt(jednotliveBarvy[2]), Integer.parseInt(jednotliveBarvy[3]));
        return output;

    }
    public static String GetRGB(Bitmap input){
        int pixel;
        pixel = input.getPixel(input.getWidth() / 2, input.getHeight() / 2);
        String output = String.valueOf(Color.alpha(pixel))+","+String.valueOf(Color.red(pixel))+","+String.valueOf(Color.green(pixel))+","+String.valueOf(Color.blue(pixel));
        return output;
    }

    /**uprava stringu*/
    public static String CheckString(String input){
        String data = "NIC";
        try {
            if (input.length() == 1) {
                data = toBinary(input.getBytes());
            }
            if (input.length() == 2){
                int i = Integer.parseInt(input,16);
                data = toBinary(i);
            }
            if (input.length() == 9) {
                String[] pole = input.split(" ");
                input = pole[0] + pole[1];
            }
            if (input.length() == 8 && CheckBin(input)) {
                data = input;
            }
        }
        catch (Exception ignore){
        }
        return data;
    }

    public static String SetData(String input){
        String[] pole;
        if (input.length() == 9) {
            pole = input.split(" ");
            input = pole[0] + pole[1];
        }
        if (input.length() == 2){
            int i = Integer.parseInt(input,16);
            input = toBinary(i);
        }
        if (input.length() == 1)
            input = toBinary(input.getBytes());
        /**přidání 1 aby se hodnota spravne ulozila*/
        return 1+input;
    }


    /**for RGB colorGet*/
    public static String[] SetDataForPOsuvni(Cursor cursor){
        String[] output = new String[6];
        output[0] = cursor.getString(5);
        for (int i = 1;(i+8)<14;i++){
            output[i] = cursor.getString(i+8);
        }
        return output;
    }
    public static String SetString (String dataTosend){
        String finalString = dataTosend.substring(1, 5) + " " + dataTosend.substring(5);
        return finalString;
    }

    /**For Drag And Drop*/
    private static float colums_zbytek,rows_zbytek;
    private static float size;

    public static RelativeLayout.LayoutParams GetLayoutParams(int id){
        RelativeLayout.LayoutParams params = null;
        if (Item_Check.buttonID.contains(id))
            params = new RelativeLayout.LayoutParams(Math.round(3*(size+colums_zbytek)),Math.round(2*(size+rows_zbytek)));
        if (Item_Check.switchID.contains(id))
            params = new RelativeLayout.LayoutParams(Math.round(3*(size+colums_zbytek)),Math.round(1*(size+rows_zbytek)));
        if (Item_Check.volantRltID.contains(id))
            params = new RelativeLayout.LayoutParams(Math.round(4*(size+colums_zbytek)),Math.round(4*(size+rows_zbytek)));
        if (Item_Check.plynRltID.contains(id))
            params = new RelativeLayout.LayoutParams(Math.round(2*(size+colums_zbytek)),Math.round(6*(size+rows_zbytek)));
        if (Item_Check.posouvacID.contains(id))
            params = new RelativeLayout.LayoutParams(Math.round(8*(size+colums_zbytek)),Math.round(1*(size+rows_zbytek)));
        return params;
    }
    public static void SetKonstant(float scaledSizes,int width,int height){
        size = scaledSizes;

        int colums = (int) (width/(size));
        int rows = (int) (height/(size));

        if (width < colums*(size))
            colums = colums-1;
        if (height < colums*(size))
            rows = rows-1;

        colums_zbytek =  ((width - colums*(size))/colums);
        rows_zbytek =  ((height - rows*(size))/rows);
    }

}
