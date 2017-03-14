package com.example.martin.bluetooth_terminal.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Methody;
import com.example.martin.bluetooth_terminal.Other.Save_Load;
import com.example.martin.bluetooth_terminal.R;

import java.util.ArrayList;

/**
 * Created by Martin on 24.10.2015.
 */
public class ChooseTable {
    public static AlertDialog Dialog(final Context context, final Handler handler, final ArrayList<String> data,final ArrayList<String> orientation){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialog));
        builder.setTitle(R.string.choose_control);
        final ListView listView = new ListView(new ContextThemeWrapper(context,R.style.CustomDialog));
        ArrayAdapter adapter = new ArrayAdapter(new ContextThemeWrapper(context,R.style.CustomDialog),android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        builder.setView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Konstanty.TABLE_NAME = data.get(position);
                Konstanty.TABLE_ORIENTATION  = orientation.get(position);
                Message msg = new Message();
                msg.arg1 = Konstanty.UPDATE;
                handler.sendMessage(msg);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder delete = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialog));
                delete.setTitle(R.string.waring);
                final String ovladani = parent.getItemAtPosition(position).toString();
                String message = String.format(context.getString(R.string.would_you_like_remove_control), ovladani);
                delete.setMessage(message);
                delete.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Methody.DeleteTable(context, ovladani, handler);
                    }
                });
                delete.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                delete.create().show();

                return true;
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }


    public static AlertDialog DialogChooseMenu(final Context context, final Handler handler, final ArrayList<String> data,final ArrayList<String> orientation){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.choose_control);
        final ListView listView = new ListView(context);
        ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        builder.setView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == data.size()-1){
                    Message msg = new Message();
                    msg.arg1 = Konstanty.CREATE_NEW_TABLE;
                    handler.sendMessage(msg);
                }
                else {
                    Konstanty.TABLE_NAME = data.get(position);
                    Konstanty.TABLE_ORIENTATION = orientation.get(position);
                    Message msg = new Message();
                    msg.arg1 = Konstanty.UPDATE;
                    handler.sendMessage(msg);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position != data.size()-1 && data.size() != 2) {
                    AlertDialog.Builder delete = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialog));
                    delete.setTitle(R.string.waring);
                    final String ovladani = parent.getItemAtPosition(position).toString();
                    String message = String.format(context.getString(R.string.would_you_like_remove_control), ovladani);
                    delete.setMessage(message);
                    delete.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Methody.DeleteTable(context, ovladani, handler);
                        }
                    });
                    delete.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    delete.create().show();
                    return true;
                }
                else
                    return false;

            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
