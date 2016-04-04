package com.example.martin.bluetooth_terminal.Other;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Ukládání a nebo oteviráni souborů
 */

    public class Save_Load {
        public static void SaveData(Context context, String readData,String name,String file) {         /**Ukládá se ve formátu R,G,B;R,B,G,; atd.....*/
            try {
                FileOutputStream out = openFileOutput(context, file, Context.MODE_PRIVATE);
                OutputStreamWriter writer = new OutputStreamWriter(out);
                try {
                    writer.write(readData+name);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Nepodařilo se uložit data.", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "Nepodařilo se uložit data.", Toast.LENGTH_SHORT).show();
            }
        }

        public static FileOutputStream openFileOutput(Context context, String name, int mode)
                throws FileNotFoundException {
            return context.openFileOutput(name, mode);
        }

        public static String LoadedData(Context context,String file) {
            String final_data = "";
            try {
                FileInputStream fi = openFileInput(context, file);
                InputStreamReader in = new InputStreamReader(fi);
                char[] buffer = new char[128];
                int size;
                try {
                    while ((size = in.read(buffer)) > 0) {
                        String read_data = String.valueOf(buffer, 0, size);
                        final_data += read_data;
                        buffer = new char[128];
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return final_data;
        }

        public static FileInputStream openFileInput(Context context, String name)
                throws FileNotFoundException {
            return context.openFileInput(name);

        }
    }


