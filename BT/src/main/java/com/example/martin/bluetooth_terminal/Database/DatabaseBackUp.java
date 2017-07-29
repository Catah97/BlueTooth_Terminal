package com.example.martin.bluetooth_terminal.Database;

import android.content.Context;
import android.database.Cursor;

import com.example.martin.bluetooth_terminal.Other.Save_Load;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Martin on 4. 11. 2015.
 * Uloží soubor .db ve kterém je uloženo nastavení, pokud uživatel si nepřeje uložit změnit, potom tento soubor
 * přepíše aktuální soubr s databází
 */
public class DatabaseBackUp {
    public static void Export(Context context,File originalDatabase, String filesName) throws IOException {
        InputStream in;
        OutputStream out;

            //create output directory if it doesn't exist


            in = new FileInputStream(originalDatabase);
            out = Save_Load.openFileOutput(context, filesName, Context.MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            // write the output file
            out.flush();
            out.close();
    }
    public static void Import(Context context,String filesName,File originalDatabase) throws IOException {
        InputStream in;
        OutputStream out;

        //create output directory if it doesn't exist


        in = Save_Load.openFileInput(context, filesName);
        out = new FileOutputStream(originalDatabase);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();

        // write the output file
        out.flush();
        out.close();
    }
}
