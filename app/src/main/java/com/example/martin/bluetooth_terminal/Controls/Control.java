package com.example.martin.bluetooth_terminal.Controls;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.BlueTooth.BlueTooth;
import com.example.martin.bluetooth_terminal.Database.DatabaOperations;
import com.example.martin.bluetooth_terminal.Dialogs.Database_dont_exist;
import com.example.martin.bluetooth_terminal.Dialogs.Database_name;
import com.example.martin.bluetooth_terminal.Draw.DrawPlyn;
import com.example.martin.bluetooth_terminal.Draw.DrawVolant;
import com.example.martin.bluetooth_terminal.MainScreen;
import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.Other.Methody;
import com.example.martin.bluetooth_terminal.R;
import com.example.martin.bluetooth_terminal.SetDatabase.Item_Check;

import java.util.Timer;

import static android.view.View.VISIBLE;

/**
 * Created by Martin on 1. 9. 2015.
 */
public class Control extends Fragment implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {


    Activity parentActivity;
    DatabaOperations databaOperations;

    DrawVolant drawVolant1;
    DrawVolant drawVolant2;
    DrawVolant drawVolant3;
    DrawVolant drawVolant4;
    DrawPlyn drawPlyn1;
    DrawPlyn drawPlyn2;

    RelativeLayout rltVolant1, rltVolant2, rltVolant3, rltVolant4, rltPlyn1, rltPlyn2;

    float size = Konstanty.size, colums_zbytek, rows_zbytek;

    public static Context context;
    public static Handler handler;
    View rootView;


    /**Timery, které aktualizuji View*/
    Timer timerdrawVolant1, timerdrawVolant2, timerdrawVolant3, timerdrawVolant4, timerdrawPlyn1, timerdrawPlyn2;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        timerdrawVolant1 = new Timer();
        timerdrawVolant2 = new Timer();
        timerdrawVolant3 = new Timer();
        timerdrawVolant4 = new Timer();
        timerdrawPlyn1 = new Timer();
        timerdrawPlyn2 = new Timer();
    }

    @Override
    public void onPause() {
        super.onPause();
        timerdrawVolant1.cancel();
        timerdrawVolant2.cancel();
        timerdrawVolant3.cancel();
        timerdrawVolant4.cancel();
        timerdrawPlyn1.cancel();
        timerdrawPlyn2.cancel();
    }

    Thread drawinvaliderPosuvnik = new Thread();
    Thread drawinvaliderVolant = new Thread();
    Runnable drawinvalideVolant = new Runnable() {
        @Override
        public void run() {
            synchronized (context) {
                while (drawVolant1.waitanime || drawVolant1.anime || drawVolant2.waitanime || drawVolant2.anime
                        || drawVolant3.waitanime || drawVolant3.anime || drawVolant4.waitanime || drawVolant4.anime) {
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            {
                                drawVolant1.invalidate();
                                drawVolant2.invalidate();
                                drawVolant3.invalidate();
                                drawVolant4.invalidate();
                            }
                        }
                    });
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    Runnable drawPosuvnik = new Runnable() {
        @Override
        public void run() {
            synchronized (Control.this) {
                while (drawPlyn1.anime || drawPlyn1.waitAnime || drawPlyn2.anime || drawPlyn2.waitAnime) {
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawPlyn1.invalidate();
                            drawPlyn2.invalidate();
                        }
                    });
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public Control() {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (databaOperations != null) {
            databaOperations.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.control_user, container, false);
        size = size * this.getResources().getDisplayMetrics().density;
        databaOperations = new DatabaOperations(context);
        rltVolant1 = (RelativeLayout) rootView.findViewById(R.id.rltLayoutVolant1);
        rltVolant2 = (RelativeLayout) rootView.findViewById(R.id.rltLayoutVolant2);
        rltVolant3 = (RelativeLayout) rootView.findViewById(R.id.rltLayoutVolant3);
        rltVolant4 = (RelativeLayout) rootView.findViewById(R.id.rltLayoutVolant4);
        rltPlyn1 = (RelativeLayout) rootView.findViewById(R.id.rltLayoutPlyn1);
        rltPlyn2 = (RelativeLayout) rootView.findViewById(R.id.rltLayoutPlyn2);

        final Switch switch1 = (Switch) rootView.findViewById(R.id.switch1);
        final Switch switch2 = (Switch) rootView.findViewById(R.id.switch2);
        final Switch switch3 = (Switch) rootView.findViewById(R.id.switch3);
        final Switch switch4 = (Switch) rootView.findViewById(R.id.switch4);
        final Switch switch5 = (Switch) rootView.findViewById(R.id.switch5);
        final Switch switch6 = (Switch) rootView.findViewById(R.id.switch6);
        final Switch switch7 = (Switch) rootView.findViewById(R.id.switch7);
        final Switch switch8 = (Switch) rootView.findViewById(R.id.switch8);
        final Switch switch9 = (Switch) rootView.findViewById(R.id.switch9);
        final Switch switch10 = (Switch) rootView.findViewById(R.id.switch10);

        final Button button1 = (Button) rootView.findViewById(R.id.button1);
        final Button button2 = (Button) rootView.findViewById(R.id.button2);
        final Button button3 = (Button) rootView.findViewById(R.id.button3);
        final Button button4 = (Button) rootView.findViewById(R.id.button4);
        final Button button5 = (Button) rootView.findViewById(R.id.button5);
        final Button button6 = (Button) rootView.findViewById(R.id.button6);
        final Button button7 = (Button) rootView.findViewById(R.id.button7);
        final Button button8 = (Button) rootView.findViewById(R.id.button8);
        final Button button9 = (Button) rootView.findViewById(R.id.button9);
        final Button button10 = (Button) rootView.findViewById(R.id.button10);
        final Button button11 = (Button) rootView.findViewById(R.id.button11);
        final Button button12 = (Button) rootView.findViewById(R.id.button12);
        final Button button13 = (Button) rootView.findViewById(R.id.button13);
        final Button button14 = (Button) rootView.findViewById(R.id.button14);
        final Button button15 = (Button) rootView.findViewById(R.id.button15);
        final Button button16 = (Button) rootView.findViewById(R.id.button16);
        final Button button17 = (Button) rootView.findViewById(R.id.button17);
        final Button button18 = (Button) rootView.findViewById(R.id.button18);
        final Button button19 = (Button) rootView.findViewById(R.id.button19);
        final Button button20 = (Button) rootView.findViewById(R.id.button20);

        final SeekBar seekBar1 = (SeekBar) rootView.findViewById(R.id.seekBar1);
        final SeekBar seekBar2 = (SeekBar) rootView.findViewById(R.id.seekBar2);
        final SeekBar seekBar3 = (SeekBar) rootView.findViewById(R.id.seekBar3);
        final SeekBar seekBar4 = (SeekBar) rootView.findViewById(R.id.seekBar4);
        final SeekBar seekBar5 = (SeekBar) rootView.findViewById(R.id.seekBar5);
        final SeekBar seekBar6 = (SeekBar) rootView.findViewById(R.id.seekBar6);
        final SeekBar seekBar7 = (SeekBar) rootView.findViewById(R.id.seekBar7);
        final SeekBar seekBar8 = (SeekBar) rootView.findViewById(R.id.seekBar8);
        final SeekBar seekBar9 = (SeekBar) rootView.findViewById(R.id.seekBar9);
        final SeekBar seekBar10 = (SeekBar) rootView.findViewById(R.id.seekBar10);

        /**Slouží pro vytváření mřízky to které jsou umístěny položky*/
        Methody.SetKonstant(size, MainScreen.width, MainScreen.height);
        if (!Methody.doesDatabaseExist(new ContextWrapper(context), Konstanty.MAIN_DATABASE_NAME)) {
            Log.e("database", "dont Exist");
            AlertDialog.Builder dialog = Database_dont_exist.Dialog(context);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Database_name.Dialog(parentActivity.getLayoutInflater(),
                            new ContextThemeWrapper(context,R.style.CustomDialog),false, handler, true);
                    dialog.dismiss();
                }
            });
            Dialog dialog1 = dialog.create();
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.setCancelable(false);
            dialog1.show();

        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (button1.getHeight() != 0)
                            break;
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GetDataFromDatabase();


                        }
                    });
                }
            });
            thread.start();
        }

        switch1.setOnCheckedChangeListener(this);
        switch2.setOnCheckedChangeListener(this);
        switch3.setOnCheckedChangeListener(this);
        switch4.setOnCheckedChangeListener(this);
        switch5.setOnCheckedChangeListener(this);
        switch6.setOnCheckedChangeListener(this);
        switch7.setOnCheckedChangeListener(this);
        switch8.setOnCheckedChangeListener(this);
        switch9.setOnCheckedChangeListener(this);
        switch10.setOnCheckedChangeListener(this);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        button10.setOnClickListener(this);
        button11.setOnClickListener(this);
        button12.setOnClickListener(this);
        button13.setOnClickListener(this);
        button14.setOnClickListener(this);
        button15.setOnClickListener(this);
        button16.setOnClickListener(this);
        button17.setOnClickListener(this);
        button18.setOnClickListener(this);
        button19.setOnClickListener(this);
        button20.setOnClickListener(this);

        seekBar1.setOnSeekBarChangeListener(new ProgresSender());
        seekBar2.setOnSeekBarChangeListener(new ProgresSender());
        seekBar3.setOnSeekBarChangeListener(new ProgresSender());
        seekBar3.setOnSeekBarChangeListener(new ProgresSender());
        seekBar4.setOnSeekBarChangeListener(new ProgresSender());
        seekBar5.setOnSeekBarChangeListener(new ProgresSender());
        seekBar6.setOnSeekBarChangeListener(new ProgresSender());
        seekBar7.setOnSeekBarChangeListener(new ProgresSender());
        seekBar8.setOnSeekBarChangeListener(new ProgresSender());
        seekBar9.setOnSeekBarChangeListener(new ProgresSender());
        seekBar10.setOnSeekBarChangeListener(new ProgresSender());

        return rootView;
    }

    @Override
    public void onClick(View v) {
        DatabaOperations databaOperations = new DatabaOperations(context);
        Cursor cursor = databaOperations.GetID_Data(String.valueOf(v.getId()));
        cursor.moveToFirst();
        if (cursor.getString(5) == null)
            Toast.makeText(context, "Pro tlačítko nebyla nastavena hodnota.", Toast.LENGTH_LONG).show();
        else {
            BlueTooth.Send(cursor.getString(5));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        Cursor cursor = databaOperations.GetID_Data(String.valueOf(id));
        cursor.moveToFirst();
        if (isChecked)
            BlueTooth.Send(cursor.getString(5));
        else
            BlueTooth.Send(cursor.getString(9));
    }

    private void GetDataFromDatabase() {

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
        if (cursor.getCount() == 0) {
            return;
        }
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
            SetPosition(Integer.parseInt(cursor.getString(0)), x, y, cursor.getString(4));

            /**nastaveni pro jednotlive poloyky*/
            if (cursor.getString(1).equals(Konstanty.BUTTON) && cursor.getString(4).equals("VISIBLE")) {
                SetDataForButton(cursor);
            }
            if (cursor.getString(1).equals(Konstanty.POZNAMKY) && cursor.getString(4).equals("VISIBLE")) {
                SetDataForPoznamky(cursor);
            }
            if (cursor.getString(1).equals(Konstanty.SEEK_BAR) && cursor.getString(4).equals("VISIBLE")){
                SetDataForPosouvac(cursor);
            }
            if (cursor.getString(1).equals(Konstanty.VOLANT)) {
                SetVolantViewClass(Integer.parseInt(cursor.getString(0)));
            }
            if (cursor.getString(1).equals(Konstanty.PLYN)) {
                SetPlynViewClass(Integer.parseInt(cursor.getString(0)));
            }
            try {
                Log.e("Database", cursor.getString(0) + " " + cursor.getString(1));
            } catch (Exception ignore) {

            }
        }
    }

    /**Slouží k nastavení pozice pro všechny prvky*/
    private void SetPosition(final int id, final float x, final float y, String visibility) {
        final View view = rootView.findViewById(id);
        if (visibility.equals("INVISIBLE")) {
            view.setVisibility(View.INVISIBLE);
            view.setX(Konstanty.NULL);
            view.setY(Konstanty.NULL);
        } else {
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(VISIBLE);
                    view.setX(x);
                    view.setY(y);
                    if (!Item_Check.poznamkyID.contains(view.getId()))
                        view.setLayoutParams(Methody.GetLayoutParams(id));

                }
            });
        }

    }

    private void SetVolantViewClass(int ID) {
        Cursor cursor = databaOperations.GetID_Data(String.valueOf(ID));
        cursor.moveToFirst();
        int position = 0;
        if (ID == rltVolant1.getId())
            position = 1;
        else if (ID == rltVolant2.getId())
            position = 2;
        else if (ID == rltVolant3.getId())
            position = 3;
        else if (ID == rltVolant4.getId())
            position = 4;


        switch (position) {
            case 1:
                drawVolant1 = new DrawVolant(context,Boolean.parseBoolean(cursor.getString(16)));
                rltVolant1.removeAllViews();
                rltVolant1.addView(drawVolant1);
                rltVolant1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        switch (m.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                drawVolant1.waitanime = true;
                                drawVolant1.anime = false;
                                drawVolant1.x = (int) m.getX();
                                drawVolant1.y = (int) m.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                drawVolant1.waitanime = false;
                                drawVolant1.anime = true;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                drawVolant1.waitanime = true;
                                drawVolant1.x = (int) m.getX();
                                drawVolant1.y = (int) m.getY();
                                break;
                            default:
                        }
                        if (!drawinvaliderVolant.isAlive()) {
                            drawinvaliderVolant = new Thread(drawinvalideVolant);
                            drawinvaliderVolant.start();
                        }
                        return true;
                    }
                });
                break;
            case 2:
                drawVolant2 = new DrawVolant(context,Boolean.parseBoolean(cursor.getString(16)));
                rltVolant2.removeAllViews();
                rltVolant2.addView(drawVolant2);
                rltVolant2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        switch (m.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                drawVolant2.waitanime = true;
                                drawVolant2.anime = false;
                                drawVolant2.x = (int) m.getX();
                                drawVolant2.y = (int) m.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                drawVolant2.waitanime = false;
                                drawVolant2.anime = true;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                drawVolant2.waitanime = true;
                                drawVolant2.x = (int) m.getX();
                                drawVolant2.y = (int) m.getY();
                                break;
                            default:
                        }
                        if (!drawinvaliderVolant.isAlive()) {
                            drawinvaliderVolant = new Thread(drawinvalideVolant);
                            drawinvaliderVolant.start();
                        }
                        return true;
                    }
                });
                break;
            case 3:

                drawVolant3 = new DrawVolant(context,Boolean.parseBoolean(cursor.getString(16)));
                rltVolant3.removeAllViews();
                rltVolant3.addView(drawVolant3);
                rltVolant3.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        switch (m.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                drawVolant3.waitanime = true;
                                drawVolant3.anime = false;
                                drawVolant3.x = (int) m.getX();
                                drawVolant3.y = (int) m.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                drawVolant3.waitanime = false;
                                drawVolant3.anime = true;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                drawVolant3.waitanime = true;
                                drawVolant3.x = (int) m.getX();
                                drawVolant3.y = (int) m.getY();
                                break;
                            default:
                        }
                        if (!drawinvaliderVolant.isAlive()) {
                            drawinvaliderVolant = new Thread(drawinvalideVolant);
                            drawinvaliderVolant.start();
                        }
                        return true;
                    }
                });
                break;
            case 4:
                drawVolant4 = new DrawVolant(context,Boolean.parseBoolean(cursor.getString(16)));
                rltVolant4.removeAllViews();
                rltVolant4.addView(drawVolant4);
                rltVolant4.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        switch (m.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                drawVolant4.waitanime = true;
                                drawVolant4.anime = false;
                                break;
                            case MotionEvent.ACTION_UP:
                                drawVolant4.waitanime = false;
                                drawVolant4.anime = true;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                drawVolant4.waitanime = true;
                                drawVolant4.x = (int) m.getX();
                                drawVolant4.y = (int) m.getY();
                                break;
                            default:
                        }
                        if (!drawinvaliderVolant.isAlive()) {
                            drawinvaliderVolant = new Thread(drawinvalideVolant);
                            drawinvaliderVolant.start();
                        }
                        return true;
                    }
                });
                break;
        }

        /**musíš být nakonci protože využívá class který je inicializován zde*/
        if (cursor.getString(4).equals("VISIBLE")) {
            SetDataForVolant(cursor, position);
        }
    }

    private void SetDataForVolant(Cursor cursor, int pozice) {
        /**
         * Na pozici 0 je id položky
         * Na pozici 1 je informace o jaky druh itemu se jedna
         * na pozici 2 je X souradnice
         * na pozici 3 je y souradnice
         * na pozici 4 je viditelnost
         * na pozici 5 je hodnota která se odesílá u tlačitka
         *             je hodnota pro rovný smer volant
         *             je nulova pozice u POsuvnik
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

        String[] data = {cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(5), cursor.getString(9), cursor.getString(10), cursor.getString(11)};
        boolean stilSending = Boolean.parseBoolean(cursor.getString(15));
        switch (pozice) {
            case 1:
                drawVolant1.poleHodnot = data;
                if (stilSending)
                    timerdrawVolant1.schedule(drawVolant1.sender, 200, 200);
                break;
            case 2:
                drawVolant2.poleHodnot = data;
                if (stilSending)
                    timerdrawVolant2.schedule(drawVolant2.sender, 200, 200);
                break;
            case 3:
                drawVolant3.poleHodnot = data;
                if (stilSending)
                    timerdrawVolant3.schedule(drawVolant3.sender, 200, 200);
                break;
            case 4:
                drawVolant4.poleHodnot = data;
                if (stilSending)
                    timerdrawVolant4.schedule(drawVolant4.sender, 200, 200);
                break;
        }
    }

    private void SetPlynViewClass(int ID) {
        Cursor cursor = databaOperations.GetID_Data(String.valueOf(ID));
        cursor.moveToFirst();
        String[] blueToothdata = Methody.SetDataForPOsuvni(cursor);
        int position = 0;
        if (ID == rltPlyn1.getId())
            position = 1;
        else if (ID == rltPlyn2.getId())
            position = 2;

        switch (position) {
            case 1:
                drawPlyn1 = new DrawPlyn(context, cursor.getString(6), cursor.getString(7), cursor.getString(8), blueToothdata,Boolean.parseBoolean(cursor.getString(16)));
                rltPlyn1.removeAllViews();
                rltPlyn1.addView(drawPlyn1);
                drawPlyn1.firstStart = true;         /**řekne že je jedná o první spuštení jelikož všechny hodnoty byli nastaveny*/
                drawPlyn1.invalidate();
                rltPlyn1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        switch (m.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                drawPlyn1.waitAnime = true;
                                drawPlyn1.anime = false;
                                drawPlyn1.yGet = (int) m.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                drawPlyn1.waitAnime = false;
                                drawPlyn1.anime = true;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                drawPlyn1.waitAnime = true;
                                drawPlyn1.yGet = (int) m.getY();
                                break;
                            default:
                        }
                        if (!drawinvaliderPosuvnik.isAlive()) {
                            drawinvaliderPosuvnik = new Thread(drawPosuvnik);
                            drawinvaliderPosuvnik.start();
                        }
                        return true;
                    }
                });
                if (Boolean.parseBoolean(cursor.getString(15)))
                    timerdrawPlyn1.schedule(drawPlyn1.sender, 200, 200);
                break;
            case 2:
                drawPlyn2 = new DrawPlyn(context, cursor.getString(6), cursor.getString(7), cursor.getString(8), blueToothdata,Boolean.parseBoolean(cursor.getString(16)));
                rltPlyn2.removeAllViews();
                rltPlyn2.addView(drawPlyn2);
                drawPlyn2.firstStart = true;         /**řekne že je jedná o první spuštení jelikož všechny hodnoty byli nastaveny*/
                drawPlyn2.invalidate();
                rltPlyn2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        switch (m.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                drawPlyn2.waitAnime = true;
                                drawPlyn2.anime = false;
                                drawPlyn2.yGet = (int) m.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                drawPlyn2.waitAnime = false;
                                drawPlyn2.anime = true;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                drawPlyn2.waitAnime = true;
                                drawPlyn2.yGet = (int) m.getY();
                                break;
                            default:
                        }
                        if (!drawinvaliderPosuvnik.isAlive()) {
                            drawinvaliderPosuvnik = new Thread(drawPosuvnik);
                            drawinvaliderPosuvnik.start();
                        }
                        return true;
                    }
                });
                if (Boolean.parseBoolean(cursor.getString(15)))
                    timerdrawPlyn2.schedule(drawPlyn2.sender, 200, 200);
                break;
        }
    }
    private void SetDataForPosouvac(Cursor cursor){
        SeekBar seekBar = (SeekBar) rootView.findViewById(Integer.parseInt(cursor.getString(0)));
        seekBar.setMax(Integer.parseInt(cursor.getString(5)));
    }

    private void SetDataForButton(Cursor cursor) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (3 * (size + colums_zbytek)), (int) (2 * (size + rows_zbytek)));
        Button btn = (Button) rootView.findViewById(Integer.parseInt(cursor.getString(0)));
        btn.setLayoutParams(params);
        btn.setText(cursor.getString(6));

        String barvaTextu = cursor.getString(7);
        String[] coloursText = barvaTextu.split(",");
        btn.setTextColor(Color.argb(Integer.parseInt(coloursText[0]), Integer.parseInt(coloursText[1]), Integer.parseInt(coloursText[2]), Integer.parseInt(coloursText[3])));

        String barvaPozadi = cursor.getString(8);
        String[] colourBackground = barvaPozadi.split(",");
        btn.setBackgroundColor(Color.argb(Integer.parseInt(colourBackground[0]), Integer.parseInt(colourBackground[1]), Integer.parseInt(colourBackground[2]), Integer.parseInt(colourBackground[3])));
    }

    private void SetDataForPoznamky(Cursor cursor) {
        TextView txt = (TextView) rootView.findViewById(Integer.parseInt(cursor.getString(0)));
        txt.setText(cursor.getString(6));

        String barvaTextu = cursor.getString(7);
        String[] coloursText = barvaTextu.split(",");
        txt.setTextColor(Color.argb(Integer.parseInt(coloursText[0]), Integer.parseInt(coloursText[1]), Integer.parseInt(coloursText[2]), Integer.parseInt(coloursText[3])));

        String barvaPozadi = cursor.getString(8);
        String[] colourBackground = barvaPozadi.split(",");
        txt.setBackgroundColor(Color.argb(Integer.parseInt(colourBackground[0]), Integer.parseInt(colourBackground[1]), Integer.parseInt(colourBackground[2]), Integer.parseInt(colourBackground[3])));

        txt.setTextSize(Float.parseFloat(cursor.getString(9)));
    }

    private class ProgresSender implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                BlueTooth.Send(seekBar.getProgress());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
