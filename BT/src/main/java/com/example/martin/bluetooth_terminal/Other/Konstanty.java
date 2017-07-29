package com.example.martin.bluetooth_terminal.Other;

import android.content.ContextWrapper;
import android.os.Environment;

import java.io.File;

/**
 * Created by Martin on 18.08.2015.
 * Konstanty používám, prakticky všude v programu a využívám je k s přehlednění kodu
 */
public class Konstanty {

    /**pro drag and drop*/
    public final static int size = 30;

    /**po blueTooth*/
    public final static int BLUETOOTH_ASCYTAS_DONE = 999;
    public final static int BLUETOOTH_CONNECTED = 998;
    public final static int BLUETOOTH_FAIL = 997;

    /**intent*/
    public final static int WELCOME = 50;
    public final static int CHOSE_DEVICE = 51;
    public final static int SET_CONTROL = 52;
    public final static int COLOR_SET = 53;

    /**databaze*/
    public final static String BACK_UP_NAME = "backUpDataBase.db";
    public final static String TABLES_FILE = "tablelist.txt";
    public final static String ORIENTATION_FILE = "orientation.txt";
    public final static String ID = "id";
    public final static String ItemType= "item_type";
    public final static String X = "x";
    public final static String Y = "y";
    public final static String VISIBLE = "visibilita";
    public final static String TEXT_HRANICEcolor = "text";
    public final static String TEXT_COLOR_POSUNcolor = "textcolor";
    public final static String BACKGROUND_COLOR = "backgroundcolor";

    public final static String VALUES = "values1";
    public final static String VALUES9 = "values9";
    public final static String VALUES10 = "values10";
    public final static String VALUES11 = "values11";
    public final static String VALUES12 = "values12";
    public final static String VALUES13 = "values13";
    public final static String VALUES14 = "values14";
    public final static String BLUE_TOOTH_SENDING_STILL = "bluetoothsending";
    public final static String BACK_ANIMATION = "backanimation";

    public final static String MAIN_DATABASE_NAME = "database";

    /**databáze spárovených zařízeních*/
    public final static String DEVICE_MAC = "macAdress";
    public final static String DEVICE_NAME = "name";
    public final static String PAIRED_DEVICES = "pairedDevices";
    public final static String PAIRED_DEVICES_DATABASE_NAME = "paireddevices";

    /**pokud poratil potom orientace na výšku, pokud landspace potom orientace tabulkz je šířku*/
    public  static String TABLE_ORIENTATION = "orientation";
    public  static String TABLE_NAME = "ERROR";
    public final static String NEZMENENO = "500505050";
    public final static float NULL = -476000000.27F;

    /**items*/
    public final static String VOLANT = "rltVolant";
    public final static String PLYN = "rltPlyn";
    public final static String SWITCH = "switch";
    public final static String BUTTON = "button";
    public final static String POZNAMKY = "poznamky";
    public final static String SEEK_BAR = "seekBar";

    /**Handler*/
    public final static int CREATE_NEW_TABLE = 4999;
    public final static int UPDATE = 5000;
    public final static int DELETE = 5001;
    public final static int PREPARING_SCREEN_COMPLETE = 5002;
    public final static int LOADING_DATABASE_COMLETE = 5003;
}
