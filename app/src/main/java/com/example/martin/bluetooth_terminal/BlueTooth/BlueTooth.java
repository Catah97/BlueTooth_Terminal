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
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Martin on 19.08.2015.
 * Tato třida cokunikační třída
 * využívá se k obousměrné komunikaci mezi zařízeními
 */
public class BlueTooth extends Thread {

    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    public static OutputStream mmOutStream;
    private static BlueToothListener blueToothListener;

    public BlueTooth(BluetoothSocket socket,BlueToothListener blueToothListener) {
        this.blueToothListener = blueToothListener;
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
        synchronized (this) {
            while (!this.isInterrupted()) {
                try {
                    int bytesAvailable = mmInStream.available();
                    if(bytesAvailable > 0)
                    {
                        byte[] buffer = new byte[bytesAvailable];
                        int size = mmInStream.read(buffer);
                        Log.e("bluetoothReader", "Data size: " + size);
                        for (byte oneByte : buffer){
                            if (oneByte != 0) {
                                blueToothListener.incomingBytes(oneByte);
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e("bluetoothReader", "error", e);
                    break;
                }
            }
        }
    }

    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }



    public static void Send(int msg){
        Log.d("POslano", msg + "");
        try {
            byte[] bytes = ByteBuffer.allocate(4).putInt(msg).array();
            mmOutStream.write(bytes);
            mmOutStream.flush();
            if (Console.dataOUT.size() >= 500)
                while (Console.dataOUT.size() > 500) {
                    Console.dataOUT.remove(500);
                }
            Console.dataOUT.add(String.valueOf(msg));

        } catch (IOException e) {
            Log.e("BlueTooth", "send error", e);
        }
    }

    public static void Send(String msg) {
        /**smaže první znak, kterým je 1. Je to tak ,protože při neudělání toho kromu se do databáze uložila špatná hodnota*/
        try {

            Log.d("POslano", msg + "");
            msg = msg.substring(1);
        } catch (Exception ignore) {
            Message mesega = new Message();
            mesega.arg1 = -1;
            blueToothListener.sendMessage(mesega);
            return;
        }
        byte[] buffer = Methody.fromBinary(msg);
        try {
            mmOutStream.write(buffer);
            mmOutStream.flush();
            int b = Integer.parseInt(msg, 2);
            if (Console.dataOUT.size() >= 500)
                Console.dataOUT.remove(500);
            Console.dataOUT.add(String.valueOf(b));
        } catch (IOException e) {
            Log.e("BlueTooth", "send error", e);
            /**zde napsat kod který zjistí že zařízení nedostává bajty*/
        }
    }

    public static interface BlueToothListener{
        void sendMessage(Message msg);
        void incomingBytes(byte bytes);
    }

}