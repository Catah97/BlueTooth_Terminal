package com.example.martin.bluetooth_terminal.SetDatabase.DragAndDrop;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Martin on 15. 12. 2015.
 * Nastatívi aby se při drag and drop položka "zvedla do vzduchu"
 */
public class MyDragShadowBuilder extends View.DragShadowBuilder {

    // The drag shadow image, defined as a drawable thing
    private static Drawable shadow;
    final float x,y;
    // Defines the constructor for myDragShadowBuilder
    public MyDragShadowBuilder(View v,float X,float Y) {
        super(v);
        this.x = X;
        this.y = Y;
        shadow = new ColorDrawable(Color.LTGRAY);
    }
    private int width, height;

    @Override
    public void onProvideShadowMetrics (Point size, Point touch){

        width = getView().getWidth();

        height = getView().getHeight();

        shadow.setBounds(0, 0, width, height);

        size.set(width, height);

        touch.set((int )x, (int) y);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        getView().draw(canvas);
    }
}