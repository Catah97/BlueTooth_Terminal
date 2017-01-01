package com.example.martin.bluetooth_terminal.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Martin on 1.1.2017.
 */

public class BluetoothBroacastListener extends BroadcastReceiver {

    public static boolean connected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            if (state == BluetoothAdapter.STATE_TURNING_OFF || state == BluetoothAdapter.STATE_DISCONNECTING)
            {
                connected = false;
            }
        }
    }
}
