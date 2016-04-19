package com.example.martin.bluetooth_terminal.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.martin.bluetooth_terminal.Database.DatabaPairedDevices;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 01.04.2016.
 */
public class RenameDevice {
    public static void Dialog(final Context context, final String id, final Handler handler){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Nastavte jméno");
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaPairedDevices db = new DatabaPairedDevices(context);
                db.Updata("1"+id,input.getText().toString());
                Message msg = new Message();
                msg.arg1 = Konstanty.UPDATE;
                handler.sendMessage(msg);
            }
        });
        builder.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        try {
            int dividerID = dialog.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            View titleDivider = dialog.getWindow().getDecorView().findViewById(dividerID);
            titleDivider.setBackgroundColor(Color.WHITE); // change divider color
        }
        catch (Exception ignore){
        }

    }
}
