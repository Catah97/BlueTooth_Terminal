package com.example.martin.bluetooth_terminal.SetDatabase.Settings;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.Dialogs.WaringDialog;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Methody;
import com.example.martin.bluetooth_terminal.R;


import yuku.ambilwarna.ColorPicker;

/**
 * Created by Martin on 4. 11. 2015.
 */
public class Set_Item_Data_Plyn extends SettingActivity{


    boolean save,stillsending,animBack;

    ImageView colorHranice,colorPosun,colorBackground;
    String[] list = new String[]{"2","3","4","5"};
    String id;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == Konstanty.UPDATE){
                setResult(RESULT_OK);
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
        actionBar.setTitle(R.string.setting_for_power);
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
                startInfo(Konstanty.PLYN);
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
    protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Konstanty.COLOR_SET){
            final int id = data.getExtras().getInt("ID");
            final String string = data.getExtras().getString("color");
            final ImageView img = (ImageView) findViewById(id);
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (img.getWidth() == 0){
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String[] barva = string.split(",");
                            ImageView img = (ImageView) findViewById(id);
                            Bitmap output = Bitmap.createBitmap(img.getWidth(),
                                    img.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(output);
                            canvas.drawARGB(Integer.parseInt(barva[0]), Integer.parseInt(barva[1]), Integer.parseInt(barva[2]), Integer.parseInt(barva[3]));
                            img.setImageBitmap(output);
                        }
                    });
                }
            });
            thread.start();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            colorPosun.setDrawingCacheEnabled(true);
            colorHranice.setDrawingCacheEnabled(true);
            colorBackground.setDrawingCacheEnabled(true);
            outState.putString("posun", Methody.GetRGB(colorPosun.getDrawingCache()));
            outState.putString("hranice", Methody.GetRGB(colorHranice.getDrawingCache()));
            outState.putString("background", Methody.GetRGB(colorBackground.getDrawingCache()));
            colorPosun.setDrawingCacheEnabled(false);
            colorHranice.setDrawingCacheEnabled(false);
            colorBackground.setDrawingCacheEnabled(false);
        }
        catch (Exception ignore){
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_data_for_plyn);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        id = getIntent().getExtras().getString("id");

        final DatabaOperations databaOperations = new DatabaOperations(this);
        final Cursor cr = databaOperations.GetID_Data(id);
        cr.moveToFirst();
        final EditText txt0 = (EditText) findViewById(R.id.txt0);
        txt0.setOnFocusChangeListener(new Methody());
        txt0.addTextChangedListener(new Methody());
        txt0.setNextFocusDownId(R.id.txt1);
        final EditText txt1 = (EditText) findViewById(R.id.txt1);
        txt1.setOnFocusChangeListener(new Methody());
        txt1.addTextChangedListener(new Methody());
        txt1.setNextFocusDownId(R.id.txt2);
        final EditText txt2 = (EditText) findViewById(R.id.txt2);
        txt2.setOnFocusChangeListener(new Methody());
        txt2.addTextChangedListener(new Methody());
        txt2.setNextFocusDownId(R.id.txt3);
        final TextView textView3 = (TextView) findViewById(R.id.textView3);
        final EditText txt3 = (EditText) findViewById(R.id.txt3);
        txt3.setOnFocusChangeListener(new Methody());
        txt3.addTextChangedListener(new Methody());
        txt3.setNextFocusDownId(R.id.txt4);
        final TextView textView4 = (TextView) findViewById(R.id.textView4);
        final EditText txt4 = (EditText) findViewById(R.id.txt4);
        txt4.setOnFocusChangeListener(new Methody());
        txt4.addTextChangedListener(new Methody());
        txt4.setNextFocusDownId(R.id.txt5);
        final TextView textView5 = (TextView) findViewById(R.id.textView5);
        final EditText txt5 = (EditText) findViewById(R.id.txt5);
        txt5.setOnFocusChangeListener(new Methody());
        txt5.addTextChangedListener(new Methody());

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (txt0.getWidth() == 0){
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        colorHranice = (ImageView) findViewById(R.id.imgColorHranice);
                        colorPosun = (ImageView) findViewById(R.id.imgColorPosun);
                        colorBackground = (ImageView) findViewById(R.id.imgColorBackground);
                        try {
                            if (savedInstanceState == null)
                                colorHranice.setImageBitmap(Methody.SetColor(cr, 6, colorHranice.getWidth(), colorHranice.getHeight()));
                            else
                                colorHranice.setImageBitmap(Methody.SetColor(savedInstanceState.getString("hranice"), colorHranice.getWidth(), colorHranice.getHeight()));
                            if (savedInstanceState == null)
                                colorPosun.setImageBitmap(Methody.SetColor(cr, 7, colorPosun.getWidth(), colorPosun.getHeight()));
                            else
                                colorPosun.setImageBitmap(Methody.SetColor(savedInstanceState.getString("posun"), colorPosun.getWidth(), colorPosun.getHeight()));
                            if (savedInstanceState == null)
                                colorBackground.setImageBitmap(Methody.SetColor(cr, 8, colorBackground.getWidth(), colorBackground.getHeight()));
                            else
                                colorBackground.setImageBitmap(Methody.SetColor(savedInstanceState.getString("background"), colorBackground.getWidth(), colorBackground.getHeight()));
                        }
                        catch (Exception ignore){
                        }
                    }
                });
            }
        });
        thread.start();
        final Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.select_dialog_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txt3.setFocusable(false);
                txt4.setFocusable(false);
                txt5.setFocusable(false);


                txt3.setVisibility(View.GONE);
                txt4.setVisibility(View.GONE);
                txt5.setVisibility(View.GONE);
                textView3.setVisibility(View.GONE);
                textView4.setVisibility(View.GONE);
                textView5.setVisibility(View.GONE);
                if (position > 0) {
                    txt3.setVisibility(View.VISIBLE);
                    textView3.setVisibility(View.VISIBLE);
                    txt3.setFocusable(true);
                    txt3.setFocusableInTouchMode(true);
                }
                if (position > 1) {
                    txt4.setVisibility(View.VISIBLE);
                    textView4.setVisibility(View.VISIBLE);
                    txt4.setFocusable(true);
                    txt4.setFocusableInTouchMode(true);
                }
                if (position > 2) {
                    txt5.setVisibility(View.VISIBLE);
                    textView5.setVisibility(View.VISIBLE);
                    txt5.setFocusable(true);
                    txt5.setFocusableInTouchMode(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int fail = 3;
        try {
            txt0.setText(Methody.SetString(cr.getString(5)));
            txt1.setText(Methody.SetString(cr.getString(9)));
            txt2.setText(Methody.SetString(cr.getString(10)));
        }
        catch (Exception ignore){
        }
        try {
            if (cr.getString(13) != null)
                txt5.setText(Methody.SetString(cr.getString(13)));
            else
            fail = Integer.parseInt("sada");
        }
        catch (Exception ignore){
            txt5.setVisibility(View.GONE);
            fail = 2;
        }
        try {
            if (cr.getString(12) != null) {
                txt4.setText(Methody.SetString(cr.getString(12)));
            }
            else
            fail = Integer.parseInt("sada");
        }
        catch (Exception ignore){
            txt4.setVisibility(View.GONE);
            fail = 1;
        }
        try {
            if (cr.getString(11) != null) {
                txt3.setText(Methody.SetString(cr.getString(11)));
            }
            else
            fail = Integer.parseInt("sada");
        }
        catch (Exception ignore){
            txt3.setVisibility(View.GONE);
            fail = 0;
        }
        /**radioButton sending*/
        final RadioButton change = (RadioButton) findViewById(R.id.radioBtnChange);
        final RadioButton still = (RadioButton) findViewById(R.id.radioBtnStill);
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
        spinner.setSelection(fail);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean fail = false;
                if (Methody.CheckString(txt0.getText().toString()).equals("NIC")){
                    fail = true;
                    Log.e("error", "log1");
                }
                if (Methody.CheckString(txt1.getText().toString()).equals("NIC")){
                    fail = true;
                    Log.e("error","log4");
                }
                if (Methody.CheckString(txt2.getText().toString()).equals("NIC")){
                    fail = true;
                    Log.e("error","log2");
                }
                if (Methody.CheckString(txt3.getText().toString()).equals("NIC") && txt3.getVisibility() == View.VISIBLE){
                    fail = true;
                    Log.e("error","log3");
                }
                if (Methody.CheckString(txt4.getText().toString()).equals("NIC") && txt4.getVisibility() == View.VISIBLE){
                    fail = true;
                    Log.e("error","log5");
                }
                if (Methody.CheckString(txt5.getText().toString()).equals("NIC") && txt5.getVisibility() == View.VISIBLE){
                    fail = true;
                    Log.e("error","log6");
                }
                if (fail){
                    Toast.makeText(getApplicationContext(), R.string.one_value_has_incorect_setting, Toast.LENGTH_SHORT).show();
                }
                if (!fail){
                    String[] data = new String[6];
                    if (txt0.getVisibility() == View.VISIBLE) {
                        String string = txt0.getText().toString();
                        data[0] = Methody.SetData(string);
                    }
                    if (txt1.getVisibility() == View.VISIBLE) {
                        String string = txt1.getText().toString();
                        data[1] = Methody.SetData(string);
                    }
                    if (txt2.getVisibility() == View.VISIBLE){
                        String string = txt2.getText().toString();
                        data[2] = Methody.SetData(string);
                    }
                    if (txt3.getVisibility() == View.VISIBLE){
                        String string = txt3.getText().toString();
                        data[3] = Methody.SetData(string);
                    }
                    /**LEVA*/
                    if (txt4.getVisibility() == View.VISIBLE){
                        String string = txt4.getText().toString();
                        data[4] = Methody.SetData(string);
                    }
                    if (txt5.getVisibility() == View.VISIBLE){
                        String string = txt5.getText().toString();
                        data[5] = Methody.SetData(string);
                    }
                    colorHranice.setDrawingCacheEnabled(true);
                    colorPosun.setDrawingCacheEnabled(true);
                    colorBackground.setDrawingCacheEnabled(true);
                    /**uložení stylu posílaní bztu**/
                    stillsending = (still.isChecked());
                    animBack = (animationBack.isChecked());

                    colorPosun.setDrawingCacheEnabled(true);
                    colorHranice.setDrawingCacheEnabled(true);
                    colorBackground.setDrawingCacheEnabled(true);
                    databaOperations.UpdatePlyn(id, Methody.GetRGB(colorHranice.getDrawingCache()), Methody.GetRGB(colorPosun.getDrawingCache()), Methody.GetRGB(colorBackground.getDrawingCache()));
                    databaOperations.UpdatePlyn_BlueTooth(id, data[0], data[1], data[2], data[3], data[4], data[5], String.valueOf(stillsending),String.valueOf(animBack));
                    colorPosun.setDrawingCacheEnabled(false);
                    colorHranice.setDrawingCacheEnabled(false);
                    colorBackground.setDrawingCacheEnabled(false);
                    setResult(RESULT_OK);
                    finish();
                    overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
                }
            }
        });

    }


    public void StartSettingColorPosuvnik(View view){
        Intent intent = new Intent(Set_Item_Data_Plyn.this,ColorPicker.class);
        view.setDrawingCacheEnabled(true);
        intent.putExtra("BITMAP", Methody.GetRGB(view.getDrawingCache()));
        intent.putExtra("ID", view.getId());
        view.setDrawingCacheEnabled(false);
        startActivityForResult(intent, Konstanty.COLOR_SET);
        overridePendingTransition(R.animator.set_control_right_in,R.animator.set_control_right_out);

    }



}
