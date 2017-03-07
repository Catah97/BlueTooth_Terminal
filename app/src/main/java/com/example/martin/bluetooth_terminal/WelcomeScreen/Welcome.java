package com.example.martin.bluetooth_terminal.WelcomeScreen;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.Other.Methody;
import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 18.08.2015.
 * Uvodní activita, ověří jeslti je zapnuté bluetooth a když neni tak vyzve uživatele k jeho zapnutí
 */
public class Welcome extends Activity implements Animation.AnimationListener{

    final BluetoothAdapter blueTooth = BluetoothAdapter.getDefaultAdapter();
    TextView text, appName;
    LinearLayout enebleBloueTooth;
    RelativeLayout RLTswipe;

    SwipeView swipeView;

    boolean startingBlueTooth,nacitam;

    Thread blueToothThread = new Thread() {
        public void run() {
            int i = 3000;
            while (!blueTooth.isEnabled()) {
                try {
                    Thread.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i = 50;
            }
            nacitam = startingBlueTooth = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView img = (ImageView) findViewById(R.id.imageView);
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.welcom_logo_animation);
                    img.startAnimation(anim);
                    text.startAnimation(anim);
                    enebleBloueTooth.startAnimation(anim);
                    appName.startAnimation(anim);
                    anim.setAnimationListener(Welcome.this);
                }
            });
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            blueTooth.enable();
            text.setText("Zapinam Bluetooth");
            blueToothThread.start();
            swipeView.invalidate();

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.welcome);
        float scale = getResources().getDisplayMetrics().density;
        text = (TextView)findViewById(R.id.txtWelcome);
        RLTswipe = (RelativeLayout) findViewById(R.id.RLTswipe);
        enebleBloueTooth = (LinearLayout) findViewById(R.id.enebla_BlueToothLayout);
        appName = (TextView) findViewById(R.id.appName);
        swipeView = new SwipeView(this,handler);
        RLTswipe.addView(swipeView);
        RLTswipe.setOnTouchListener(swipeView.swipetouch);
        Start();

    }
    private void Start()
    {
        if (!nacitam) {
            nacitam = true;
            SetBluetooth();
            if (!startingBlueTooth) {
                ImageView img = (ImageView) findViewById(R.id.imageView);
                ImageView logoFirma = (ImageView) findViewById(R.id.imgViewAppForceONe);
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.welcom_logo_animation);
                anim.setStartOffset(3000);
                img.startAnimation(anim);
                anim.setAnimationListener(Welcome.this);
                logoFirma.startAnimation(anim);
            } else {
                final ImageView logoFirma = (ImageView) findViewById(R.id.imgViewAppForceONe);
                Animation fadeou = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out);
                fadeou.setStartOffset(3000);
                fadeou.setDuration(500);
                logoFirma.startAnimation(fadeou);
                fadeou.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        logoFirma.setVisibility(View.GONE);
                        if (startingBlueTooth) {
                            enebleBloueTooth.setVisibility(View.VISIBLE);
                            Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_in);
                            fadeIn.setStartOffset(100);
                            fadeIn.setDuration(800);
                            enebleBloueTooth.startAnimation(fadeIn);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    }

    private void SetBluetooth() {

        if (blueTooth == null) {
            Toast.makeText(getApplicationContext(), "BlueTooth nenalezeno", Toast.LENGTH_LONG).show();
            startingBlueTooth = true;
        }
        else if (!blueTooth.isEnabled()) {
            startingBlueTooth=true;

        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}