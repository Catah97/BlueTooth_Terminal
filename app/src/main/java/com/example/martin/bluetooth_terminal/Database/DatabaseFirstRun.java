package com.example.martin.bluetooth_terminal.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.example.martin.bluetooth_terminal.Dialogs.DatabaseCreating;
import com.example.martin.bluetooth_terminal.Other.Konstanty;

import java.util.ArrayList;

/**
 * Created by Martin on 25. 11. 2015.
 * Spouští se pouze při vytváření tabulky
 * a přiřezuje jednotlivým ID o jaký tip se jedna
 */
public class DatabaseFirstRun extends AsyncTask<ArrayList<Integer>,Void,Void> {

    final Context context;
    final Handler handler;

    public DatabaseFirstRun (Context context,Handler handler){
        this.context = context;
        this.handler = handler;
    }


    @Override
    protected Void doInBackground(ArrayList<Integer>... params) {
        DatabaOperations databaOperations = new DatabaOperations(context);
        ArrayList<Integer> list = params[0];
        for (int i = 0;i<list.size();i++){
            if (i<4)
                databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.VOLANT, "INVISIBLE");
            else if (i<6)
                databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.PLYN, "INVISIBLE");
            else if (i<16)
                databaOperations.PutData(String.valueOf(list.get(i)),Konstanty.SWITCH,"INVISIBLE");
            else if (i<36)
                databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.BUTTON, "INVISIBLE");
            else if (i<46)
                databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.SEEK_BAR, "INVISIBLE");
            else
                databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.POZNAMKY, "INVISIBLE");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!isCancelled()) {
            Message msg = new Message();
            msg.arg1 = 100;
            handler.sendMessage(msg);
            DatabaseCreating.dialog.dismiss();
        }

    }
}
