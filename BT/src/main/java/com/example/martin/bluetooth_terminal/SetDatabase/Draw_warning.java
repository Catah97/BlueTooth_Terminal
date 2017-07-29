package com.example.martin.bluetooth_terminal.SetDatabase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.example.martin.bluetooth_terminal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 3. 9. 2015.
 * Vykresli u položek, kde nebyla nastavena odesílací hodnota
 * trojuhelník jako varování, že je uživatel nenastavil
 */
public class Draw_warning extends View {

    private final Bitmap waring;
    public ArrayList<Float> x = new ArrayList<>();
    public ArrayList<Float> y = new ArrayList<>();

    public Draw_warning(Context context) {
        super(context);
        waring = BitmapFactory.decodeResource(getResources(), R.drawable.waring);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0;i<x.size();i++) {
            canvas.drawBitmap(waring, x.get(i), y.get(i), null);
        }
        x.clear();
        y.clear();
        Log.e("done"," ");
        super.onDraw(canvas);
    }
}
