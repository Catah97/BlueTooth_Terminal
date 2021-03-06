package com.example.martin.bluetooth_terminal.Controls;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.bluetooth_terminal.BlueTooth.BlueTooth;
import com.example.martin.bluetooth_terminal.Other.Methody;
import com.example.martin.bluetooth_terminal.Other.MyMath;
import com.example.martin.bluetooth_terminal.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Martin on 16.01.2016.
 */
public class Console extends Fragment implements TextWatcher{

    private static final String TAG = "Console";

    private static final int sizeOfIntInHalfBytes = 8;
    private static final int numberOfBitsInAHalfByte = 4;
    private static final int halfByte = 0x0F;
    private static final char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    private float scale;

    public Context context;
    private static boolean HEX,BIN,DEC, ASCII;
    private boolean landSpace;
    private EditText txtSend;
    private LinearLayout LLkeyboard,keyboard;
    private ScrollView scrolIn,scrolOut;
    private RelativeLayout LLinput,LLOutput,mainLayout;

    /**vsechny bity uloyene v desetine podobe*/
    public static ArrayList<String> dataOUT, dataIN;
    public static TextView inputStream,outpustStream;
    public boolean showKeyboard = true;

    private AnimationKeyboard anim;
    private View rootView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        landSpace = (getResources().getConfiguration().orientation == 2);
        scale = this.getResources().getDisplayMetrics().density;
        rootView = inflater.inflate(R.layout.console,container, false);
        inputStream = (TextView) rootView.findViewById(R.id.txtInputStream);
        outpustStream = (TextView) rootView.findViewById(R.id.txtOutputStream);
        LLinput = (RelativeLayout) rootView.findViewById(R.id.llInput);
        LLOutput = (RelativeLayout) rootView.findViewById(R.id.llOutput);
        LLkeyboard = (LinearLayout) rootView.findViewById(R.id.LLkeyboard);
        keyboard = (LinearLayout) rootView.findViewById(R.id.lineraLayoutKeyboard);
        mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);
        scrolIn = (ScrollView) rootView.findViewById(R.id.scrollInput);
        scrolOut = (ScrollView) rootView.findViewById(R.id.scrollOutput);
        final ViewTreeObserver vto = mainLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                CreateKeyboard();
            }
        });
        final ArrayList<String> spinerList = new ArrayList<>();
        spinerList.add("BIN");
        spinerList.add("HEX");
        spinerList.add("DEC");
        spinerList.add("ASCII");
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.spnChoose);
        txtSend = (EditText) rootView.findViewById(R.id.txtByte);
        txtSend.addTextChangedListener(this);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, spinerList);
        spinner.setAdapter(adapter);
        spinner.setSelection(getSelectedPostion());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean zmena = false;
                txtSend.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
                switch (position){
                    //vybráno BIN
                    case 0:
                        HEX = false;
                        DEC = false;
                        BIN = true;
                        ASCII = false;
                        txtSend.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
                        if (!showKeyboard)
                            zmena = true;
                        showKeyboard = true;
                        try {
                            inputStream.setText(ConvertToBIN(true));
                            outpustStream.setText(ConvertToBIN(false));
                        }catch (Exception ignore) {
                        }
                        break;
                    // vybráno HEX
                    case 1:
                        HEX = true;
                        DEC = false;
                        BIN = false;
                        ASCII = false;
                        if (showKeyboard)
                            zmena = true;
                        showKeyboard = false;
                        try {
                            inputStream.setText(ConvertToHEX(true));
                            outpustStream.setText(ConvertToHEX(false));
                        }catch (Exception ignore){}
                        break;
                    // vybráno DEC
                    case 2:
                        HEX = false;
                        DEC = true;
                        BIN = false;
                        ASCII = false;
                        if (showKeyboard)
                            zmena = true;
                        showKeyboard = false;
                        try {
                            inputStream.setText(ConvertToDec(true));
                            outpustStream.setText(ConvertToDec(false));
                        }catch (Exception ignore){}
                        break;
                    case 3:
                        HEX = false;
                        DEC = false;
                        BIN = false;
                        ASCII = true;
                        if (showKeyboard)
                            zmena = true;
                        showKeyboard = false;
                        try {
                            inputStream.setText(ConvertToAscii(true));
                            outpustStream.setText(ConvertToAscii(false));
                        }catch (Exception ignore){}
                }
                if (zmena) {
                    /**Animace pro vyjetí nebo zajeti */
                    anim = new AnimationKeyboard(txtSend.getHeight(), LLkeyboard.getX(), LLkeyboard.getY());
                    anim.setDuration(200);
                    LLkeyboard.startAnimation(anim);
                    final int posunY = txtSend.getHeight();
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            if (showKeyboard)
                                SetLayout(posunY);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (!showKeyboard)
                                SetLayout(posunY);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                txtSend.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        txtSend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String text = v.getText().toString();
                if (text.length() != 0) {
                    SendData(text);
                }
                return false;
            }
        });
        ImageView btnSend = (ImageView) rootView.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = txtSend.getText().toString();
                SendData(text);
            }
        });
        return rootView;
    }

    private int getSelectedPostion(){
        if (BIN){
            return 0;
        }
        if (HEX){
            return 1;
        }
        if (DEC){
            return 2;
        }
        else {
            return 0;
        }
    }

    private void SendData(String text){
        try {
            SendDataOrThrow(text);
        }
        catch (IllegalArgumentException ex){
            Toast.makeText(context, R.string.bad_value,Toast.LENGTH_SHORT).show();
        }
    }

    private void SendDataOrThrow(String s) throws IllegalArgumentException{
        String[] splited = s.split(" ");
        if (HEX) {
            /**HEX*/
            for (String s1 : splited) {
                int i = Integer.parseInt(s1, 16);
                if (i > 255) {
                    throw new IllegalArgumentException("value is large then 255");
                }
            }
            for (String s1 : splited) {
                int i = Integer.parseInt(s1, 16);
                BlueTooth.Send(i);
            }
            outpustStream.setText(ConvertToHEX(false));
        }
        else if (DEC) {
            /**DEC*/
            for (String s1 : splited) {
                int i = Integer.parseInt(s1);
                if (i > 255) {
                    throw new IllegalArgumentException("value is large then 255");
                }
            }
            for (String s1 : splited) {
                int i = Integer.parseInt(s1);
                BlueTooth.Send(i);
            }
            outpustStream.setText(ConvertToDec(false));
        }
        else if (BIN){
            /**BIN*/
            if (!Methody.CheckString(s).equals("NIC") && s.length() == 9) {
                /**prevode na DEC*/
                String[] pole = s.split(" ");
                String s1 = pole[0] + pole[1];
                int b = Integer.parseInt(s1,2);
                if (b > 255){
                    throw new IllegalArgumentException("value is large then 255");
                }
                BlueTooth.Send(b);
                outpustStream.setText(ConvertToBIN(false));
            }
            else
                throw new IllegalArgumentException("bad bin value: " + s);
        }
        else if (ASCII){
            for (int i = 0; i < s.length(); i++){
                char c = s.charAt(i);
                int sToSend = c;
                BlueTooth.Send(sToSend);
            }
            dataOUT.add(null);
            outpustStream.setText(ConvertToAscii(false));
        }
        txtSend.setText("");
    }

    public static void SetInputTextStatic(byte prichozi_zprava) {
        //int result = ByteBuffer.wrap(prichozi_zprava).getInt();
        //Log.e(TAG, "SetInputTextStatic: " + result);
        if (dataIN.size() > 500){
            dataIN.remove(500);
        }
        int input = 0;
        input = input << 8;
        int incomingByte = prichozi_zprava;
        incomingByte = incomingByte < 0 ? incomingByte & 0xFF : incomingByte;
        input = input + incomingByte;
        dataIN.add(String.valueOf(input));
    }

    String text;

    public void reDraw(){
        if (HEX){
            text = ConvertToHEX(true);
        }
        else if (DEC){
            text = ConvertToDec(true);
        }
        else if (BIN){
            text = ConvertToBIN(true);
        }
        else if (ASCII){
            text = ConvertToAscii(true);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                inputStream.setText(text);
            }
        });
    }


    private String ConvertToHEX(boolean in) {
        Collections.reverse(dataIN);
        Collections.reverse(dataOUT);
        ArrayList<String> poleDat =  (in) ? dataIN : dataOUT;
        String final_string = "";
        for (String s: poleDat) {
            if (s != null) {
                int dec = Integer.parseInt(s);
                StringBuilder hexBuilder = new StringBuilder(sizeOfIntInHalfBytes);
                hexBuilder.setLength(sizeOfIntInHalfBytes);
                for (int i = sizeOfIntInHalfBytes - 1; i >= 0; --i) {
                    int j = dec & halfByte;
                    hexBuilder.setCharAt(i, hexDigits[j]);
                    dec >>= numberOfBitsInAHalfByte;
                }
                while (true) {
                    if (!hexBuilder.toString().startsWith("0") || hexBuilder.length() == 1)
                        break;
                    hexBuilder.delete(0, 1);
                }
                final_string += hexBuilder.toString();
                final_string += "\n";
            }
        }
        /**vraceni listu do puvodni polohy*/
        Collections.reverse(dataIN);
        Collections.reverse(dataOUT);
        return final_string;
    }

    private String ConvertToAscii(boolean in){
        ArrayList<String> poleDat =  (in) ? dataIN : dataOUT;
        String final_string = "";
        String text = "";
        for (String s: poleDat) {
            if (s != null) {
                int number = Integer.parseInt(s);
                char c = (char) number;
                if (in) {
                    final_string += c;
                }
                else {
                    text += c;
                }
            }
            else {
                if (in) {
                    final_string += "\n";
                }
                else {
                    final_string = text + "\n" + final_string;
                    text = "";
                }
            }
        }
        return final_string;
    }

    private String ConvertToDec(boolean in){
        Collections.reverse(dataIN);
        Collections.reverse(dataOUT);
        ArrayList<String> poleDat =  (in) ? dataIN : dataOUT;
        String final_string = "";
        for (String s: poleDat) {
            if (s != null) {
                final_string += s + "\n";
            }
        }
        Collections.reverse(dataIN);
        Collections.reverse(dataOUT);
        return final_string;
    }

    private String ConvertToBIN(boolean in){
        Collections.reverse(dataIN);
        Collections.reverse(dataOUT);
        ArrayList<String> poleDat =  (in) ? dataIN : dataOUT;
        String final_string = "";
        for (String s: poleDat) {
            if (s != null) {
                int dec = Integer.parseInt(s);
            /*if (dec >= 32768){
                final_string += "Nepodporovaný formát zobrazení pro bin";
            }
            else {
                if (dec >= 256){
                    int startByte = (int) MyMath.expo(2,15);
                    for (int i = startByte; i>255; i-=i/2){
                        if (i == 2048){
                            final_string += " ";
                        }
                        final_string = (dec >= i) ? final_string + "1" : final_string + "0";
                        dec = (dec >= i) ? dec - i : dec;
                        if (i == 1)
                            break;
                    }
                    final_string += " | ";
                }*/
                for (int i = (int) MyMath.expo(2, 7); i > 0; i -= i / 2) {
                    if (i == 8) {
                        final_string += " ";
                    }
                    final_string = (dec >= i) ? final_string + "1" : final_string + "0";
                    dec = (dec >= i) ? dec - i : dec;
                    if (i == 1)
                        break;
                }
                //}
                final_string += "\n";
            }
        }
        Collections.reverse(dataIN);
        Collections.reverse(dataOUT);
        return final_string;
    }

    private void SetLayout() {
        int height = mainLayout.getHeight();
        int keyboardHeight = LLkeyboard.getHeight();
        int freeHeight = height - keyboardHeight;
        int itemHeight = freeHeight;
        inputStream.setText(ConvertToBIN(true));
        outpustStream.setText(ConvertToBIN(false));
        if (!landSpace) {
            itemHeight = itemHeight / 2;
            ViewGroup.LayoutParams params = LLinput.getLayoutParams();
            params.height = itemHeight;
            LLinput.setLayoutParams(params);
            ViewGroup.LayoutParams paramsScrollOut = scrolOut.getLayoutParams();
            paramsScrollOut.height = itemHeight- rootView.findViewById(R.id.txtOut).getHeight()- Math.round(21*scale);
            scrolOut.setLayoutParams(paramsScrollOut);
        }
        else {
            RelativeLayout center = (RelativeLayout) rootView.findViewById(R.id.center);
            ViewGroup.LayoutParams params3 = center.getLayoutParams();
            params3.height = itemHeight;
            center.setLayoutParams(params3);
            ViewGroup.LayoutParams paramsScrollIn = scrolIn.getLayoutParams();
            paramsScrollIn.height = itemHeight- rootView.findViewById(R.id.txtIn).getHeight();
            scrolIn.setLayoutParams(paramsScrollIn);
            ViewGroup.LayoutParams paramsScrollOut = scrolOut.getLayoutParams();
            paramsScrollOut.height = itemHeight- rootView.findViewById(R.id.txtOut).getHeight();
            scrolOut.setLayoutParams(paramsScrollOut);
        }
    }

    private void SetLayout(int posunY){
        ViewGroup.LayoutParams paramsLLoutput = LLOutput.getLayoutParams();
        paramsLLoutput.height = (showKeyboard) ? LLOutput.getHeight() - posunY :  LLOutput.getHeight() + posunY;
        LLOutput.setLayoutParams(paramsLLoutput);
        ViewGroup.LayoutParams paramsScrollOut = scrolOut.getLayoutParams();
        paramsScrollOut.height = (showKeyboard) ? scrolOut.getHeight() - posunY :  scrolOut.getHeight() + posunY;
        scrolOut.setLayoutParams(paramsScrollOut);
        if (landSpace) {
            ViewGroup.LayoutParams paramsScrollIn = scrolIn.getLayoutParams();
            paramsScrollIn.height =  (showKeyboard) ? scrolIn.getHeight() - posunY :  scrolIn.getHeight() + posunY;
            scrolIn.setLayoutParams(paramsScrollIn);
            ViewGroup.LayoutParams params3 = rootView.findViewById(R.id.center).getLayoutParams();
            params3.height =  (showKeyboard) ? rootView.findViewById(R.id.center).getHeight() - posunY :  rootView.findViewById(R.id.center).getHeight() + posunY;
            rootView.findViewById(R.id.center).setLayoutParams(params3);
        }
    }

    private void CreateKeyboard() {
        keyboard.removeAllViews();
        int width = keyboard.getWidth();
        int height = keyboard.getHeight();
        int margin = (int) (5 * scale);
        int onewidth = width / 2 - margin;
        int oneheight = height - 2 * margin;
        for (int i = 0; i < 2; i++) {
            final Button btn = new Button(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(onewidth, oneheight);

            if (i == 0)
                params.setMargins(0, margin, margin, margin);
            else
                params.setMargins(margin, margin, 0, margin);
            btn.setLayoutParams(params);
            btn.setBackgroundResource(R.drawable.oval_background);
            btn.setText(String.valueOf(i));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = txtSend.getText().toString();
                    text += btn.getText().toString();
                    txtSend.setText(text);
                }
            });
            keyboard.addView(btn);

        }
        SetLayout();
    }

    int beforeTXTleng;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeTXTleng = s.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 4 && beforeTXTleng !=5 && BIN) {
            txtSend.setText(s + " ");
            txtSend.setSelection(5);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    private class AnimationKeyboard extends Animation{

        float startX,startY,posunY;


        public AnimationKeyboard(int posunY,float startX,float startY){
            this.startX = startX;
            this.startY = startY;
            this.posunY = posunY;

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            Math.round(interpolatedTime * 100);
            if (showKeyboard){
                LLkeyboard.setX(startX);
                LLkeyboard.setY(startY - posunY*interpolatedTime);

            }
            else {
                LLkeyboard.setX(startX);
                LLkeyboard.setY(startY + posunY * interpolatedTime);

            }
        }
    }
}
