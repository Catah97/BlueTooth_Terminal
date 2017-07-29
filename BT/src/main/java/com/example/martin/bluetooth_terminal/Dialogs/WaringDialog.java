package com.example.martin.bluetooth_terminal.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 12. 10. 2015.
 */
public class WaringDialog {
    public static void Dialog (Context context, final Handler handler,String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.waring);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.arg1 = Konstanty.UPDATE;
                handler.sendMessage(msg);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.arg1 = Konstanty.DELETE;
                handler.sendMessage(msg);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        try {
        int dividerID = dialog.getContext().getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.getWindow().getDecorView().findViewById(dividerID);
        titleDivider.setBackgroundColor(Color.BLACK); // change divider color
        }
        catch (Exception ignore){
        }
    }
}
