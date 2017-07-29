package com.example.martin.bluetooth_terminal.Draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.martin.bluetooth_terminal.BlueTooth.BlueTooth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by Martin on 04.10.2015.
 * Vykresluje Plyn
 */
public class DrawPlyn extends View {

    public int y,yGet;
    private int height,width,line,rychlost;
    private String data,lastData;
    private String[] poleHodnot;
    private ArrayList<Float> hranice = new ArrayList<>();
    private int draw = 0;
    private boolean animBack;
    public boolean firstStart,waitAnime,anime,thrasPosition; /**waitanime plati pro animaci pro stisknuti kdy objekt "dohani" Touch bod
                                                **aime plati pokud Touch neni sktisknu
                                                ** a pusobi to ze se objekt vraci na puvodni hodnotu */
    public TimerTask sender = new TimerTask() {
                @Override
                public void run() {
                    BlueTooth.Send(data);
                }
            };

    private Paint hranicColor,posunColor,backgroundColor,trashColor;
    public DrawPlyn(Context context, String hraniceColor, String posunColor, String backgroundColor, String[] data){
        super(context);
        /**musí zde aby nedo nedocházelo k pádům když je položka neviditelná */
        try {
            thrasPosition = false;
            poleHodnot = data;
            int[] buffer = GetColor(hraniceColor);
            this.hranicColor = new Paint();
            this.hranicColor.setColor(Color.argb(buffer[0], buffer[1], buffer[2], buffer[3]));
            int[] buffer2 = GetColor(posunColor);
            this.posunColor = new Paint();
            this.posunColor.setColor(Color.argb(buffer2[0], buffer2[1], buffer2[2], buffer2[3]));
            int[] buffer3 = GetColor(backgroundColor);
            this.backgroundColor = new Paint();
            this.backgroundColor.setColor(Color.argb(buffer3[0], buffer3[1], buffer3[2], buffer3[3]));
            this.trashColor = new Paint();
            this.trashColor.setColor(Color.argb(218, 158, 2, 9));
            this.animBack = animBack;
            line = 1;
            if (data[2] != null)
                line++;
            if (data[3] != null)
                line++;
            if (data[4] != null)
                line++;
            if (data[5] != null)
                line++;
        }
        catch (Exception ignore){

        }


    }
    public DrawPlyn(Context context, String hraniceColor, String posunColor, String backgroundColor,
                    String[] data,boolean animBack){
        super(context);
        /**musí zde aby nedo nedocházelo k pádům když je položka neviditelná */
        try {
            thrasPosition = false;
            poleHodnot = data;
            int[] buffer = GetColor(hraniceColor);
            this.hranicColor = new Paint();
            this.hranicColor.setColor(Color.argb(buffer[0], buffer[1], buffer[2], buffer[3]));
            int[] buffer2 = GetColor(posunColor);
            this.posunColor = new Paint();
            this.posunColor.setColor(Color.argb(buffer2[0], buffer2[1], buffer2[2], buffer2[3]));
            int[] buffer3 = GetColor(backgroundColor);
            this.backgroundColor = new Paint();
            this.backgroundColor.setColor(Color.argb(buffer3[0], buffer3[1], buffer3[2], buffer3[3]));
            this.trashColor = new Paint();
            this.trashColor.setColor(Color.argb(218, 158, 2, 9));
            this.animBack = animBack;
            line = 1;
            if (data[2] != null)
                line++;
            if (data[3] != null)
                line++;
            if (data[4] != null)
                line++;
            if (data[5] != null)
                line++;
        }
        catch (Exception ignore){

        }


    }
    private int[] GetColor(String input){
        String[] buffer = input.split(",");
        int[] outpun = new int[4];
        for (int i = 0 ;i<buffer.length;i++){
            outpun[i] = Integer.parseInt(buffer[i]);
        }
        return outpun;
    }

    @Override
    protected void onDraw(Canvas canvas) {   /**predelat!!!!!!!*/
        super.onDraw(canvas);
        if (animBack)
            AnimationY();
        AnimeStop();
        if (!anime)
            AnimeMove();
        if (firstStart) { /**KOntrola prvního spuštení*****zapne se pouze při první spustění potom už ne**/
            y = height;
            if (yGet ==0)
                yGet = height;
            firstStart = false;
        }
        Rect background = new Rect(0,0,canvas.getWidth(),canvas.getHeight());
        if (thrasPosition)
            canvas.drawRect(background,trashColor);
        else
            canvas.drawRect(background,backgroundColor);
        Rect posun = new Rect(0,y,canvas.getWidth(),height);
        canvas.drawRect(posun,posunColor);
        hranice.clear();
        for (int i = 0;i<line;i++) {
            float hranice = (height / line) * i;
            if (i > 0 && i < line) {
                canvas.drawLine(0, hranice, width, hranice, hranicColor);
                this.hranice.add(hranice);
            }
        }
        /**prohozeni listu*/
        Collections.reverse(hranice);
        TakeData();
        if (draw < 10)
            draw++;
    }

    private void AnimationY(){
        if (anime && y<height)
            y = y+rychlost;
        else if (anime)
            anime = false;
    }
    private void AnimeStop(){                           /**pokud se y vraci do původní pozice*/
        if (y>=height) {                         /**tato methoda slouží k tomu aby se y zastavilo na neutrálu*/
            anime = false;
            y = height;
        }

    }
    private void AnimeMove(){
        if (y<(yGet+(rychlost+10))&&y>(yGet-(rychlost+10))) {
            waitAnime = false;
            y = yGet;
        }
        else if (waitAnime && y<yGet)
            y = y+rychlost;
        else if (waitAnime && y>yGet)
            y = y-rychlost;
    }

    private void TakeData(){
        try {
            if (y < hranice.get(hranice.size() - 1))
                data = poleHodnot[line];
        }
        /**vola se jenom kdyz neni nastaveno */
        catch (Exception ignore){
            data = poleHodnot[line];
        }
        for (int i = 0;i<hranice.size();i++){
            if (y>hranice.get(i)){
                data = poleHodnot[i+1];
                break;
            }
        }
        if (y == height)
            data = poleHodnot[0];                   /**pokud bluetooth nebylo inicializováno*/
        if (data != null && !data.equals(lastData) && BlueTooth.mmOutStream !=null && draw !=0) {
            BlueTooth.Send(data);
            lastData = data;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        width = parentWidth;
        height = parentHeight;
        rychlost = (height/100)*5;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
