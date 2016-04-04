package com.example.martin.bluetooth_terminal.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Save_Load;
import com.example.martin.bluetooth_terminal.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Martin on 23.10.2015.
 */
public class Database_name{

    static AlertDialog dialog;
    static boolean portail = false,landspace = false;
    final static int background_black = R.drawable.orientation_choose_set_background_black,
            background_white = R.drawable.orientation_choose_set_background_white;

    public static void Dialog (LayoutInflater inflater, final Context context, final boolean appTheme,
                               final Handler handler, final boolean firstRun) {
        portail = false;
        landspace = false;

        View rootView = inflater.inflate(R.layout.database_name, null);
        final ImageView imgPortail = (ImageView) rootView.findViewById(R.id.imageOrientationPortail);
        final ImageView imgLandspace = (ImageView) rootView.findViewById(R.id.imageOrientationLandspace);
        final EditText input = (EditText) rootView.findViewById(R.id.txt);

        imgPortail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portail = true;
                landspace = false;
                imgPortail.setBackgroundResource((appTheme ? background_black : background_white));
                imgLandspace.setBackgroundColor(Color.TRANSPARENT);
                if (CheckString(context, input.getText().toString()) && CheckOrientation())
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

            }
        });
        imgLandspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portail = false;
                landspace = true;
                imgPortail.setBackgroundColor(Color.TRANSPARENT);
                imgLandspace.setBackgroundResource((appTheme) ? background_black : background_white);
                if (CheckString(context, input.getText().toString()) && CheckOrientation())
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Zadejte název ovládání:");
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (CheckString(context, s.toString()) && CheckOrientation())
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

        });
        input.setBackgroundColor(Color.rgb(255, 255, 255));
        builder.setView(rootView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String readData = Save_Load.LoadedData(context, Konstanty.TABLES_FILE);
                List<String> list = Arrays.asList(readData.split(";"));
                if (input.getText().length() == 0) {
                    Toast.makeText(context, "Zadejte název ovládání.", Toast.LENGTH_SHORT).show();
                } else if (input.getText().toString().contains(";"))
                    Toast.makeText(context, "Zadali jste zakázaný znak ( ; ).", Toast.LENGTH_SHORT).show();
                else if (input.getText().toString().contains("("))
                    Toast.makeText(context, "Zadali jste zakázaný znak ( ( ).", Toast.LENGTH_SHORT).show();
                else if (input.getText().toString().contains(")"))
                    Toast.makeText(context, "Zadali jste zakázaný znak ( ) ).", Toast.LENGTH_SHORT).show();
                else if (input.getText().toString().contains(" "))
                    Toast.makeText(context, "Můsíte zadat pouze jedno slovo.", Toast.LENGTH_SHORT).show();
                else if (list.contains(input.getText().toString()))
                    Toast.makeText(context, "Takto už se jedno ovládání jmenuje.", Toast.LENGTH_SHORT).show();
                else if (!portail && !landspace)
                    Toast.makeText(context, "Nezvolili jste žádné ovládání.", Toast.LENGTH_SHORT).show();
                else {
                    Message msg = new Message();
                    if (firstRun)
                        msg.arg2 = 200;
                    else
                        msg.arg2 = 100;
                    String name = input.getText().toString();
                    Konstanty.TABLE_NAME = name;
                    if (portail)
                        Konstanty.TABLE_ORIENTATION = "portail";
                    else if (landspace)
                        Konstanty.TABLE_ORIENTATION = "landspace";
                    handler.sendMessage(msg);
                    dialog.dismiss();
                }

            }
        });
        if (!firstRun) {
            builder.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        if (firstRun) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        try {
            int dividerID = dialog.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            View titleDivider = dialog.getWindow().getDecorView().findViewById(dividerID);
            titleDivider.setBackgroundColor((appTheme) ? Color.BLACK : Color.WHITE); // change divider color
        }
        catch (Exception ignore){
        }

    }
    private static boolean CheckString(Context context,String input){
        String readData = Save_Load.LoadedData(context, Konstanty.TABLES_FILE);
        List<String> list = Arrays.asList(readData.split(";"));
        if (input.length() == 0) {
            return false;
        }
        else if (input.contains(";"))
            return false;
        else if (input.contains("("))
            return false;
        else if (input.contains(")"))
            return false;
        else if (input.contains(" "))
            return false;
        else if (list.contains(input))
            return false;
        else
            return true;
    }
    private static boolean CheckOrientation(){
        if (portail || landspace)
            return true;
        else
            return false;
    }
}
