package com.example.martin.bluetooth_terminal.BlueTooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.martin.bluetooth_terminal.Other.Konstanty;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Created by Martin on 18.08.2015.
 * Třída která zařizuje připojení k zařízení, celá beží v pozadí
 * a tudíž nenarušuje chod aplikace
 */
public class BlueToothConnection extends AsyncTask<String,Void,Void>{
    BluetoothAdapter blueTooth = BluetoothAdapter.getDefaultAdapter();
    private final Handler handler;
    private String mac;
    public static BluetoothSocket socket = null;
    boolean fail = false;



    public BlueToothConnection(Handler handler){
        this.handler = handler;
    }

    private void Connect(BluetoothDevice device) {      /**vraci soket pokud vrati NULL pak se spojeni nenavázalo*/
        UUID uuid;
        if (Build.VERSION.SDK_INT < 15)                            /**plati pro APi LV 15 aplikace se pripoji pouze k auticku*/
            uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        else {
            try {
                ParcelUuid[] uuids = device.getUuids();
                uuid = uuids[0].getUuid();
            }
            catch (NullPointerException e){
                uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            }
        }
        Log.e("UUID", uuid.toString());
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            BluetoothBroacastListener.connected = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("socket", e.getMessage());
            socket = null;
            fail = true;
        }
    }
    /*private void ConnectionTwo(BluetoothDevice device) {
        try {
            socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
            try {
                socket.connect();
            } catch (IOException e) {
                Log.e("connect fail", e.getMessage());
                fail = true;
            }
        } catch (NoSuchMethodException e1) {
            Log.e("cath1", e1.getMessage());
            fail = true;
        } catch (InvocationTargetException e1) {
            Log.e("cath2", e1.getMessage());
            fail = true;
        } catch (IllegalAccessException e1) {
            Log.e("cath3", e1.getMessage());
            fail = true;
        }
    }*/

    @Override
    protected Void doInBackground(final String... params) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mac = params[0];
        blueTooth.cancelDiscovery();
        BluetoothDevice device = blueTooth.getRemoteDevice(mac);
        Log.e("api",Build.VERSION.SDK_INT+"");
        Connect(device);
        Message msg = new Message();
        Log.e("calnled", isCancelled() + " " + fail);
        if (fail && !isCancelled()) {
            msg.arg1 = Konstanty.BLUETOOTH_ASCYTAS_DONE;
            msg.arg2 = Konstanty.BLUETOOTH_FAIL;
            //msg.arg2 = Konstanty.BLUETOOTH_CONNECTED; //vyuzivam pokud se zrovna nikam nemohu pripojit
            handler.sendMessage(msg);
        }
        else if (!isCancelled()) {
            msg.arg1 = Konstanty.BLUETOOTH_ASCYTAS_DONE;
            msg.arg2 = Konstanty.BLUETOOTH_CONNECTED;
            handler.sendMessage(msg);
        }
        else{
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error BlueToothConectio", e.getMessage());
            }
        }
        return null;
    }


    @Override
    protected void onCancelled() {
        Log.e("BlueToothConnection","canceled");
        super.onCancelled();
    }
}
