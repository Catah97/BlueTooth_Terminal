package com.example.martin.bluetooth_terminal.WelcomeScreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.martin.bluetooth_terminal.R;

/**
 * Created by Martin on 29.01.2016.
 * SwipeView pro zapnutí Bluetooth
 */
public class SwipeView extends View{


    static float scale,touchX,x,posun;
    final Drawable touchpoint,border;
    Paint textOnSwippe,textOnEnd;

    final Handler handler;
    private int viewWidth, viewHeight;
    final int width,height;
    private boolean starBlueTooth;

    public Touch swipetouch;
    GestureDetector gestureDetector;


    public SwipeView(Context context,Handler handler) {
        super(context);
        this.handler = handler;
        gestureDetector = new GestureDetector(context, new SingleTapConfirm());
        scale = getResources().getDisplayMetrics().density;
        textOnSwippe = new Paint();
        textOnSwippe.setColor(Color.WHITE);
        textOnSwippe.setTextSize(16F*scale);

        textOnEnd = new Paint();
        textOnEnd.setColor(Color.rgb(4,57,135));
        textOnEnd.setTextSize(16F*scale);
        touchpoint = ContextCompat.getDrawable(context, R.drawable.swipe_touchpoint);
        border = ContextCompat.getDrawable(context, R.drawable.swipe_border);

        swipetouch = new Touch();
        width = (int) (60*scale);
        height = (int) (40*scale);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawARGB(0, 255, 255, 255);
        border.setBounds(0, 0, viewWidth, viewHeight);
        border.draw(c);
        touchpoint.setBounds((int) x,0,(int)(x+width),height);
        touchpoint.draw(c);
        c.drawText("ON",(int) (viewWidth-width+20*scale),(int) (25*scale),textOnEnd);
        starBlueTooth = ((x+width)>(viewWidth-(viewWidth/5)));
        if (starBlueTooth)
            c.drawText("ON",(int) (x+20*scale),(int) (25*scale), textOnSwippe);
        else
            c.drawText("OFF",(int) (x+18*scale),(int) (25*scale), textOnSwippe);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        invalidate();
    }
    public class Touch implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchX = event.getX();
                    posun = touchX-x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                        if (starBlueTooth) {
                            x = viewWidth - width;
                            invalidate();
                            handler.sendEmptyMessage(10);
                            return false;
                        } else {
                            touchX = posun = 0;
                        }

                    break;
                default:
            }
            if (SetX())
                invalidate();
            return true;
        }
        private boolean SetX(){
            if ((posun)>width)
                return false;
            x = touchX-posun;
            x = (x<0)? 0:x;
            x = ((x+width)>viewWidth) ? viewWidth-width:x;

            return true;
        }
    }
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            handler.sendEmptyMessage(10);
            x = viewWidth-width;
            invalidate();
            return true;
        }
    }
}
