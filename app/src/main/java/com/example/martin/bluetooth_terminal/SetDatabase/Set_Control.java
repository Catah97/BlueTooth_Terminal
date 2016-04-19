package com.example.martin.bluetooth_terminal.SetDatabase;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.Database.DatabaseBackUp;
import com.example.martin.bluetooth_terminal.Database.DatabaseFirstRun;
import com.example.martin.bluetooth_terminal.Dialogs.DatabaseCreating;
import com.example.martin.bluetooth_terminal.Dialogs.ErrorDialog;
import com.example.martin.bluetooth_terminal.Dialogs.WaringDialog;
import com.example.martin.bluetooth_terminal.Draw.DrawPlyn;
import com.example.martin.bluetooth_terminal.Draw.DrawVolant;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Methody;
import com.example.martin.bluetooth_terminal.Other.Save_Load;
import com.example.martin.bluetooth_terminal.R;
import com.example.martin.bluetooth_terminal.SetDatabase.DragAndDrop.DragAndDrop;
import com.example.martin.bluetooth_terminal.SetDatabase.DragAndDrop.MyDragShadowBuilder;
import com.example.martin.bluetooth_terminal.SetDatabase.Settings.Set_Item_Data_Button;
import com.example.martin.bluetooth_terminal.SetDatabase.Settings.Set_Item_Data_Plyn;
import com.example.martin.bluetooth_terminal.SetDatabase.Settings.Set_Item_Data_Posuvnik;
import com.example.martin.bluetooth_terminal.SetDatabase.Settings.Set_Item_Data_Poznamka;
import com.example.martin.bluetooth_terminal.SetDatabase.Settings.Set_Item_Data_Switch;
import com.example.martin.bluetooth_terminal.SetDatabase.Settings.Set_Item_Data_Volant;

import java.io.IOException;
import java.util.ArrayList;

import static android.view.View.VISIBLE;


/**
 * Created by Martin on 1. 9. 2015.
 * Obrazovka nastavení
 */
public class Set_Control extends AppCompatActivity implements Items_menu.Items_menu_zpetnavazba{

    private DatabaseFirstRun databaseFirstRun;

    private DrawVolant drawVolant1;
    private DrawVolant drawVolant2;
    private DrawVolant drawVolant3;
    private DrawVolant drawVolant4;
    private DrawPlyn drawPlyn1;
    private DrawPlyn drawPlyn2;

    private DragAndDrop dragAndDrop;

    protected static Items_menu items_menu;
    protected static GestureDetector gestureDetector;
    protected static Draw_warning draw_warning;
    protected static DatabaOperations databaOperations;
    protected static DatabaseBackUp databaseBackUp;
    protected static float size, scale;
    protected static ImageView imgThras;
    protected static RelativeLayout waringLayout;
    protected static Vibrator vibrator;
    protected static Toolbar toolbar;
    protected static int id_buffer_layout,BUTTONresquest = 80,PLYNresquest = 81,POZNAMKYreqeust = 82,databaseComlete = 100;


    private boolean firtStart = false, zalohaFaild = false;


    private RelativeLayout rltVolant1,rltVolant2,rltVolant3,rltVolant4,rltPlyn1,rltPlyn2;


    private Thread thread;

    final Handler loading = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == Konstanty.PREPARING_SCREEN_COMPLETE){
                GetDataFromDatabase();
            }
            if (msg.arg1 == Konstanty.LOADING_DATABASE_COMLETE){
                SetClasses();
            }
        }
    };

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == Konstanty.UPDATE){
                finish();
            }
            else if (msg.arg1 == Konstanty.DELETE){
                try {
                    databaseBackUp.Import(Set_Control.this, Konstanty.BACK_UP_NAME, Set_Control.this.getDatabasePath(Konstanty.MAIN_DATABASE_NAME));
                } catch (IOException e) {
                    Log.e("Set_Control","nahrani se nepodarilo :"+e.getMessage());
                }
                finish();
            }
            else if (msg.arg1 ==databaseComlete){
                DatabaseCreating.dialog.dismiss();
                thread.start();
            }

        }
    };


    /**Nemůže být jako onClick listeners protože by se to vyrušoval s Drag and Drop*/
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            int id = dragAndDrop.id_buffer;
            id_buffer_layout = id;
            if (Item_Check.buttonID.contains(id)) {
                Intent intent = new Intent(Set_Control.this, Set_Item_Data_Button.class);
                intent.putExtra("id", String.valueOf(id));
                startActivityForResult(intent, BUTTONresquest);
            }
            if (Item_Check.switchID.contains(id)){
                Intent intent = new Intent(Set_Control.this,Set_Item_Data_Switch.class);
                intent.putExtra("id",String.valueOf(id));
                startActivityForResult(intent,1);
            }
            if (Item_Check.volantRltID.contains(id)) {
                Intent intent = new Intent(Set_Control.this, Set_Item_Data_Volant.class);
                intent.putExtra("id", String.valueOf(id));
                startActivityForResult(intent,1);
            }
            if (Item_Check.plynRltID.contains(id)) {
                Intent intent = new Intent(Set_Control.this, Set_Item_Data_Plyn.class);
                intent.putExtra("id", String.valueOf(id));
                startActivityForResult(intent, PLYNresquest);
            }
            if (Item_Check.poznamkyID.contains(id)){
                Intent intent = new Intent(Set_Control.this, Set_Item_Data_Poznamka.class);
                intent.putExtra("id", String.valueOf(id));
                startActivityForResult(intent, POZNAMKYreqeust);
            }
            if (Item_Check.posouvacID.contains(id)){
                Intent intent = new Intent(Set_Control.this, Set_Item_Data_Posuvnik.class);
                intent.putExtra("id", String.valueOf(id));
                startActivityForResult(intent, 1);
            }
            overridePendingTransition(R.animator.set_control_right_in,R.animator.set_control_right_out);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == BUTTONresquest){
            Cursor cursor = databaOperations.GetID_Data(String.valueOf(id_buffer_layout));
            cursor.moveToFirst();
            SetDataForButton(cursor);
        }
        if (resultCode == RESULT_OK && requestCode == POZNAMKYreqeust){
            Cursor cursor = databaOperations.GetID_Data(String.valueOf(id_buffer_layout));
            cursor.moveToFirst();
            SetDataForPoznamky(cursor);
        }
        if (resultCode == RESULT_OK && requestCode == PLYNresquest){
            SetPlynViewClass(id_buffer_layout);
        }
        ChekcDatabase();
        draw_warning.invalidate();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_table, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (!items_menu.isDrawerOpen())
                    items_menu.Open();
                else
                    items_menu.Close();
                break;
            case R.id.save:
                End();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            End();
        }
        if (keyCode == KeyEvent.KEYCODE_HOME && event.getAction() == KeyEvent.ACTION_DOWN){
            End();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void End(){
        if (ChekcDatabase()) {
            ErrorDialog.Dialog(this);
            draw_warning.invalidate();
        } else {
            setResult(RESULT_OK);
            if (!firtStart) {
                WaringDialog.Dialog(this, handler, "Přejete se uložit změny?");
            }
            else
                finish();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        /**Zálohování databáze*/
        try {
            DatabaseBackUp.Export(this, this.getDatabasePath(Konstanty.MAIN_DATABASE_NAME), Konstanty.BACK_UP_NAME);
        } catch (IOException e) {
            Log.e("Set_Control", "zaloha se nepovedla: " + e.getMessage());
            zalohaFaild = true;
        }
        /**pokud se chce uživatel vrátit do applikace a náhodou byla zrušena promená tableList*/
        if (Konstanty.TABLE_NAME.equals("")) {
            String tablesNames = Save_Load.LoadedData(Set_Control.this, Konstanty.TABLES_FILE);
            String orientation = Save_Load.LoadedData(Set_Control.this, Konstanty.ORIENTATION_FILE);
            if (tablesNames != null) {
                String[] seznamTabulek = tablesNames.split(";");
                String[] seznamOrientation = orientation.split(";");
                Konstanty.TABLE_NAME = seznamTabulek[0];
                Konstanty.TABLE_ORIENTATION = seznamOrientation[0];
            }
        }
        if (Konstanty.TABLE_ORIENTATION.equals("portail"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        scale = this.getResources().getDisplayMetrics().density;
        size = Konstanty.size;
        size = size * scale;
        setContentView(R.layout.set_table);
        /**actionbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                  /**zobrazí iconu sipku zpet*/
        actionBar.setHomeButtonEnabled(true);                       /**nastaví iconu eneble pro click*/
        actionBar.setTitle("Nastavení pro: "+Konstanty.TABLE_NAME);
        /**konec actionbar*/
        items_menu = (Items_menu) getSupportFragmentManager().findFragmentById(R.id.item_drawer);
        items_menu.setUp(R.id.item_drawer, (DrawerLayout) findViewById(R.id.item_menu));
        draw_warning = new Draw_warning(this);
        databaOperations = new DatabaOperations(this);
        waringLayout = (RelativeLayout) findViewById(R.id.WaringLayout);
        waringLayout.addView(draw_warning);
        rltVolant1 = (RelativeLayout) findViewById(R.id.rltLayoutVolant1);
        rltVolant2 = (RelativeLayout) findViewById(R.id.rltLayoutVolant2);
        rltVolant3 = (RelativeLayout) findViewById(R.id.rltLayoutVolant3);
        rltVolant4 = (RelativeLayout) findViewById(R.id.rltLayoutVolant4);
        rltPlyn1 = (RelativeLayout) findViewById(R.id.rltLayoutPlyn1);
        rltPlyn2 = (RelativeLayout) findViewById(R.id.rltLayoutPlyn2);

        final SwitchCompat switch1 = (SwitchCompat) findViewById(R.id.switch1);
        final SwitchCompat switch2 = (SwitchCompat) findViewById(R.id.switch2);
        final SwitchCompat switch3 = (SwitchCompat) findViewById(R.id.switch3);
        final SwitchCompat switch4 = (SwitchCompat) findViewById(R.id.switch4);
        final SwitchCompat switch5 = (SwitchCompat) findViewById(R.id.switch5);
        final SwitchCompat switch6 = (SwitchCompat) findViewById(R.id.switch6);
        final SwitchCompat switch7 = (SwitchCompat) findViewById(R.id.switch7);
        final SwitchCompat switch8 = (SwitchCompat) findViewById(R.id.switch8);
        final SwitchCompat switch9 = (SwitchCompat) findViewById(R.id.switch9);
        final SwitchCompat switch10 = (SwitchCompat) findViewById(R.id.switch10);

        final Button button1 = (Button) findViewById(R.id.button1);
        final Button button2 = (Button) findViewById(R.id.button2);
        final Button button3 = (Button) findViewById(R.id.button3);
        final Button button4 = (Button) findViewById(R.id.button4);
        final Button button5 = (Button) findViewById(R.id.button5);
        final Button button6 = (Button) findViewById(R.id.button6);
        final Button button7 = (Button) findViewById(R.id.button7);
        final Button button8 = (Button) findViewById(R.id.button8);
        final Button button9 = (Button) findViewById(R.id.button9);
        final Button button10 = (Button) findViewById(R.id.button10);
        final Button button11 = (Button) findViewById(R.id.button11);
        final Button button12 = (Button) findViewById(R.id.button12);
        final Button button13 = (Button) findViewById(R.id.button13);
        final Button button14 = (Button) findViewById(R.id.button14);
        final Button button15 = (Button) findViewById(R.id.button15);
        final Button button16 = (Button) findViewById(R.id.button16);
        final Button button17 = (Button) findViewById(R.id.button17);
        final Button button18 = (Button) findViewById(R.id.button18);
        final Button button19 = (Button) findViewById(R.id.button19);
        final Button button20 = (Button) findViewById(R.id.button20);

        final SeekBar seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        final SeekBar seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        final SeekBar seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
        final SeekBar seekBar4 = (SeekBar) findViewById(R.id.seekBar4);
        final SeekBar seekBar5 = (SeekBar) findViewById(R.id.seekBar5);
        final SeekBar seekBar6 = (SeekBar) findViewById(R.id.seekBar6);
        final SeekBar seekBar7 = (SeekBar) findViewById(R.id.seekBar7);
        final SeekBar seekBar8 = (SeekBar) findViewById(R.id.seekBar8);
        final SeekBar seekBar9 = (SeekBar) findViewById(R.id.seekBar9);
        final SeekBar seekBar10 = (SeekBar) findViewById(R.id.seekBar10);

        final TextView txt = (TextView) findViewById(R.id.txt0);
        final TextView txt1 = (TextView) findViewById(R.id.txt1);
        final TextView txt2 = (TextView) findViewById(R.id.txt2);
        final TextView txt3 = (TextView) findViewById(R.id.txt3);
        final TextView txt4 = (TextView) findViewById(R.id.txt4);
        final TextView txt5 = (TextView) findViewById(R.id.txt5);
        final TextView txt6 = (TextView) findViewById(R.id.txt6);
        final TextView txt7 = (TextView) findViewById(R.id.txt7);
        final TextView txt8 = (TextView) findViewById(R.id.txt8);
        final TextView txt9 = (TextView) findViewById(R.id.txt9);
        final TextView txt10 = (TextView) findViewById(R.id.txt10);
        final TextView txt11 = (TextView) findViewById(R.id.txt11);
        final TextView txt12 = (TextView) findViewById(R.id.txt12);
        final TextView txt13 = (TextView) findViewById(R.id.txt13);
        final TextView txt14 = (TextView) findViewById(R.id.txt14);

        if (getIntent().getExtras().getBoolean("firstStart")) {
            DatabaseCreating.Dialog(Set_Control.this, getLayoutInflater());
            ArrayList<Integer> list = new ArrayList<>();
            list.add(rltVolant1.getId());
            list.add(rltVolant2.getId());
            list.add(rltVolant3.getId());
            list.add(rltVolant4.getId());
            list.add(rltPlyn1.getId());
            list.add(rltPlyn2.getId());

            list.add(switch1.getId());
            list.add(switch2.getId());
            list.add(switch3.getId());
            list.add(switch4.getId());
            list.add(switch5.getId());
            list.add(switch6.getId());
            list.add(switch7.getId());
            list.add(switch8.getId());
            list.add(switch9.getId());
            list.add(switch10.getId());

            list.add(button1.getId());
            list.add(button2.getId());
            list.add(button3.getId());
            list.add(button4.getId());
            list.add(button5.getId());
            list.add(button6.getId());
            list.add(button7.getId());
            list.add(button8.getId());
            list.add(button9.getId());
            list.add(button10.getId());
            list.add(button11.getId());
            list.add(button12.getId());
            list.add(button13.getId());
            list.add(button14.getId());
            list.add(button15.getId());
            list.add(button16.getId());
            list.add(button17.getId());
            list.add(button18.getId());
            list.add(button19.getId());
            list.add(button20.getId());

            list.add(seekBar1.getId());
            list.add(seekBar2.getId());
            list.add(seekBar3.getId());
            list.add(seekBar4.getId());
            list.add(seekBar5.getId());
            list.add(seekBar6.getId());
            list.add(seekBar7.getId());
            list.add(seekBar8.getId());
            list.add(seekBar9.getId());
            list.add(seekBar10.getId());

            list.add(txt.getId());
            list.add(txt1.getId());
            list.add(txt2.getId());
            list.add(txt3.getId());
            list.add(txt4.getId());
            list.add(txt5.getId());
            list.add(txt6.getId());
            list.add(txt7.getId());
            list.add(txt8.getId());
            list.add(txt9.getId());
            list.add(txt10.getId());
            list.add(txt11.getId());
            list.add(txt12.getId());
            list.add(txt13.getId());
            list.add(txt14.getId());
            databaseFirstRun = new DatabaseFirstRun(Set_Control.this, handler);
            databaseFirstRun.execute(list);
            firtStart = true;
        }
        imgThras = (ImageView) findViewById(R.id.imgKos);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (Set_Control.this) {
                    while (true) {
                        if (imgThras.getHeight() != 0) {
                            break;
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Methody.SetKonstant(size, waringLayout.getWidth(), waringLayout.getHeight());

                            dragAndDrop = new DragAndDrop(Set_Control.this, (GridLayout) findViewById(R.id.GridLayout), loading);

                            rltPlyn1.setOnTouchListener(dragAndDrop);
                            rltPlyn2.setOnTouchListener(dragAndDrop);

                            switch1.setOnTouchListener(dragAndDrop);
                            switch2.setOnTouchListener(dragAndDrop);
                            switch3.setOnTouchListener(dragAndDrop);
                            switch4.setOnTouchListener(dragAndDrop);
                            switch5.setOnTouchListener(dragAndDrop);
                            switch6.setOnTouchListener(dragAndDrop);
                            switch7.setOnTouchListener(dragAndDrop);
                            switch8.setOnTouchListener(dragAndDrop);
                            switch9.setOnTouchListener(dragAndDrop);
                            switch10.setOnTouchListener(dragAndDrop);


                            button1.setOnTouchListener(dragAndDrop);
                            button2.setOnTouchListener(dragAndDrop);
                            button3.setOnTouchListener(dragAndDrop);
                            button4.setOnTouchListener(dragAndDrop);
                            button5.setOnTouchListener(dragAndDrop);
                            button6.setOnTouchListener(dragAndDrop);
                            button7.setOnTouchListener(dragAndDrop);
                            button8.setOnTouchListener(dragAndDrop);
                            button9.setOnTouchListener(dragAndDrop);
                            button10.setOnTouchListener(dragAndDrop);
                            button13.setOnTouchListener(dragAndDrop);
                            button14.setOnTouchListener(dragAndDrop);
                            button15.setOnTouchListener(dragAndDrop);
                            button16.setOnTouchListener(dragAndDrop);
                            button17.setOnTouchListener(dragAndDrop);
                            button18.setOnTouchListener(dragAndDrop);
                            button19.setOnTouchListener(dragAndDrop);
                            button20.setOnTouchListener(dragAndDrop);

                            seekBar1.setOnTouchListener(dragAndDrop);
                            seekBar2.setOnTouchListener(dragAndDrop);
                            seekBar3.setOnTouchListener(dragAndDrop);
                            seekBar4.setOnTouchListener(dragAndDrop);
                            seekBar5.setOnTouchListener(dragAndDrop);
                            seekBar6.setOnTouchListener(dragAndDrop);
                            seekBar7.setOnTouchListener(dragAndDrop);
                            seekBar8.setOnTouchListener(dragAndDrop);
                            seekBar9.setOnTouchListener(dragAndDrop);
                            seekBar10.setOnTouchListener(dragAndDrop);

                            txt.setOnTouchListener(dragAndDrop);
                            txt1.setOnTouchListener(dragAndDrop);
                            txt2.setOnTouchListener(dragAndDrop);
                            txt3.setOnTouchListener(dragAndDrop);
                            txt4.setOnTouchListener(dragAndDrop);
                            txt5.setOnTouchListener(dragAndDrop);
                            txt6.setOnTouchListener(dragAndDrop);
                            txt7.setOnTouchListener(dragAndDrop);
                            txt8.setOnTouchListener(dragAndDrop);
                            txt9.setOnTouchListener(dragAndDrop);
                            txt10.setOnTouchListener(dragAndDrop);
                            txt11.setOnTouchListener(dragAndDrop);
                            txt12.setOnTouchListener(dragAndDrop);
                            txt13.setOnTouchListener(dragAndDrop);
                            txt14.setOnTouchListener(dragAndDrop);

                            toolbar.setOnDragListener(dragAndDrop);
                            imgThras.setOnDragListener(dragAndDrop);

                            /**nastaveni marign aby byl videt stin ale zaroven neblokovl astatni prvky*/
                            LinearLayout actionBar = (LinearLayout) findViewById(R.id.action_bar);
                            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(actionBar.getLayoutParams());
                            params.setMargins(0,0,0,(int) (findViewById(R.id.mainLayout).getHeight()-5*scale));
                            actionBar.setLayoutParams(params);
                        }
                    });
                }
            }
        });
        if (!firtStart) {
            thread.start();
        }
    }




    private boolean ChekcDatabase() {
        boolean fail = false;
        Cursor cursor = databaOperations.GetData();
        while (cursor.moveToNext()){
            int id = Integer.parseInt(cursor.getString(0));
            if (cursor.getString(5) == null && cursor.getString(4).equals("VISIBLE") && !cursor.getString(1).equals(Konstanty.POZNAMKY)) {
                fail = true;
                View v = findViewById(id);
                draw_warning.x.add(v.getX());
                draw_warning.y.add(v.getY());
            }
        }
        return fail;
    }


    protected synchronized void GetDataFromDatabase(){

        /**
         * Na pozici 0 je id položky
         * Na pozici 1 je informace o jaky druh itemu se jedna
         * na pozici 2 je X souradnice
         * na pozici 3 je y souradnice
         * na pozici 4 je viditelnost
         * na pozici 5 je hodnota která se odesílá u tlačitka
         *             je hodnota pro rovný smer volant
         *             je nulova pozice u Plynu
         *             je max hbodnota u posuvniku
         *
         * na pozici 6 je text u tlacitko
         *             je barva hranic posuvniku
         * na pozici 7 je barva pro text u tlacitko
         *             je barva posunu u posuvniku
         * na pozici 8 je barva pro pozadi
         *
         * na pozici 9 je hodnota pro zatačeni VLEVO u volantu (první stupen)
         *              je hodnota pro 1. stupně u posuvníku
         * na pozici 10 je hodnota pro zatačení VLEVO u volantu (druhy stupen)
         *              je hodnota pro 2. stupně u posuvníku
         * na pozici 11 je hodnota pro zataceni VLEVO u volantu (treti stupen)
         *              je hodnota pro 3. stupně u posuvníku
         * na pozici 12 je hodnota pro zatečení VPRAVO u volantu (prvni stupen)
         *              je hodnota pro 4. stupně u posuvníku
         * na pozici 13 je hodnota pro zatačeni VPRAVO u volantu (druhy stupen)
         *              je hodnota pro 5. stupně u posuvníku
         * na pozici 14 je hodnota pro zatačení VPRAVO u volantu (treti stupen)
         *
         * na pozici 15 je hodnota určující jeslti se mají data posílat furt nebo jenom pri zmene
         * pokud jenom při zmene je nastavená na false pokud porad pak je na true
         */

        Cursor cursor = databaOperations.GetData();
        if (cursor.getCount() ==0){
            Log.e("null","zadny zanma");
            return ;
        }
        /**čistění listu s neviditelnými ID*/
        Item_Check.invisibleBtnID.clear();
        Item_Check.buttonID.clear();
        Item_Check.invisibleSwitch.clear();
        Item_Check.switchID.clear();
        Item_Check.invisibleVolantRlt.clear();
        Item_Check.volantRltID.clear();
        Item_Check.invisiblePlynRlt.clear();
        Item_Check.plynRltID.clear();
        Item_Check.invisiblePoznamkyID.clear();
        Item_Check.poznamkyID.clear();
        Item_Check.invisiblePosouvacID.clear();
        Item_Check.posouvacID.clear();
        while (cursor.moveToNext()) {
            float x, y;
            if (cursor.getString(2) == null)
                x = Konstanty.NULL;
            else
                x = (cursor.getFloat(2));
            if (cursor.getString(3) == null)
                y = Konstanty.NULL;
            else
                y = cursor.getFloat(3);
            if (cursor.getString(1).equals(Konstanty.VOLANT))
                Item_Check.volantRltID.add(Integer.parseInt(cursor.getString(0)));
            else if (cursor.getString(1).equals(Konstanty.PLYN))
                Item_Check.plynRltID.add(Integer.parseInt(cursor.getString(0)));
            else if (cursor.getString(1).equals(Konstanty.SWITCH))
                Item_Check.switchID.add(Integer.parseInt(cursor.getString(0)));
            else if (cursor.getString(1).equals(Konstanty.BUTTON))
                Item_Check.buttonID.add(Integer.parseInt(cursor.getString(0)));
            else if (cursor.getString(1).equals(Konstanty.SEEK_BAR))
                Item_Check.posouvacID.add(Integer.parseInt(cursor.getString(0)));
            else if (cursor.getString(1).equals(Konstanty.POZNAMKY))
                Item_Check.poznamkyID.add(Integer.parseInt(cursor.getString(0)));
            try {
                Log.e("Database", cursor.getString(0) + " " + cursor.getString(1));
            } catch (Exception ignore) {

            }

            SetParams(Integer.parseInt(cursor.getString(0)), x, y, cursor.getString(4));
            if (cursor.getString(1).equals(Konstanty.BUTTON) && cursor.getString(4).equals("VISIBLE")) {
                SetDataForButton(cursor);
            }
            if (cursor.getString(1).equals(Konstanty.POZNAMKY) && cursor.getString(4).equals("VISIBLE")) {
                SetDataForPoznamky(cursor);
            }
        }
        Message msg = new Message();
        msg.arg1 = Konstanty.LOADING_DATABASE_COMLETE;
        loading.sendMessage(msg);
    }
    private void SetClasses(){
        SetVolantViewClass(rltVolant1.getId());
        SetVolantViewClass(rltVolant2.getId());
        SetVolantViewClass(rltVolant3.getId());
        SetVolantViewClass(rltVolant4.getId());


        SetPlynViewClass(rltPlyn1.getId());
        SetPlynViewClass(rltPlyn2.getId());
    }


    private void SetParams(int id, float x, float y,String visibility){
        View v = findViewById(id);
        if (visibility.equals("INVISIBLE")) {
            v.setVisibility(View.INVISIBLE);
            v.setX(Konstanty.NULL);
            v.setY(Konstanty.NULL);
            Item_Check.InvisibleAddMethod(id);
        }
        else {
           v.setVisibility(VISIBLE);
            v.setX(x);
            v.setY(y);
            if (!Item_Check.poznamkyID.contains(id)) {
                v.setLayoutParams(Methody.GetLayoutParams(id));
                /**psát vždycky o jedno menší než je skutečná velikost objektu*/
                dragAndDrop.id_buffer = id;
                int[] sizes = dragAndDrop.sizes();
                final int width = sizes[0], height = sizes[1];
                dragAndDrop.SetObsazenePozice(x, y, width, height);
            }
        }
    }

    private void SetDataForButton(Cursor cursor){
        Button btn = (Button) findViewById(Integer.parseInt(cursor.getString(0)));
        btn.setText(cursor.getString(6));

        String barvaTextu = cursor.getString(7);
        String[] coloursText = barvaTextu.split(",");
        btn.setTextColor(Color.argb(Integer.parseInt(coloursText[0]), Integer.parseInt(coloursText[1]), Integer.parseInt(coloursText[2]), Integer.parseInt(coloursText[3])));

        String barvaPozadi = cursor.getString(8);
        String[] colourBackground = barvaPozadi.split(",");
        btn.setBackgroundColor(Color.argb(Integer.parseInt(colourBackground[0]), Integer.parseInt(colourBackground[1]), Integer.parseInt(colourBackground[2]), Integer.parseInt(colourBackground[3])));
    }

    private void SetDataForPoznamky(Cursor cursor){
        TextView txt = (TextView) findViewById(Integer.parseInt(cursor.getString(0)));
        txt.setText(cursor.getString(6));

        String barvaTextu = cursor.getString(7);
        String[] coloursText = barvaTextu.split(",");
        txt.setTextColor(Color.argb(Integer.parseInt(coloursText[0]), Integer.parseInt(coloursText[1]), Integer.parseInt(coloursText[2]), Integer.parseInt(coloursText[3])));

        String barvaPozadi = cursor.getString(8);
        String[] colourBackground = barvaPozadi.split(",");
        txt.setBackgroundColor(Color.argb(Integer.parseInt(colourBackground[0]), Integer.parseInt(colourBackground[1]), Integer.parseInt(colourBackground[2]), Integer.parseInt(colourBackground[3])));

        txt.setTextSize(Float.parseFloat(cursor.getString(9)));
    }

    private void SetVolantViewClass(int ID){
        Cursor cursor = databaOperations.GetID_Data(String.valueOf(ID));
        cursor.moveToFirst();
        findViewById(ID).setVisibility(VISIBLE);
        findViewById(ID).setX(Konstanty.NULL);
        findViewById(ID).setY(Konstanty.NULL);
        int position = 0;
        if (ID == rltVolant1.getId())
            position = 1;
        else if (ID == rltVolant2.getId())
            position =2;
        else if (ID == rltVolant3.getId())
            position =3;
        else if (ID == rltVolant4.getId())
            position =4;

        switch (position){
            case 1:

                drawVolant1 = new DrawVolant(Set_Control.this);
                rltVolant1.setOnTouchListener(dragAndDrop);
                rltVolant1.removeAllViews();
                rltVolant1.addView(drawVolant1);
                break;
            case 2:
                drawVolant2 = new DrawVolant(Set_Control.this);
                rltVolant2.setOnTouchListener(dragAndDrop);
                rltVolant2.removeAllViews();
                rltVolant2.addView(drawVolant2);
                break;
            case 3:

                drawVolant3 = new DrawVolant(Set_Control.this);
                rltVolant3.setOnTouchListener(dragAndDrop);
                rltVolant3.removeAllViews();
                rltVolant3.addView(drawVolant3);
                break;
            case 4:
                drawVolant4 = new DrawVolant(Set_Control.this);
                rltVolant4.setOnTouchListener(dragAndDrop);
                rltVolant4.removeAllViews();
                rltVolant4.addView(drawVolant4);
                break;
        }
        if (cursor.getString(4).equals("VISIBLE")){
            findViewById(ID).setVisibility(VISIBLE);
            findViewById(ID).setX(cursor.getFloat(2));
            findViewById(ID).setY(cursor.getFloat(3));
        }
    }

    private void SetPlynViewClass(int ID){
        Cursor cursor = databaOperations.GetID_Data(String.valueOf(ID));
        cursor.moveToFirst();
        String[] blueToothdata = Methody.SetDataForPOsuvni(cursor);
        int position = 0;
        if (ID == rltPlyn1.getId())
            position = 1;
        else if (ID == rltPlyn2.getId())
            position =2;

        switch (position){
            case 1:
                drawPlyn1 =  new DrawPlyn(Set_Control.this, cursor.getString(6), cursor.getString(7), cursor.getString(8), blueToothdata);
                drawPlyn1.firstStart = true;         /**řekne že je jedná o první spuštení jelikož všechny hodnoty byli nastaveny*/
                rltPlyn1.removeAllViews();
                rltPlyn1.addView(drawPlyn1);
                break;
            case 2:
                drawPlyn2 =  new DrawPlyn(Set_Control.this, cursor.getString(6), cursor.getString(7), cursor.getString(8), blueToothdata);
                drawPlyn2.firstStart = true;         /**řekne že je jedná o první spuštení jelikož všechny hodnoty byli nastaveny*/
                rltPlyn2.removeAllViews();
                rltPlyn2.addView(drawPlyn2);
                break;
        }
    }



    @Override
    public void onNavigationItemsItemSelected(int position,float y,float evetX,float evetY) {
        int id = (int)Konstanty.NULL;
        try {
            switch (position) {
                case 0:
                    id = Item_Check.invisibleBtnID.get(0);
                    Item_Check.invisibleBtnID.remove(0);
                    break;
                case 1:
                    id = Item_Check.invisibleSwitch.get(0);
                    Item_Check.invisibleSwitch.remove(0);

                    break;
                case 2:
                    id = Item_Check.invisiblePosouvacID.get(0);
                    Item_Check.invisiblePosouvacID.remove(0);
                    break;
                case 3:
                    id = Item_Check.invisiblePoznamkyID.get(0);
                    Item_Check.invisiblePoznamkyID.remove(0);
                    break;
                case 4:
                    id = Item_Check.invisibleVolantRlt.get(0);
                    Item_Check.invisibleVolantRlt.remove(0);
                    break;
                case 5:
                    id = Item_Check.invisiblePlynRlt.get(0);
                    Item_Check.invisiblePlynRlt.remove(0);
                    break;
            }
            final View v = findViewById(id);
            v.setVisibility(VISIBLE);
            databaOperations.Updata(String.valueOf(v.getId()), v.getX(), v.getY(), "VISIBLE");
            if (!Item_Check.poznamkyID.contains(id)) {
                RelativeLayout.LayoutParams params = Methody.GetLayoutParams(id);
                v.setLayoutParams(params);
            }
            SetClasses();
            if (Item_Check.buttonID.contains(id)) {
                Button btn = (Button) v;
                btn.setText("Text");
                btn.setTextColor(Color.rgb(0, 0, 0));
                btn.setBackgroundColor(Color.rgb(255, 255, 255));
                databaOperations.UpdateButton(String.valueOf(id), Konstanty.NEZMENENO, "Text", "255,0,0,0,", "255,255,255,255");
            }
            else if (Item_Check.poznamkyID.contains(id)){
                TextView txt = (TextView) v;
                txt.setText("Text");
                txt.setTextSize(20);
                txt.setTextColor(Color.rgb(0,0,0));
                txt.setBackgroundColor(Color.argb(0,0,0,0));
                databaOperations.UpdatePoznamky(String.valueOf(id),"Text","255,0,0,0","0,0,0,0","20");

            }
            else if (Item_Check.volantRltID.contains(id)) {

            }
            else if (Item_Check.plynRltID.contains(id)) {
                databaOperations.UpdatePlyn(String.valueOf(id), "255,255,0,0", "255,255,255,0", "255,68,68,68");
                SetPlynViewClass(id);
            }
            else if (Item_Check.posouvacID.contains(id)){
            }
            draw_warning.invalidate();
            imgThras.setVisibility(View.VISIBLE);
            v.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        dragAndDrop.id_buffer = v.getId();
                        dragAndDrop.firstTouch = true;
                        dragAndDrop.SetPosin(v.getWidth()/2, v.getHeight()/2);
                        vibrator.vibrate(100);
                        MyDragShadowBuilder myShadow = new MyDragShadowBuilder(v, v.getWidth()/2, v.getHeight()/2);
                        v.startDrag(null, myShadow, v, 0);
                    } catch (Exception ex) {
                    }
                }
            });
        }
        catch (Exception ignore){
            Toast.makeText(Set_Control.this,"Bohužel víc těchto prvků už přidat nemůžete.",Toast.LENGTH_LONG).show();
        }

    }


}
