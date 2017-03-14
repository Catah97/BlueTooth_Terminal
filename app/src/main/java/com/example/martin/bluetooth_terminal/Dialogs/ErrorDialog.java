package com.example.martin.bluetooth_terminal.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 3. 9. 2015.
 */
public class ErrorDialog {
    public static void Dialog (Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.one_value_has_not_been_set);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
