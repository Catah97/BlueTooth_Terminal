package com.example.martin.bluetooth_terminal.SetDatabase.Settings;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.Dialogs.WaringDialog;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 7. 1. 2016.
 */
public class Set_Item_Data_Posuvnik extends SettingActivity {

    String id;
    boolean save;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == Konstanty.UPDATE){
                finish();
                overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_item, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                  /**zobrazí iconu sipku zpet*/
        actionBar.setHomeButtonEnabled(true);                       /**nastaví iconu eneble pro click*/
        actionBar.setTitle(R.string.setting_for_seekbar);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                End();
                break;
            case R.id.information:
                startInfo(Konstanty.SEEK_BAR);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            End();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void End(){
        if (!save){
            WaringDialog.Dialog(this, handler, getString(R.string.would_you_like_continue_without_save));
        }
        else {
            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_data_for_posuvnik);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final DatabaOperations databaOperations = new DatabaOperations(Set_Item_Data_Posuvnik.this);
        id = getIntent().getExtras().getString("id");
        Cursor cursor = databaOperations.GetID_Data(id);
        cursor.moveToFirst();
        final EditText txtMax = (EditText) findViewById(R.id.txtMax);
        try {
            txtMax.setText(cursor.getString(5));
        }
        catch (Exception ignore){
        }

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String max = txtMax.getText().toString();
                try {
                    if (Integer.parseInt(max) == 0)
                        Toast.makeText(Set_Item_Data_Posuvnik.this, R.string.have_to_set_value_large_then_zero, Toast.LENGTH_SHORT).show();
                    else {
                        databaOperations.UpdatePosuvnik(id, max);
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
                    }
                }
                catch (Exception ignore){
                    Toast.makeText(Set_Item_Data_Posuvnik.this, R.string.you_have_to_set_some_value, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
