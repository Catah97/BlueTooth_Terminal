package com.example.martin.bluetooth_terminal.SetDatabase.Settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.R;

import java.util.Locale;

/**
 * Created by Martin on 18.5.2017.
 */

public class SettingActivity extends AppCompatActivity {

    protected void startInfo(String item) {
        if (isSupported()) {
            Intent information = new Intent(this, Information.class);
            information.putExtra("item", item);
            startActivity(information);
            overridePendingTransition(R.animator.set_control_right_in, R.animator.set_control_right_out);
        }
        else {
            Toast.makeText(this, R.string.unsupported, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isSupported(){
        Log.d("SettingActvity", Locale.getDefault().getDisplayLanguage());
        String language = Locale.getDefault().getDisplayLanguage();
        return language.equals("čeština");
    }
}
