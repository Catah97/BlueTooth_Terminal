package com.example.martin.bluetooth_terminal.Draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.example.martin.bluetooth_terminal.BlueTooth.BlueTooth;
import com.example.martin.bluetooth_terminal.R;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by Martin on 5. 3. 2015.
 * Vykresluje volant
 */
public class DrawVolant extends View  {

    String data;

    public int x,y;
    private int Sx,Sy;
    private int draw = 0;
    private final Bitmap volant;
    private final Paint p;
    public float uhel;
    private boolean animBack;
    public boolean waitanime,anime; /**waitanime plati pro animaci pro stisknuti kdy objekt "dohani" Touch bod
                                     **aime plati pokud Touch neni sktisknu
                                     ** a pusobi to ze se objekt vraci na puvodni hodnotu */


    /**0 hodnota pro max prava
     * 1 hodnota pro střeení prava
     * 2 hodnota pro min prava
     * 3 je smer rovne
     * 4 hodnota pro min leva
     * 5 hodnota pro střední leva
     * 6 hodnota pro max leva
     */
    public String[] poleHodnot = new String[7];
    private String lastData = "";

    public TimerTask sender = new TimerTask() {
        @Override
        public void run() {
                BlueTooth.Send(data);
        }
    };

    public DrawVolant(Context context) {
        super(context);
        volant = BitmapFactory.decodeResource(getResources(), R.drawable.volant);
        p = new Paint();
        /**vyhlazeni bitmapy*/
        p.setFilterBitmap(true);
        p.setDither(true);
    }
    public DrawVolant(Context context,boolean animBack) {
        super(context);
        this.animBack = animBack;
        volant = BitmapFactory.decodeResource(getResources(), R.drawable.volant);
        p = new Paint();
        /**vyhlazeni bitmapy*/
        p.setFilterBitmap(true);
        p.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rotate();
        Rect drawRect = new Rect(0,0, volant.getWidth(), volant.getHeight());
        Rect dest = new Rect(Sx -(volant.getWidth()/2), // left
                Sy-(volant.getHeight()/2), // top
                Sx +(volant.getWidth()/2),//right
                Sy+(volant.getHeight()/2));// bottom
        canvas.save(Canvas.MATRIX_SAVE_FLAG); //Saving the canvas and later restoring it so only this image will be rotated.
        canvas.rotate(uhel,Sx, Sy);
        canvas.drawBitmap(volant, drawRect, dest, p);
        TakeData();
        if (draw < 10)
            draw++;
    }
    private void rotate(){
        if (anime && animBack)
            uhel = AnimeUhel();
        else {
            anime = false;
            uhel = Uhel();
        }
    }
    private float Uhel() {
        int rozdilX = Sx - x;
        int rozdilY = Sy - y;
        double vyslede;
        float potrebnyuhel;
        if (rozdilY == 0 && (uhel<=0 || uhel>180))
            potrebnyuhel = -90;
        else if (rozdilY == 0 && (uhel>0 || uhel<=180))
            potrebnyuhel = 90;
        else {
            double a = (double) rozdilX / rozdilY;
            vyslede = Math.atan(a);
            potrebnyuhel = (float) (vyslede * (180 / Math.PI))*-1;
        }
        if (rozdilY<0)                                          /**otáčení obrazu**/
            potrebnyuhel = potrebnyuhel+180;                    /**uvedeni uhlu do kladn0 hodnoty aby nedoslo k otoceni obrayu*/
        if (potrebnyuhel>180)
            potrebnyuhel = potrebnyuhel-360;                    /**uvedeni do yaporn7ch hodnot aby mohlo dojit k animaci*/
        if (potrebnyuhel >135)
            potrebnyuhel = 135;                                 /**zastaveni animace */
        if (potrebnyuhel <-135)
            potrebnyuhel =-135;
        if  (potrebnyuhel < uhel-5)                           /**samotne oteceni v uhlech*/
            uhel = uhel -5;
        else if (potrebnyuhel > uhel+5)
            uhel = uhel +5;
        else {
            uhel = potrebnyuhel;
            waitanime = false;
        }
        return uhel;
    }
    private float AnimeUhel(){
        float uhel = this.uhel;
        if (uhel>3 && uhel<180)
            uhel=uhel-3;
        else if (uhel<-3 || (uhel>180 && uhel<360))
            uhel = uhel +3;
        else {
            uhel = y = 0;
            x=Sx;
            anime = false;
        }
        return uhel;
    }
    private void TakeData(){

        if (uhel>90)
            data = poleHodnot[2];
        else if (uhel> 45)
            data= poleHodnot[1];
        else if (uhel>0)
            data=poleHodnot[0];

        else if (uhel == 0)
            data = poleHodnot[3];
        else if (uhel<-90)
            data=poleHodnot[6];
        else if (uhel<-45)
            data=poleHodnot[5];
        else
            data=poleHodnot[4];
        if (data != null && !data.equals(lastData) && draw !=0) {
            BlueTooth.Send(data);
            lastData = data;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        Sx = x= parentWidth/2;
        Sy = parentHeight/2;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
