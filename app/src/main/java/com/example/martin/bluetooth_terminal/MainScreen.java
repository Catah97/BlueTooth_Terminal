package com.example.martin.bluetooth_terminal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.BlueTooth.BlueTooth;
import com.example.martin.bluetooth_terminal.BlueTooth.BlueToothConnection;
import com.example.martin.bluetooth_terminal.BlueTooth.BluetoothBroacastListener;
import com.example.martin.bluetooth_terminal.Controls.Console;
import com.example.martin.bluetooth_terminal.Controls.Control;
import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.Dialogs.ChooseTable;
import com.example.martin.bluetooth_terminal.Dialogs.Database_name;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Save_Load;
import com.example.martin.bluetooth_terminal.SetDatabase.Set_Control;

import java.io.IOException;
import java.util.ArrayList;

/**Created by Martin
 * Tato obrazovka je odsílací obrazovkou, stará se o odesílání dat a přepínáni fragmentů
 */

public class MainScreen extends AppCompatActivity implements Navigation_Menu_Main.NavigationDrawerCallbacks, BlueTooth.BlueToothListener{

    private static final String TAG = "MainScreen";

    Navigation_Menu_Main navigation_menu;
    BlueTooth blueToothSender;

    Fragment control;
    Console console;
    FragmentTransaction fragmentTransaction;
    static boolean consoleRUN,pripojeno;
    public static int width,height;


    private BroadcastReceiver blueToothStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == BluetoothAdapter.STATE_TURNING_OFF)
                {
                    Dissconnect();
                    Toast.makeText(context, "Bluetooth bylo vypnuto", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    Dialog menuDialog;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /**tabulka neexistuje a je potřeba nastavit jeji jmeno*/
            if (msg.arg1 == 100){
                Database_name.Dialog(getLayoutInflater(),
                        new ContextThemeWrapper(MainScreen.this,R.style.CustomDialog),false,handler,true);
            }
            /**jmeno je nastaveno ted se musi nastavit databaze*/
            if (msg.arg2 == 100){
                DatabaOperations databaOperations = new DatabaOperations(MainScreen.this);
                try {
                    databaOperations.CreateTable();
                } catch (Exception e) {
                    Toast.makeText(MainScreen.this,"Tento název, nemůžete zadat", Toast.LENGTH_LONG).show();
                    return;
                }
                String readData= Save_Load.LoadedData(MainScreen.this, Konstanty.TABLES_FILE);
                Save_Load.SaveData(MainScreen.this, readData, Konstanty.TABLE_NAME+";", Konstanty.TABLES_FILE);
                readData = Save_Load.LoadedData(MainScreen.this,Konstanty.ORIENTATION_FILE);
                Save_Load.SaveData(MainScreen.this,readData,Konstanty.TABLE_ORIENTATION+";",Konstanty.ORIENTATION_FILE);
                StartSetting(true);
            }
            /**vola se jenom pri prvn9 spust2ni applikace, protoze databaze se musi prvne vztvorit ney muye byt naplnena*/
            if (msg.arg2 == 200){
                DatabaOperations databaOperations = new DatabaOperations(MainScreen.this);
                try {
                    databaOperations.CreateTable();
                } catch (Exception e) {
                    Toast.makeText(MainScreen.this,"Tento název, nemůžete zadat", Toast.LENGTH_LONG).show();
                    /**mažu databazi, aby později nedělala neplechu*/
                    MainScreen.this.deleteDatabase(Konstanty.MAIN_DATABASE_NAME);
                    return;
                }
                String readData= Save_Load.LoadedData(MainScreen.this, Konstanty.TABLES_FILE);
                Save_Load.SaveData(MainScreen.this, readData, Konstanty.TABLE_NAME+";", Konstanty.TABLES_FILE);
                readData = Save_Load.LoadedData(MainScreen.this,Konstanty.ORIENTATION_FILE);
                Save_Load.SaveData(MainScreen.this,readData,Konstanty.TABLE_ORIENTATION+";",Konstanty.ORIENTATION_FILE);
                StartSetting(true);

            }
            if (msg.arg1 == Konstanty.UPDATE){
                if (Konstanty.TABLE_ORIENTATION.equals("portail"))
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                consoleRUN = false;
                control = new Control();
                fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, control);
                fragmentTransaction.commit();
                navigation_menu.SetAdapter();
                invalidateOptionsMenu();
                menuDialog.dismiss();
            }
            if (msg.arg1 == Konstanty.DELETE){
                menuDialog.dismiss();
                Toast.makeText(MainScreen.this,"Smazáno",Toast.LENGTH_SHORT).show();
                String tableNames = Save_Load.LoadedData(MainScreen.this, Konstanty.TABLES_FILE);
                String orientation = Save_Load.LoadedData(MainScreen.this,Konstanty.ORIENTATION_FILE);

                String[] seznamTabulek = tableNames.split(";");
                String[] seznamOrientation = orientation.split(";");

                ArrayList<String> listTabulek = new ArrayList<>();
                ArrayList<String> listOrientation = new ArrayList<>();
                for (int i = 0; i < seznamTabulek.length; i++) {
                    if (!seznamTabulek[i].equals(Konstanty.TABLE_NAME)) {
                        listTabulek.add(seznamTabulek[i]);
                        listOrientation.add(seznamOrientation[i]);
                    }
                }
                if (listTabulek.size() != 0){
                    menuDialog = ChooseTable.Dialog(MainScreen.this, handler, listTabulek,listOrientation);
                    menuDialog.show();
                    try {
                    int dividerID = menuDialog.getContext().getResources().getIdentifier("titleDivider", "id", "android");
                    View titleDivider = menuDialog.getWindow().getDecorView().findViewById(dividerID);
                    titleDivider.setBackgroundColor(Color.WHITE); // change divider color
                    }
                    catch (Exception ignore){
                    }
                }
                else
                    invalidateOptionsMenu();
            }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                  /**zobrazí iconu sipku zpet*/
        actionBar.setHomeButtonEnabled(true);                       /**nastaví iconu eneble pro click*/
        if (consoleRUN) {
            actionBar.setTitle("Konzole");
            getMenuInflater().inflate(R.menu.menu_console, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_control, menu);
            actionBar.setTitle(Konstanty.TABLE_NAME);
            MenuItem option = menu.findItem(R.id.option);

            String readData = Save_Load.LoadedData(MainScreen.this, Konstanty.TABLES_FILE);
            if (readData != null) {
                String[] seznamTabulek = readData.split(";");
                if (seznamTabulek.length == 1)
                    option.setVisible(false);
                else {
                    option.setVisible(true);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if (!navigation_menu.isDrawerOpen())
                navigation_menu.Open();
            else
                navigation_menu.Close();
        }
        if (item.getItemId() == R.id.option) {
            String tableNames = Save_Load.LoadedData(MainScreen.this, Konstanty.TABLES_FILE);
            String orientation = Save_Load.LoadedData(MainScreen.this,Konstanty.ORIENTATION_FILE);

            String[] seznamTabulek = tableNames.split(";");
            String[] seznamOrientation = orientation.split(";");

            ArrayList<String> listTabulek = new ArrayList<>();
            ArrayList<String> listOrientation = new ArrayList<>();
            for (int i = 0; i < seznamTabulek.length; i++) {
                if (!seznamTabulek[i].equals(Konstanty.TABLE_NAME)) {
                    listTabulek.add(seznamTabulek[i]);
                    listOrientation.add(seznamOrientation[i]);
                }
            }
            if (listTabulek.size() == 0)
                Toast.makeText(this, "Nejsou k dispozici žádné další ovládání.", Toast.LENGTH_SHORT).show();
            else {
                menuDialog = ChooseTable.Dialog(MainScreen.this, handler, listTabulek,listOrientation);
                menuDialog.show();
                try {
                int dividerID = menuDialog.getContext().getResources().getIdentifier("titleDivider", "id", "android");
                View titleDivider = menuDialog.getWindow().getDecorView().findViewById(dividerID);
                titleDivider.setBackgroundColor(Color.WHITE); // change divider color
                }
                catch (Exception ignore){
                }
            }
        }
        if (item.getItemId() == R.id.clear){
            Console.dataOUT.clear();
            Console.dataIN.clear();
            Console.inputStream.setText("");
            Console.outpustStream.setText("");
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialog));
            alertDialog.setTitle("Varování");
            alertDialog.setMessage("Přejte si se odpojit od zařízení?");
            alertDialog.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Dissconnect();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
            try {
            int dividerID = alert.getContext().getResources().getIdentifier("titleDivider", "id", "android");
            View titleDivider = alert.getWindow().getDecorView().findViewById(dividerID);
            titleDivider.setBackgroundColor(Color.WHITE); // change divider color
            }
            catch (Exception ignore){
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Konstanty.SET_CONTROL && resultCode == RESULT_OK){
            consoleRUN = false;
            control = new Control();
            fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, control);
            fragmentTransaction.commit();
            navigation_menu.SetAdapter();
            invalidateOptionsMenu();
            setOrientation();
        }
        if (requestCode == Konstanty.SET_CONTROL && resultCode == RESULT_CANCELED){
            Dissconnect();
        }
    }

    private void setOrientation(){
        if (!consoleRUN) {
            if (Konstanty.TABLE_ORIENTATION.equals("portail"))
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Console.dataOUT == null){
            Console.dataOUT = new ArrayList<>();
        }
        if (Console.dataIN == null){
            Console.dataIN = new ArrayList<>();
        }
        /**připravaní databáze*/
        if (Konstanty.TABLE_NAME.equals("ERROR")) {
            String tablesNames = Save_Load.LoadedData(MainScreen.this, Konstanty.TABLES_FILE);
            String orientation = Save_Load.LoadedData(MainScreen.this, Konstanty.ORIENTATION_FILE);
            if (tablesNames != null) {
                String[] seznamTabulek = tablesNames.split(";");
                String[] seznamOrientation = orientation.split(";");
                Konstanty.TABLE_NAME = seznamTabulek[0];
                Konstanty.TABLE_ORIENTATION = seznamOrientation[0];
            }
        }
        setOrientation();
        SetMemu(getIntent().getExtras().getString("device_name"));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);                       /**zablokování autamitického uspání obrazovky*/
        if (BlueToothConnection.socket == null) {
            Toast.makeText(this,"Zařízení bylo odpojeno", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        blueToothSender = new BlueTooth(BlueToothConnection.socket, this);
        blueToothSender.start();
        final FrameLayout layout = (FrameLayout) findViewById(R.id.container);
        final ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                width = layout.getWidth();
                height = layout.getHeight();
                Control.context = MainScreen.this;
                Control.handler = handler;
                if (consoleRUN){
                    console = new Console();
                    fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, console);
                }
                else {
                    control = new Control();
                    fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, control);
                }
                fragmentTransaction.commit();
                navigation_menu.SetAdapter();
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!BluetoothBroacastListener.connected){
            Dissconnect();
        }
        else {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(blueToothStatusListener, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(blueToothStatusListener);
        }
        catch (IllegalArgumentException ignore){
            //Když neni registrovaný
        }
    }

    private void SetMemu(String deviceName){
        setContentView(R.layout.main_layout);
        navigation_menu.title = deviceName;
        navigation_menu = (Navigation_Menu_Main)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigation_menu.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        navigation_menu.SetAdapter();
    }
    public void onNavigationDrawerItemSelected(int position) {
        Log.e("Position", position + "");
        switch (position) {
            case 0:
                StartSetting(false);
                break;
            case 1:
                Database_name.Dialog(getLayoutInflater(),
                        new ContextThemeWrapper(MainScreen.this,R.style.CustomDialog),false,handler,false);
                break;
            case 2:
                if (!consoleRUN){
                    consoleRUN = true;
                    console = new Console();
                    fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, console);
                    fragmentTransaction.commit();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else{
                    consoleRUN = false;
                    control = new Control();
                    fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.container, control);
                    fragmentTransaction.commit();
                    if (Konstanty.TABLE_ORIENTATION.equals("portail"))
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    else
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }
                navigation_menu.SetAdapter();
                invalidateOptionsMenu();
                break;
            case 3:
                Dissconnect();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        Dissconnect();
    }

    private void Dissconnect(){
        if (BlueToothConnection.socket != null) {
            try {
                blueToothSender.interrupt();
                blueToothSender.join();
                pripojeno = false;
                BlueToothConnection.socket.close();
                Console.dataIN.clear();
                Console.dataOUT.clear();
                finish();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Zařízení se nepodařilo odpojit.", Toast.LENGTH_LONG).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void StartSetting(boolean firstStart){
        Intent intent = new Intent(MainScreen.this, Set_Control.class);
        intent.putExtra("firstStart",firstStart);
        startActivityForResult(intent, Konstanty.SET_CONTROL);
    }

    @Override
    public void sendMessage(Message msg) {

    }

    @Override
    public void incomingBytes(byte bytes) {
        if (consoleRUN){
            console.SetInputText(bytes);
        }
        else {
            Console.SetInputTextStatic(bytes);
        }
    }
}
