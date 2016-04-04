package com.example.martin.bluetooth_terminal.SetDatabase;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.example.martin.bluetooth_terminal.Database.DatabaOperations;

import java.util.ArrayList;

/**
 * Created by Martin on 23. 9. 2015.
 * Slouží pro animaci poznámky, pokud ji uživatel umístí mimo display
 * může fungovat i pro jiné položky
 */
public class Item_Check {

    public static ArrayList<Integer> volantRltID = new ArrayList<>();
    public static ArrayList<Integer> invisibleVolantRlt = new ArrayList<>();

    public static ArrayList<Integer> plynRltID = new ArrayList<>();
    public static ArrayList<Integer> invisiblePlynRlt = new ArrayList<>();

    public static ArrayList<Integer> switchID = new ArrayList<>();
    public static ArrayList<Integer> invisibleSwitch = new ArrayList<>();

    public static ArrayList<Integer> buttonID = new ArrayList<>();
    public static ArrayList<Integer> invisibleBtnID = new ArrayList<>();

    public static ArrayList<Integer> posouvacID = new ArrayList<>();
    public static ArrayList<Integer> invisiblePosouvacID = new ArrayList<>();

    public static ArrayList<Integer> poznamkyID = new ArrayList<>();
    public static ArrayList<Integer> invisiblePoznamkyID = new ArrayList<>();



    public static void InvisibleAddMethod(int id){
        if (buttonID.contains(id))
            invisibleBtnID.add(id);
        else if (volantRltID.contains(id))
            invisibleVolantRlt.add(id);
        else if (switchID.contains(id))
            invisibleSwitch.add(id);
        else if (plynRltID.contains(id))
            invisiblePlynRlt.add(id);
        else if (poznamkyID.contains(id))
            invisiblePoznamkyID.add(id);
        else if (posouvacID.contains(id))
            invisiblePosouvacID.add(id);

    }

    private static boolean left,right,top,bot;
    private static boolean anim;

    public static void AnimationCheck(final View v,float screenWidth,float screenHeight,final DatabaOperations databaOperations){
        left=right=top=bot=false;
        if (v.getX() < 0)
            left = true;
        if (v.getY() < 0)
            top = true;
        if ((v.getX()+v.getWidth())>screenWidth)
            right = true;
        if ((v.getY()+v.getHeight())>screenHeight)
            bot = true;
        if (left || right || top || bot) {
            ItemAnimation imgAnim = new ItemAnimation(v.getX(),v.getY(),screenWidth,screenHeight,v);
            anim = true;
            imgAnim.setDuration(200);
            v.startAnimation(imgAnim);
            imgAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    /**slouži k tomu aby informace o pozici o animaci byli nahrány do databáze*/
                    databaOperations.Updata(String.valueOf(v.getId()), v.getX(), v.getY(), "VISIBLE");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
    public static class ItemAnimation extends Animation{

        private final float startX,startY,screenWidth,screenHeight;
        private final View v;
        private float needX,needY;

        public ItemAnimation(float startX,float startY,float screenWidth,float screenHeight,View v){
            this.startX = startX;
            this.startY = startY;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.v = v;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            interpolatedTime = Math.round(interpolatedTime*100);
            if (anim){
                if (left)
                    needX = (0 - startX)/100;
                if (top)
                    needY = (0 - startY)/100;
                if (right)
                    needX = (screenWidth - (startX+v.getWidth()))/100;
                if (bot)
                    needY = (screenHeight - (startY+v.getHeight()))/100;
                anim = false;
            }
            v.setX(startX + (needX*interpolatedTime));
            v.setY(startY + (needY*interpolatedTime));
        }
    }
}
