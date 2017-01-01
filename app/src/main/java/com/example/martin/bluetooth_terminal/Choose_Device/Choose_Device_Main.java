package com.example.martin.bluetooth_terminal.Choose_Device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.BlueTooth.BlueToothConnection;
import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.Database.DatabaPairedDevices;
import com.example.martin.bluetooth_terminal.Dialogs.ChooseScreenMenu;
import com.example.martin.bluetooth_terminal.Dialogs.ChooseTable;
import com.example.martin.bluetooth_terminal.Dialogs.Database_name;
import com.example.martin.bluetooth_terminal.Dialogs.RenameDevice;
import com.example.martin.bluetooth_terminal.MainScreen;
import com.example.martin.bluetooth_terminal.Dialogs.ConnectionDialog;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Methody;
import com.example.martin.bluetooth_terminal.Other.Save_Load;
import com.example.martin.bluetooth_terminal.R;
import com.example.martin.bluetooth_terminal.SetDatabase.Set_Control;
import com.example.martin.bluetooth_terminal.WelcomeScreen.Welcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.crypto.Mac;

/**
 * Created by Martin on 18.08.2015.
 * Activita, kde si uživatel voli na které zařizení se chce připojit
 */
public class Choose_Device_Main extends AppCompatActivity {

    BlueToothConnection blueToothConnection;

    static MenuItem search_device;
    static Dialog loadingDialog, dialog;

    static boolean welcome;
    final BluetoothAdapter blueTooth = BluetoothAdapter.getDefaultAdapter();
    static ArrayList<HashMap<String, String>> list = new ArrayList<>();
    ArrayList<HashMap<String, String>> savedData = new ArrayList<>();
    DatabaPairedDevices db;
    private static final String jmeno = "jmeno";
    private static final String mac = "mac";

    private BroadcastReceiver blueToothStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == BluetoothAdapter.STATE_ON)
                {
                    Start();
                }
            }
        }
    };

    /**Pokud se zařízení spáruje tento Broadcast zařídí přepnutí zpět do activity*/
    private final BroadcastReceiver mHledam = new BroadcastReceiver() {
        public void onReceive(Context context,Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Intent back = new Intent(Choose_Device_Main.this,Choose_Device_Main.class);
                    back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(back);
                    unregisterReceiver(mHledam);
                }
            }
        }
    };

    String deviceName = "";


    /**Tento handler dostává zpravy z dialogu pro výber ovládání a podle něj vykoná určité kroky*/
    Handler tableHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == Konstanty.UPDATE) {
                StartSetting(false);
                dialog.dismiss();
            }
            if (msg.arg1 == Konstanty.DELETE) {
                dialog.dismiss();
                Toast.makeText(Choose_Device_Main.this, "Smazáno", Toast.LENGTH_SHORT).show();
                String tableNames = Save_Load.LoadedData(Choose_Device_Main.this, Konstanty.TABLES_FILE);
                String orientation = Save_Load.LoadedData(Choose_Device_Main.this,Konstanty.ORIENTATION_FILE);

                String[] seznamTabulek = tableNames.split(";");
                String[] seznamOrientation = orientation.split(";");

                ArrayList<String> listTabulek = new ArrayList<>();
                ArrayList<String> listOrientation = new ArrayList<>();
                for (int i = 0; i < seznamTabulek.length; i++) {
                    listTabulek.add(seznamTabulek[i]);
                    listOrientation.add(seznamOrientation[i]);
                }
                listTabulek.add("Vytvořit nové ovládání");
                dialog = ChooseTable.DialogChooseMenu(Choose_Device_Main.this, tableHandler, listTabulek,listOrientation);
                dialog.show();
                try {
                int dividerID = dialog.getContext().getResources().getIdentifier("titleDivider", "id", "android");
                View titleDivider = dialog.getWindow().getDecorView().findViewById(dividerID);
                titleDivider.setBackgroundColor(Color.BLACK); // change divider color
                }
                catch (Exception ignore){
                }
            }
            if (msg.arg1 == Konstanty.CREATE_NEW_TABLE) {
                /**tabulka neexistuje a je potřeba nastavit jeji jmeno*/
                Database_name.Dialog(getLayoutInflater(),Choose_Device_Main.this,true, tableHandler, false);
                dialog.dismiss();
            }
            /**jmeno je nastaveno ted se musi nastavit databaze*/
            if (msg.arg2 == 100){
                DatabaOperations databaOperations = new DatabaOperations(Choose_Device_Main.this);
                try {
                    databaOperations.CreateTable();
                } catch (Exception e) {
                    Toast.makeText(Choose_Device_Main.this,"Tento název, nemůžete zadat",Toast.LENGTH_LONG).show();
                    return;
                }
                String readData= Save_Load.LoadedData(Choose_Device_Main.this, Konstanty.TABLES_FILE);
                Save_Load.SaveData(Choose_Device_Main.this, readData, Konstanty.TABLE_NAME+";", Konstanty.TABLES_FILE);
                readData = Save_Load.LoadedData(Choose_Device_Main.this,Konstanty.ORIENTATION_FILE);
                Save_Load.SaveData(Choose_Device_Main.this,readData,Konstanty.TABLE_ORIENTATION+";",Konstanty.ORIENTATION_FILE);
                StartSetting(true);
                dialog.dismiss();
            }
            if (msg.arg2 == 200){
                DatabaOperations databaOperations = new DatabaOperations(Choose_Device_Main.this);
                try {
                    databaOperations.CreateTable();
                } catch (Exception e) {
                    Toast.makeText(Choose_Device_Main.this,"Tento název, nemůžete zadat",Toast.LENGTH_LONG).show();
                    /**mažu databazi, aby později nedělala neplechu*/
                    Choose_Device_Main.this.deleteDatabase(Konstanty.MAIN_DATABASE_NAME);
                    return;
                }
                String readData= Save_Load.LoadedData(Choose_Device_Main.this, Konstanty.TABLES_FILE);
                Save_Load.SaveData(Choose_Device_Main.this, readData, Konstanty.TABLE_NAME+";", Konstanty.TABLES_FILE);
                readData = Save_Load.LoadedData(Choose_Device_Main.this,Konstanty.ORIENTATION_FILE);
                Save_Load.SaveData(Choose_Device_Main.this,readData,Konstanty.TABLE_ORIENTATION+";",Konstanty.ORIENTATION_FILE);
                StartSetting(true);
            }
        }
    };
    private void StartSetting(boolean firstStart){
        Intent intent = new Intent(Choose_Device_Main.this, Set_Control.class);
        intent.putExtra("firstStart",firstStart);
        startActivity(intent);
    }


    /**Handler pro rozšířené menu*/
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0){
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  /**slouži k parovani**/
                registerReceiver(mHledam, filter);
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);

            }
            if (msg.arg1 == 1){
                if (!Methody.doesDatabaseExist(Choose_Device_Main.this,Konstanty.MAIN_DATABASE_NAME)) {
                    Database_name.Dialog(getLayoutInflater(),Choose_Device_Main.this,true, tableHandler,true);
                }
                else{
                    String tableNames = Save_Load.LoadedData(Choose_Device_Main.this, Konstanty.TABLES_FILE);
                    String orientation = Save_Load.LoadedData(Choose_Device_Main.this,Konstanty.ORIENTATION_FILE);

                    String[] seznamTabulek = tableNames.split(";");
                    String[] seznamOrientation = orientation.split(";");

                    ArrayList<String> listTabulek = new ArrayList<>();
                    ArrayList<String> listOrientation = new ArrayList<>();
                    for (int i = 0; i < seznamTabulek.length; i++) {
                        listTabulek.add(seznamTabulek[i]);
                        listOrientation.add(seznamOrientation[i]);
                    }
                    listTabulek.add("Vytvořit nové ovládání");
                    dialog = ChooseTable.DialogChooseMenu(Choose_Device_Main.this, tableHandler, listTabulek,listOrientation);
                    dialog.show();
                    try {
                        int dividerID = dialog.getContext().getResources().getIdentifier("titleDivider", "id", "android");
                        View titleDivider = dialog.getWindow().getDecorView().findViewById(dividerID);
                        titleDivider.setBackgroundColor(Color.BLACK); // change divider color
                    }
                    catch (Exception ignore){
                    }
                }

            }

            if (msg.arg1 == Konstanty.BLUETOOTH_ASCYTAS_DONE && msg.arg2 == Konstanty.BLUETOOTH_CONNECTED){
                loadingDialog.dismiss();
                Intent intent = new Intent(Choose_Device_Main.this, MainScreen.class);
                intent.putExtra("device_name",deviceName);
                startActivity(intent);
                return;
            }
            if (msg.arg1 == Konstanty.BLUETOOTH_ASCYTAS_DONE && msg.arg2 == Konstanty.BLUETOOTH_FAIL) {
                Toast.makeText(getApplicationContext(), "Zařízení nebylo připojeno, zkuste to znovu.", Toast.LENGTH_LONG).show();
                try {
                    loadingDialog.dismiss();
                }
                catch (Exception ig){
                }
            }
            if (msg.arg1 == Konstanty.UPDATE){
                Start();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_control, menu);
        search_device = menu.findItem(R.id.option);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { /**Activity result 1 je pro hledani*/
        int id = item.getItemId();
        switch (id)
        {
            case R.id.option:
                String[] list;
                if (!Methody.doesDatabaseExist(Choose_Device_Main.this,Konstanty.MAIN_DATABASE_NAME))
                    list = getResources().getStringArray(R.array.MenuPhotoUncomplete);
                else
                    list = getResources().getStringArray(R.array.MenuPhoto);
                dialog = ChooseScreenMenu.MenuDialog(this, handler,list);
                dialog.show();
                int pixels = (int) (300 * /**scale*/Choose_Device_Main.this.getResources().getDisplayMetrics().density + 0.5f);
                dialog.getWindow().setLayout(pixels, WindowManager.LayoutParams.WRAP_CONTENT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialog));
            alertDialog.setTitle("Varování");
            alertDialog.setMessage("Přejte si ukončit aplikaci?");
            alertDialog.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            alertDialog.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Choose_Device_Main.this.finish();
                    return;
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Konstanty.WELCOME){
            welcome = true;
            Start();
        }
        else if (resultCode == RESULT_CANCELED && requestCode == Konstanty.WELCOME)
            finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Start();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(blueToothStatusListener, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(blueToothStatusListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaPairedDevices(Choose_Device_Main.this);
        /**přesměrování na uvitací obrazovku*/
        if (!welcome) {
            welcome = true;
            Intent intent = new Intent(this, Welcome.class);
            startActivityForResult(intent, Konstanty.WELCOME);
        }
        else{
            Start();
        }
    }


    private void Start(){
        /**Pokud se nepřesměruje na uvodní obrazovku spustí se tato methoda*/
        CreateList();
        setTitle("Vyberte zařízení");
        setContentView(R.layout.choose_device);
        /**actionbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Vyberte zařízení");
        SimpleAdapter listAdapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, new String[]{jmeno, mac}, new int[]{android.R.id.text1, android.R.id.text2});
        ListView lv = (ListView) findViewById(R.id.deviceSeznam);
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (blueTooth.isEnabled()) {
                    blueToothConnection = new BlueToothConnection(handler);
                    deviceName = list.get(position).get(jmeno);
                    loadingDialog = ConnectionDialog.LoadingDialog(deviceName, getLayoutInflater(), Choose_Device_Main.this, blueToothConnection);/**vytvoření dialogu*/
                    String adress = list.get(position).get(mac);
                    blueToothConnection.execute(adress); /**zapnutí background workeru*/
                    loadingDialog.show();
                    loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                                blueToothConnection.cancel(true);
                            }
                            return true;
                        }
                    });
                }
                else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 2);
                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                RenameDevice.Dialog(Choose_Device_Main.this,list.get(position).get(mac),handler);
                return true;
            }
        });
    }

    private void CreateList() {
        list.clear();
        savedData.clear();
        Set<BluetoothDevice> pairedDevices = blueTooth.getBondedDevices();
        if (pairedDevices.size() == 0 ){
            Choose_Device_Main.this.deleteDatabase(Konstanty.PAIRED_DEVICES_DATABASE_NAME);
        }
        else {
            for (BluetoothDevice device : pairedDevices) {
                HashMap<String, String> map = new HashMap<>();
                map.put(mac, device.getAddress());
                String macAdress = "1"+device.getAddress();
                Cursor nameCr = db.GetID_Data(macAdress);
                String name;
                if (nameCr.getCount() == 0){
                    name = device.getName();
                    db.PutData(macAdress,name);
                }
                else {
                    nameCr.moveToFirst();
                    name = nameCr.getString(nameCr.getColumnIndex(Konstanty.DEVICE_NAME));
                }
                map.put(name,name);
                list.add(map);
            }
            SynchozietWithDatabase();
        }
    }

    /**Slouží k nahrání přejmenovaných položek z databáze*/
    private void SynchozietWithDatabase() {
        Cursor cr = db.GetData();
        /**mazání starých položek a přejmenování aktuálních*/
        while (cr.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put(mac, cr.getString(cr.getColumnIndex(Konstanty.DEVICE_MAC)));
            map.put(jmeno, cr.getString(cr.getColumnIndex(Konstanty.DEVICE_NAME)));
            savedData.add(map);
        }
        for (int i = 0; i < savedData.size(); i++) {
            boolean check = false;
            String macAdress = savedData.get(i).get(mac).substring(1);
            if (list.size() != 0) {
                for (int listPOstion = 0; listPOstion < list.size(); listPOstion++) {
                    HashMap<String, String> pairedInformation = list.get(listPOstion);
                    if (macAdress.equals(pairedInformation.get(mac))) {
                        pairedInformation.put(jmeno, savedData.get(i).get(jmeno));
                        check = true;
                        break;
                    }
                }
                if (!check)
                    db.DeleteItem("1"+macAdress);
            }
        }


    }
}

