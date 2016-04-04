package com.example.martin.bluetooth_terminal.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;

import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 2. 9. 2015.
 */
public class Database_dont_exist {
    public static AlertDialog.Builder Dialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialog));
        builder.setMessage("Musíte nastavit olvdání.");
        return builder;
    }
}
