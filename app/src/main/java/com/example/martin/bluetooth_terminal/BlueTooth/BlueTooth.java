package com.example.martin.bluetooth_terminal.BlueTooth;

import android.bluetooth.BluetoothSocket;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.Controls.Console;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Methody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Martin on 19.08.2015.
 * Tato třida cokunikační třída
 * využívá se k obousměrné komunikaci mezi zařízeními
 */
public class BlueTooth extends Thread {
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    public static OutputStream mmOutStream;
    private static Handler handler;

    public BlueTooth(BluetoothSocket socket,Handler handler) {
        this.handler = handler;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
              tmpIn = mmSocket.getInputStream();
              tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
    public void run()
    {
        byte[] buffer = new byte[1024];

        while (!this.isInterrupted()) {
            try {
                int bytes = mmInStream.read(buffer);
                Log.e("bytes", bytes + "");
                handler.obtainMessage(10,buffer)
                        .sendToTarget();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("bluetoothReader", e.getMessage());
                break;
            }
        }
    }


    public static void Send(int msg){
        Log.d("POslano", msg + "");
        try {
            mmOutStream.write(msg);
            if (Console.dataOUT.size() >= 500)
                Console.dataOUT.remove(500);
            Console.dataOUT.add(String.valueOf(msg));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Send(String msg) {
        /**smaže první znak, kterým je 1. Je to tak ,protože při neudělání toho kromu se do databáze uložila špatná hodnota*/
        try {

            Log.d("POslano", msg + "");
            msg = msg.substring(1);
        }
        catch (Exception ignore){
            Message mesega = new Message();
            mesega.arg1 = -1;
            handler.sendMessage(mesega);
            return;
        }
            byte[] buffer = Methody.fromBinary(msg);
        try {
            mmOutStream.write(buffer);
            int b = Integer.parseInt(msg, 2);
            if (Console.dataOUT.size() >= 500)
                Console.dataOUT.remove(500);
            Console.dataOUT.add(String.valueOf(msg));
        } catch (IOException e) {                                                                    /**zde napsat kod který zjistí že zařízení nedostává bajty*/
            }
    }


}