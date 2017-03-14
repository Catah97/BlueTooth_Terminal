package com.example.martin.bluetooth_terminal.SetDatabase.Settings;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
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
 * Created by Martin on 17.10.2015.
 */
public class Set_Item_Data_Button extends AppCompatActivity {

    boolean save;
    String id;
    ImageView textColor,backgroundColor;


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
        getMenuInflater().inflate(R.menu.menu_set_item,menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                  /**zobrazí iconu sipku zpet*/
        actionBar.setHomeButtonEnabled(true);                       /**nastaví iconu eneble pro click*/
        actionBar.setTitle(R.string.setting_for_button);
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
                Intent information = new Intent(this,Information.class);
                information.putExtra("item",Konstanty.BUTTON);
                startActivity(information);
                overridePendingTransition(R.animator.set_control_right_in,R.animator.set_control_right_out);
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
            WaringDialog.Dialog(this, handler,getString(R.string.would_you_like_continue_without_save));
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
        setContentView(R.layout.set_data_for_button);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        id = getIntent().getExtras().getString("id");
        DatabaOperations databaOperations = new DatabaOperations(this);
        final Cursor cursor = databaOperations.GetID_Data(id);
        cursor.moveToFirst();
        final EditText data = (EditText) findViewById(R.id.txtSize);
        try {
            String dataTosend = cursor.getString(5);
            String finalString = dataTosend.substring(1, 5) + " " + dataTosend.substring(5);
            data.setText(finalString);
        }
        catch (Exception ignore){
        }
        /** k určení aby bylo jasno u kterého Textu opravovat (Jelikoz na teto obrayovce je jenom jeden text tak se muye definovat rovnou)*/
        Methody.txt = data;
        data.addTextChangedListener(new Methody());
        final EditText txtText = (EditText) findViewById(R.id.txtMax);
        txtText.setText(cursor.getString(6));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (txtText.getHeight() == 0){
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
                        try{
                        if (savedInstanceState == null)
                            textColor.setImageBitmap(Methody.SetColor(cursor, 7,textColor.getWidth(),textColor.getHeight()));
                        else
                            textColor.setImageBitmap(Methody.SetColor(savedInstanceState.getString("text"),textColor.getWidth(),textColor.getHeight()));
                        if (savedInstanceState == null)
                            backgroundColor.setImageBitmap(Methody.SetColor(cursor, 8,textColor.getWidth(),textColor.getHeight()));
                        else
                            backgroundColor.setImageBitmap(Methody.SetColor(savedInstanceState.getString("background"),textColor.getWidth(),textColor.getHeight()));
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
                if (!Methody.CheckString(data.getEditableText().toString()).equals("NIC") && txtText.getEditableText().length() != 0) {
                    DatabaOperations databaOperations = new DatabaOperations(Set_Item_Data_Button.this);
                    textColor.setDrawingCacheEnabled(true);
                    backgroundColor.setDrawingCacheEnabled(true);
                    databaOperations.UpdateButton(id, Methody.SetData(data.getEditableText().toString()), txtText.getEditableText().toString(), Methody.GetRGB(textColor.getDrawingCache()), Methody.GetRGB(backgroundColor.getDrawingCache()));
                    textColor.setDrawingCacheEnabled(false);
                    backgroundColor.setDrawingCacheEnabled(false);
                    setResult(RESULT_OK);
                    finish();
                    overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
                }
                else
                    Toast.makeText(getApplicationContext(), R.string.one_value_has_incorect_setting,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void StartSettingColorButton(View view){
        Intent intent = new Intent(Set_Item_Data_Button.this, ColorPicker.class);
        view.setDrawingCacheEnabled(true);
        intent.putExtra("BITMAP", Methody.GetRGB(view.getDrawingCache()));
        intent.putExtra("ID", view.getId());
        view.setDrawingCacheEnabled(false);
        startActivityForResult(intent, Konstanty.COLOR_SET);
        overridePendingTransition(R.animator.set_control_right_in,R.animator.set_control_right_out);

    }


}
