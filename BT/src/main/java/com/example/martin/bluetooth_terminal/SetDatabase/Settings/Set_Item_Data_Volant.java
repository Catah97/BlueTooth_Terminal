package com.example.martin.bluetooth_terminal.SetDatabase.Settings;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.Dialogs.WaringDialog;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Methody;
import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 6. 10. 2015.
 */
public class Set_Item_Data_Volant extends SettingActivity {

    boolean save, stillsending,animBack;
    String[] list = new String[]{"1","2","3"};

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
        actionBar.setTitle(R.string.setting_for_steering_wheel);
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
                startInfo(Konstanty.VOLANT);
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
            WaringDialog.Dialog(this,handler, getString(R.string.would_you_like_continue_without_save));
        }
        else {
            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.animator.set_control_left_in, R.animator.set_control_left_out);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_data_for_volant);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /**radioButton*/
        final RadioButton change = (RadioButton) findViewById(R.id.radioBtnChange);
        final RadioButton still = (RadioButton) findViewById(R.id.radioBtnStill);

        /**label*/
        final TextView txtStupen2 = (TextView) findViewById(R.id.txtStupen2);
        final TextView txtStupen3 = (TextView) findViewById(R.id.txtStupen3);
        final TextView txtStupen2second = (TextView) findViewById(R.id.tstStupen22);
        final TextView txtStupen3second = (TextView) findViewById(R.id.tstStupen33);
        /**natahnuti dat z databaze*/
        final String id = getIntent().getExtras().getString("id");
        DatabaOperations databaOperations  = new DatabaOperations(this);
        Cursor cr = databaOperations.GetID_Data(id);
        cr.moveToFirst();
        /**prava*/
        final EditText txtVpravo1 = (EditText)findViewById(R.id.txtVpravo);
        txtVpravo1.addTextChangedListener(new Methody());
        txtVpravo1.setOnFocusChangeListener(new Methody());
        final EditText txtVpravo2 = (EditText)findViewById(R.id.txtVpravo2);
        txtVpravo2.addTextChangedListener(new Methody());
        txtVpravo2.setOnFocusChangeListener(new Methody());
        final EditText txtVpravo3 = (EditText)findViewById(R.id.txtVpravo3);
        txtVpravo3.addTextChangedListener(new Methody());
        txtVpravo3.setOnFocusChangeListener(new Methody());
        /**leva*/
        final EditText txtVlevo1 = (EditText)findViewById(R.id.txtVlevo);
        txtVlevo1.addTextChangedListener(new Methody());
        txtVlevo1.setOnFocusChangeListener(new Methody());
        final EditText txtVlevo2 = (EditText)findViewById(R.id.txtVlevo2);
        txtVlevo2.addTextChangedListener(new Methody());
        txtVlevo2.setOnFocusChangeListener(new Methody());
        final EditText txtVlevo3 = (EditText)findViewById(R.id.txtVlevo3);
        txtVlevo3.addTextChangedListener(new Methody());
        txtVlevo3.setOnFocusChangeListener(new Methody());

        /**rovne*/
        final EditText txtRovne = (EditText) findViewById(R.id.txtRovne);
        txtRovne.addTextChangedListener(new Methody());
        txtRovne.setOnFocusChangeListener(new Methody());

        /**spinner*/
        final Spinner spiner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.select_dialog_item, list);
        spiner.setAdapter(adapter);
        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtStupen2.setVisibility(View.GONE);
                txtStupen3.setVisibility(View.GONE);
                txtStupen2second.setVisibility(View.GONE);
                txtStupen3second.setVisibility(View.GONE);

                txtVpravo2.setVisibility(View.GONE);
                txtVpravo3.setVisibility(View.GONE);

                txtVlevo2.setVisibility(View.GONE);
                txtVlevo3.setVisibility(View.GONE);
                switch (position) {
                    case 1:
                        txtStupen2.setVisibility(View.VISIBLE);
                        txtStupen2second.setVisibility(View.VISIBLE);
                        txtVpravo2.setVisibility(View.VISIBLE);
                        txtVlevo2.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        txtStupen2.setVisibility(View.VISIBLE);
                        txtStupen2second.setVisibility(View.VISIBLE);
                        txtVpravo2.setVisibility(View.VISIBLE);
                        txtVlevo2.setVisibility(View.VISIBLE);
                        txtStupen3.setVisibility(View.VISIBLE);
                        txtStupen3second.setVisibility(View.VISIBLE);
                        txtVpravo3.setVisibility(View.VISIBLE);
                        txtVlevo3.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**nastavení ulozeny hodnot*/
        /**kdzby hodnota nebyla uložena tak aby appka nespadla*/
        try {
            stillsending = Boolean.parseBoolean(cr.getString(15));
            if (stillsending)
                still.setChecked(true);
            else
                change.setChecked(true);
        }
        catch (Exception ignore){

        }
        /**radioButton animation*/
        final RadioButton animationBack = (RadioButton) findViewById(R.id.radioBtnAnimBack);
        final RadioButton notAnimationBack = (RadioButton) findViewById(R.id.radioBtnWithoutAnimBack);
        try {
            animBack = Boolean.parseBoolean(cr.getString(16));
            if (!animBack && cr.getString(16)!= null)
                notAnimationBack.setChecked(true);
            else
                animationBack.setChecked(true);

        }
        catch (Exception ignore){
            animationBack.setChecked(true);
            animBack = true;

        }

        int fail = 2;
        try {
            txtRovne.setText(Methody.SetString(cr.getString(5)));
            txtVpravo1.setText(Methody.SetString(cr.getString(12)));
            txtVlevo1.setText(Methody.SetString(cr.getString(9)));
        }
        catch (Exception ignore){
        }
        try {
            if (!cr.getString(13).equals(cr.getString(14))) {
                txtVpravo3.setText(Methody.SetString(cr.getString(14)));
                txtVlevo3.setText(Methody.SetString(cr.getString(11)));
            } else {
                /**je zde aby program padl :D */
                fail = Integer.parseInt("sada");
            }
        }
        catch (Exception ignore){
            txtStupen3.setVisibility(View.GONE);
            txtStupen3second.setVisibility(View.GONE);
            txtVpravo3.setVisibility(View.GONE);
            txtVlevo3.setVisibility(View.GONE);
            /**slouží k idenfitikaci kde se stala chyba*/
            fail = 1;
        }
        try {
            if (!cr.getString(12).equals(cr.getString(13))) {
                txtVpravo2.setText(Methody.SetString(cr.getString(13)));
                txtVlevo2.setText(Methody.SetString(cr.getString(10)));
            } else {
                /**je zde aby program padl :D */
                fail = Integer.parseInt("sada");
            }
        }
        catch (Exception ignore){
            txtStupen2.setVisibility(View.GONE);
            txtStupen2second.setVisibility(View.GONE);
            txtVpravo2.setVisibility(View.GONE);
            txtVlevo2.setVisibility(View.GONE);
            /**slouží k idenfitikaci kde se stala chyba*/
            fail = 0 ;
        }
        /**nastavi spinner do pozice*/
        spiner.setSelection(fail);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean fail = false;
                if (Methody.CheckString(txtVpravo1.getText().toString()).equals("NIC")){
                    fail = true;
                    Log.e("error","log1");
                }
                if (Methody.CheckString(txtVpravo2.getText().toString()).equals("NIC") && txtVpravo2.getVisibility() == View.VISIBLE ){
                    fail = true;
                    Log.e("error","log2");
                }
                if (Methody.CheckString(txtVpravo3.getText().toString()).equals("NIC") && txtStupen3.getVisibility() == View.VISIBLE){
                    fail = true;
                    Log.e("error","log3");
                }
                if (Methody.CheckString(txtVlevo1.getText().toString()).equals("NIC")){
                    fail = true;
                    Log.e("error","log4");
                }
                if (Methody.CheckString(txtVlevo2.getText().toString()).equals("NIC") && txtVlevo2.getVisibility() == View.VISIBLE){
                    fail = true;
                    Log.e("error","log5");
                }
                if (Methody.CheckString(txtVlevo3.getText().toString()).equals("NIC") && txtVlevo3.getVisibility() == View.VISIBLE){
                    fail = true;
                    Log.e("error","log6");
                }
                if (Methody.CheckString(txtRovne.getText().toString()).equals("NIC")){
                    fail = true;
                    Log.e("error","log7");
                }
                if (fail){
                    Toast.makeText(getApplicationContext(), R.string.one_value_has_incorect_setting, Toast.LENGTH_SHORT).show();
                }
                if (!fail){
                    String[] data = new String[7];
                    if (txtRovne.getVisibility() == View.VISIBLE) {
                        String string = txtRovne.getText().toString();
                        data[0] = Methody.SetData(string);
                    }
                    if (txtVpravo1.getVisibility() == View.VISIBLE) {
                        String string = txtVpravo1.getText().toString();
                        data[1] = Methody.SetData(string);
                    }
                    if (txtVpravo2.getVisibility() == View.VISIBLE){
                        String string = txtVpravo2.getText().toString();
                        data[2] = Methody.SetData(string);
                    }
                    if (txtVpravo3.getVisibility() == View.VISIBLE){
                        String string = txtVpravo3.getText().toString();
                        data[3] = Methody.SetData(string);
                    }
                    /**LEVA*/
                    if (txtVlevo1.getVisibility() == View.VISIBLE){
                        String string = txtVlevo1.getText().toString();
                        data[4] = Methody.SetData(string);
                    }
                    if (txtVlevo2.getVisibility() == View.VISIBLE){
                        String string = txtVlevo2.getText().toString();
                        data[5] = Methody.SetData(string);
                    }
                    if (txtVlevo3.getVisibility() == View.VISIBLE){
                        String string = txtVlevo3.getText().toString();
                        data[6] = Methody.SetData(string);
                    }


                    /**uložení stylu posílaní bztu**/
                    stillsending = still.isChecked();
                    /**uložení jestli má proběhnout animace zpět*/
                    animBack = animationBack.isChecked();

                    SaveData(Set_Item_Data_Volant.this, id, CheckData(data));
                    finish();
                    overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
                }
            }
        });
    }
    private String[] CheckData(String[] data){
        if (data[3] == null && data[2] != null){
            data[3] = data[2];
        }
        else if (data[3] == null && data[2] == null){
            data[3] = data[1];
            data[2] = data[1];
        }
        /**LEVA*/
        if (data[6] == null && data[5] != null){
            data[6] = data[5];
        }
        else if (data[6] == null && data[5] == null){
            data[6] = data[4];
            data[5] = data[4];
        }
        return data;
    }


    private void SaveData(Context context,String id,String[] data) {
        DatabaOperations databaOperations = new DatabaOperations(context);
        /**ROVNE, PRAVA,                   LEVA                       */
        databaOperations.UpdateVolant_BlueTooth(id,data[0],data[1],data[2],data[3],data[4],data[5],data[6]
                ,String.valueOf(stillsending),String.valueOf(animBack));
    }




}
