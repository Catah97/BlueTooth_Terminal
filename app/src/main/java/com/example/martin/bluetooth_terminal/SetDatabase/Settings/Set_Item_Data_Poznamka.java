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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.Dialogs.WaringDialog;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Methody;
import com.example.martin.bluetooth_terminal.R;


import yuku.ambilwarna.ColorPicker;

/**
 * Created by Martin on 25.12.2015.
 */
public class Set_Item_Data_Poznamka extends SettingActivity {
    boolean save;
    ImageView textColor,backgroundColor;
    String id;

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
        actionBar.setTitle(R.string.setting_for_note);
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
                startInfo(Konstanty.POZNAMKY);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            textColor.setDrawingCacheEnabled(true);
            backgroundColor.setDrawingCacheEnabled(true);
            outState.putString("text", Methody.GetRGB(textColor.getDrawingCache()));
            outState.putString("background", Methody.GetRGB(backgroundColor.getDrawingCache()));
            textColor.setDrawingCacheEnabled(false);
            backgroundColor.setDrawingCacheEnabled(false);
        }
        catch (Exception ignore){
        }
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_data_for_poznamky);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        id = getIntent().getExtras().getString("id");
        DatabaOperations databaOperations = new DatabaOperations(this);
        final Cursor cursor = databaOperations.GetID_Data(id);
        cursor.moveToFirst();
        final EditText txtText = (EditText) findViewById(R.id.txtMax);
        txtText.setText(cursor.getString(6));
        final EditText size = (EditText) findViewById(R.id.txtSize);
        size.setText(cursor.getString(9));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (size.getWidth() ==0){
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textColor = (ImageView) findViewById(R.id.imgTextColor);
                        backgroundColor = (ImageView) findViewById(R.id.imgBackgroundColor);
                        try {
                            if (savedInstanceState == null)
                                textColor.setImageBitmap(Methody.SetColor(cursor, 7, textColor.getWidth(), textColor.getHeight()));
                            else
                                textColor.setImageBitmap(Methody.SetColor(savedInstanceState.getString("text"), textColor.getWidth(), textColor.getHeight()));
                            if (savedInstanceState == null)
                                backgroundColor.setImageBitmap(Methody.SetColor(cursor, 8, backgroundColor.getWidth(), backgroundColor.getHeight()));
                            else
                                backgroundColor.setImageBitmap(Methody.SetColor(savedInstanceState.getString("background"), backgroundColor.getWidth(), backgroundColor.getHeight()));
                        }
                            catch (Exception ignore){
                        }
                    }
                });
            }
        });
        thread.start();
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtText.getText().toString().length() !=0 && size.getText().toString().length() !=0) {
                    DatabaOperations databaOperations = new DatabaOperations(Set_Item_Data_Poznamka.this);
                    textColor.setDrawingCacheEnabled(true);
                    backgroundColor.setDrawingCacheEnabled(true);
                    databaOperations.UpdatePoznamky(id, txtText.getText().toString(), Methody.GetRGB(textColor.getDrawingCache()), Methody.GetRGB(backgroundColor.getDrawingCache()), size.getText().toString());
                    setResult(RESULT_OK);
                    textColor.setDrawingCacheEnabled(false);
                    backgroundColor.setDrawingCacheEnabled(false);
                    finish();
                    overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
                }
                else
                    Toast.makeText(getApplicationContext(), R.string.one_value_has_not_been_set,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void StartSettingColorButton(View view){
        Intent intent = new Intent(Set_Item_Data_Poznamka.this,ColorPicker.class);
        view.setDrawingCacheEnabled(true);
        intent.putExtra("BITMAP", Methody.GetRGB(view.getDrawingCache()));
        intent.putExtra("ID", view.getId());
        view.setDrawingCacheEnabled(false);
        startActivityForResult(intent, Konstanty.COLOR_SET);
        overridePendingTransition(R.animator.set_control_right_in,R.animator.set_control_right_out);

    }
}
