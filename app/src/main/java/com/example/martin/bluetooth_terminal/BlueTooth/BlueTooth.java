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
            byte[] buffer = new byte[256];

            while (!this.isInterrupted()) {
                try {
                    int bytes = mmInStream.read(buffer);
                    Log.e("bytes", buffer[0] + "");
                    blueToothListener.incomingBytes(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("bluetoothReader", e.getMessage());
                    break;
                }
            }
        }
    }


    public static void Send(int msg){
        Log.d("POslano", msg + "");
        try {
            byte[] buffer = new byte[]{(byte) 0x0, (byte)0x3, (byte)0x0, (byte)0x0};
            mmOutStream.write(buffer, 0 ,4);
            //mmOutStream.write(msg);
            if (Console.dataOUT.size() >= 500)
                while (Console.dataOUT.size() > 500) {
                    Console.dataOUT.remove(500);
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] createBuffer(int msg){
        byte[] buffer = new byte[4];
        return buffer;
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
            blueToothListener.sendMessage(mesega);
            return;
        }
            byte[] buffer = Methody.fromBinary(msg);
        try {
            mmOutStream.write(buffer);
            int b = Integer.parseInt(msg, 2);
            if (Console.dataOUT.size() >= 500)
                Console.dataOUT.remove(500);
            Console.dataOUT.add(String.valueOf(b));
        } catch (IOException e) {                                                                    /**zde napsat kod který zjistí že zařízení nedostává bajty*/
            }
    }

    public static interface BlueToothListener{
        void sendMessage(Message msg);
        void incomingBytes(byte[] bytes);
    }

}