package com.example.martin.bluetooth_terminal.SetDatabase.DragAndDrop;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.example.martin.bluetooth_terminal.Other.Konstanty;
import com.example.martin.bluetooth_terminal.SetDatabase.Item_Check;
import com.example.martin.bluetooth_terminal.SetDatabase.Set_Control;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Martin on 15. 12. 2015.
 * Když se položka nastaví jako setOntouchListeners na tuto třídu potom bude možno s touto položkou
 * pohobyvat methodou drag and drop
 */
public class DragAndDrop extends Set_Control implements View.OnDragListener,View.OnTouchListener {
    final Context context;

    private float colums_zbytek, rows_zbytek;

    ArrayList<HashMap<Integer,Integer>> layoutPosition = new ArrayList<>();
    ArrayList<LinearLayout> layoutSeznam = new ArrayList<>();
    ArrayList<Boolean> obsazeneLayouty = new ArrayList<>();

    int KONSTANT_X = 564698,KONSTANT_Y = 98491,posun_X, posun_Y;
    public int id_buffer;
    private boolean obsazeno = false;
    public boolean firstTouch = false;

    private int colums,rows, layoutWidth, layoutHeight;

    /**slouží k uložení touchpointu pro poznámky*/
    float[] poznakmySouradniceBuffer = new float[2];



    public DragAndDrop (Context context,GridLayout gridLayout,Handler handler){
        this.context = context;
        Creator(gridLayout,handler);
    }

    /**Vytvoří mřížku podle rozměrů display*/
    private void Creator(GridLayout grid,Handler handler){
        layoutWidth = grid.getWidth();
        layoutHeight = grid.getHeight();

        colums = (int) (layoutWidth /(size));
        rows = (int) (layoutHeight /(size));

        if (layoutWidth < colums*(size))
            colums = colums-1;
        if (layoutHeight < colums*(size))
            rows = rows-1;

        /**je potřeba aby nevznikali bíle pruhy podlé krajů obrazovky*/
        colums_zbytek =  ((layoutWidth - colums*(size))/colums);
        rows_zbytek =  ((layoutHeight - rows*(size))/rows);

        grid.setColumnCount(colums);
        grid.setRowCount(rows);


        /**Vytváření samotné mřížky*/
        for (int y = 0;y< rows;y++) {
            for (int x = 0;x<colums;x++) {
                LinearLayout l = new LinearLayout(context);
                l.setBackgroundColor(Color.TRANSPARENT);
                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(Math.round((size)+colums_zbytek), Math.round((size)+rows_zbytek));
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(LLParams);
                l.setLayoutParams(params);
                HashMap <Integer,Integer> hashMap = new HashMap<>();
                hashMap.put(KONSTANT_X,x);
                hashMap.put(KONSTANT_Y, y);
                layoutPosition.add(hashMap);
                l.setId(y * 10000 + x);
                Log.e("IDs", l.getId() + "");
                layoutSeznam.add(l);
                obsazeneLayouty.add(false);

                l.setOnDragListener(this);
                grid.addView(l);
            }
        }
        Message msg = new Message();
        msg.arg1 = Konstanty.PREPARING_SCREEN_COMPLETE;
        handler.sendMessage(msg);
    }

    public void SetPosin (float x,float y){
        /**zjištuje na kterém místu objektu se uživatel dotkl a vypočítá souřednice potřebuné k doasžení
         * levého horního okraje všech objektů
         * s tímto okrajem počítají všechny další methody
         */

        posun_X = (int)(x/(size+colums_zbytek));
        posun_Y =  (int) (y/(size+rows_zbytek));
    }


    public void UnSetObsazenePozice(float intput_x,float input_y,int sizeX, int sizeY) {
        /**ID se musi vypocitat v methodě protože jeho vypočetr trvá moc dlouho*/
        int l_ID = GetPosiiton(intput_x,input_y);
        /**maže uložené layouty že jsou obsazeny
         * volý se při začátku drag and drop
         * funguje stejně jako DrawBackground jenom s tím rozdílem že maže uložené backgroundy
         */
        int startX = 0,startY = 0;
        for (int i = 0;i<layoutSeznam.size();i++){
            if (l_ID == layoutSeznam.get(i).getId()){
                /**DŮLEŽITÉ!!!!!!! neni zde posunX a posunY protože se rovnou vola s levým horním layoutem*/
                startX = layoutPosition.get(i).get(KONSTANT_X);
                startY = layoutPosition.get(i).get(KONSTANT_Y);
                break;
            }
        }
        startX = startX<0 ? 0 : startX;
        startY = startY<0 ? 0 : startY;

        startX = startX+sizeX>colums-1 ? colums-1-sizeX : startX;
        startY = startY+sizeY>rows-1 ? rows-1-sizeY : startY;

        for (int y = startY; y <= startY + sizeY; y++) {
            for (int x = startX; x <= +startX + sizeX; x++) {
                for (int i = 0; i < layoutPosition.size(); i++) {
                    int postionX = layoutPosition.get(i).get(KONSTANT_X);
                    int positionY = layoutPosition.get(i).get(KONSTANT_Y);
                    if (postionX == x && positionY == y) {
                        obsazeneLayouty.set(i, false);
                    }
                }
            }
        }
    }

    public void SetObsazenePozice(float intput_x,float input_y,int sizeX, int sizeY) {
        int l_ID = GetPosiiton(intput_x,input_y);
        /**ukládá pozice layoutů které jsou zabrané */
        /**vola se pri konci drag and drop*/
        /**funguje stejně jako methoda DrawBackground*/
        /**jenom s tím rozdílem že layoutu pod objektem označuje že jsou obraseny*/

        int startX = 0,startY = 0;
        for (int i = 0;i<layoutSeznam.size();i++){
            if (l_ID == layoutSeznam.get(i).getId()){
                startX = layoutPosition.get(i).get(KONSTANT_X)-posun_X;
                startY = layoutPosition.get(i).get(KONSTANT_Y)-posun_Y;
                break;
            }
        }
        startX = startX<0 ? 0 : startX;
        startY = startY<0 ? 0 : startY;

        startX = startX+sizeX>colums-1 ? colums-1-sizeX : startX;
        startY = startY+sizeY>rows-1 ? rows-1-sizeY : startY;


        for (int y = startY; y <= startY + sizeY; y++) {
            for (int x = startX; x <= +startX + sizeX; x++) {
                for (int i = 0; i < layoutPosition.size(); i++) {
                    int postionX = layoutPosition.get(i).get(KONSTANT_X);
                    int positionY = layoutPosition.get(i).get(KONSTANT_Y);
                    if (postionX == x && positionY == y) {
                        obsazeneLayouty.set(i, true);
                    }
                }
            }
        }
    }

    private void DrawBackground(){
        /**překreslí všechny layout do defaultní barvy*/
        for (int y = 0;y<rows;y++) {
            for (int x=0;x<=+colums;x++) {
                for (int i = 0;i<layoutPosition.size();i++) {
                    LinearLayout linera = layoutSeznam.get(i);
                    linera.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
    }

    private void DrawBackground(int l_ID,int sizeX,int sizeY){
        obsazeno = false;
        /**sizeX a sizeY je int čislo které značí přes kolik layoutů se má objekt roztáhnout */

        /**zjištení levého horního okraje obrazu. Respektive layoutu na kterém se levý horní okej nacházi*/
        int startX = 0,startY = 0;
        for (int i = 0;i<layoutSeznam.size();i++){
            if (l_ID == layoutSeznam.get(i).getId()){
                /**posun se vypocitává v methodě SetPosun a je zde i napsan komentár*/
                startX = layoutPosition.get(i).get(KONSTANT_X)-posun_X;
                startY = layoutPosition.get(i).get(KONSTANT_Y)- posun_Y;
                break;
            }
        }
        /**pokud je obraz mimo obrazovku*/
        startX = startX<0 ? 0 : startX;
        startY = startY<0 ? 0 : startY;

        startX = startX+sizeX>colums-1 ? colums-1-sizeX : startX;
        startY = startY+sizeY>rows-1 ? rows-1-sizeY : startY;

        /**uloženi jiže vykreslených lazoutů*/
        ArrayList<LinearLayout> drawedLayout = new ArrayList<>();

        /**cyklus který vykreslí všechny layoutu pod obrazcem*/
        for (int y = startY;y<=startY+sizeY;y++) {
            for (int x=startX;x<=+startX+sizeX;x++) {
                /** nalezeni přislušného layoutu podle souřednicového systomu */
                for (int i = 0;i<layoutPosition.size();i++) {
                    int postionX = layoutPosition.get(i).get(KONSTANT_X);
                    int positionY = layoutPosition.get(i).get(KONSTANT_Y);
                    LinearLayout linera = layoutSeznam.get(i);
                    /**pokud se schodují tak to znamené že layout je pod obrazcem*/
                    if (postionX == x && positionY == y) {
                        linera.setBackgroundColor(Color.argb(200,68,68,68));
                        drawedLayout.add(linera);
                        /**pokud je pod layoutem jiný objekt*/
                        if (obsazeneLayouty.get(i)) {
                            obsazeno = true;
                            DrawBackgroundError(l_ID, sizeX, sizeY);
                            return;
                        }
                    }
                    /** v opračném připadě je layout mimo a musím byt překlesnelo pozadi jinak by vznikala stopa*/
                    else if (!drawedLayout.contains(linera))
                        linera.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
    }
    private void DrawBackgroundError(int l_ID,int sizeX,int sizeY){
        /**vola se pouze pokud je pod taženým objektem jiný objekt*/

        /**stejna jako methoda DrawBackground jenom stím rozdilem že vykresule s jinými barvamy*/
        int startX = 0,startY = 0;
        for (int i = 0;i<layoutSeznam.size();i++){
            if (l_ID == layoutSeznam.get(i).getId()){
                startX = layoutPosition.get(i).get(KONSTANT_X)-posun_X;
                startY = layoutPosition.get(i).get(KONSTANT_Y)- posun_Y;
                break;
            }
        }
        startX = startX<0 ? 0 : startX;
        startY = startY<0 ? 0 : startY;

        startX = startX+sizeX>colums-1 ? colums-1-sizeX : startX;
        startY = startY+sizeY>rows-1 ? rows-1-sizeY : startY;
        ArrayList<LinearLayout> drawedLayout = new ArrayList<>();

        for (int y = startY;y<=startY+sizeY;y++) {
            for (int x=startX;x<=+startX+sizeX;x++) {
                for (int i = 0;i<layoutPosition.size();i++) {
                    int postionX = layoutPosition.get(i).get(KONSTANT_X);
                    int positionY = layoutPosition.get(i).get(KONSTANT_Y);
                    LinearLayout linera = layoutSeznam.get(i);
                    if (postionX == x && positionY == y) {
                        linera.setBackgroundColor(Color.argb(150,200,0,0));
                        drawedLayout.add(linera);

                    }
                    else if (!drawedLayout.contains(linera))
                        linera.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
    }

    private Handler longClikcHandler = new Handler();
    Runnable runnable;

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        id_buffer = v.getId();
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        /**pojistka kdyby náhodou uživatel nejal na action_bar tak aby nebyl furt firstTouch*/
                        if (!items_menu.isDrawerOpen()) {
                            firstTouch = false;
                            draw_warning.invalidate();
                            imgThras.setVisibility(View.VISIBLE);
                            id_buffer = v.getId();
                            if (Item_Check.poznamkyID.contains(v.getId())) {
                                poznakmySouradniceBuffer[0] = event.getX();
                                poznakmySouradniceBuffer[1] = event.getY();
                            } else {
                                SetPosin(event.getX(), event.getY());
                                int[] sizes = sizes();
                                int width = sizes[0], height = sizes[1];
                                UnSetObsazenePozice(v.getX(), v.getY(), width, height);
                            }
                            vibrator.vibrate(100);

                            /**občas appka spadla pokud uživate uhnul s prtem, toto tomu zabrání*/
                            float x = (event.getX() < 0) ? 0 : event.getX();
                            float y = (event.getY() < 0) ? 0 : event.getY();
                            MyDragShadowBuilder myShadow = new MyDragShadowBuilder(v, x, y);
                            v.startDrag(null, myShadow, v, 0);
                        }
                    }
                };
                longClikcHandler.postDelayed(runnable, 500);
                break;
            case MotionEvent.ACTION_UP:
                longClikcHandler.removeCallbacks(runnable);
                break;
            case MotionEvent.ACTION_CANCEL:
                longClikcHandler.removeCallbacks(runnable);

        }
        return true;
    }

    private void Delete(View view){
        Item_Check.InvisibleAddMethod(view.getId());
        databaOperations.Updata(String.valueOf(view.getId()), Konstanty.NULL, Konstanty.NULL, "INVISIBLE");
        /**pokud plati potom se smazou ulozene hodnoty*/
        if (Item_Check.volantRltID.contains(view.getId())) {
            /**ROVNE, PRAVA,           LEVA                       */
            databaOperations.UpdateVolant_BlueTooth(String.valueOf(view.getId()), null, null, null, null, null, null, null, null,null);
        }
        if (Item_Check.buttonID.contains(view.getId())) {
            databaOperations.UpdateButton(String.valueOf(view.getId()), null, null, null, null);
        }
        if (Item_Check.plynRltID.contains(view.getId())) {
            databaOperations.UpdatePlyn(String.valueOf(view.getId()), null, null, null);
            databaOperations.UpdatePlyn_BlueTooth(String.valueOf(view.getId()), null, null, null, null, null, null, null,null);
        }
        if (Item_Check.poznamkyID.contains(view.getId()))
            databaOperations.UpdatePoznamky(String.valueOf(view.getId()),null,null,null,null);
        view.setX(Konstanty.NULL);
        view.setY(Konstanty.NULL);
        view.setVisibility(View.INVISIBLE);
        DrawBackground();
    }



    @Override
    public boolean onDrag(final View v, DragEvent event) {
        final int action = event.getAction();
        /**psát vždycky o jedno menší než je skutečná velikost objektu*/
        int[] sizes = sizes();
        final int width = sizes[0], height = sizes[1];
        boolean thras = false, actionBar = false;
        if (v.getId() == imgThras.getId()) {
            thras = true;
        }
        if (v.getId() == toolbar.getId()){
            actionBar = true;
        }


        // Handles each of the expected events
        final View view = (View) event.getLocalState();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                view.setVisibility(View.INVISIBLE);
                return true;
            case DragEvent.ACTION_DRAG_EXITED:

                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (!Item_Check.poznamkyID.contains(view.getId())) {
                    if (!thras && !actionBar)
                        DrawBackground(v.getId(), width, height);
                    else {
                        DrawBackground();
                    }
                }
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                if (!Item_Check.poznamkyID.contains(view.getId())) {
                    if (!thras && !actionBar)
                        DrawBackground(v.getId(), width, height);
                    else {
                        DrawBackground();
                    }
                }
                return true;
            case DragEvent.ACTION_DROP:
                if (v.getId() == toolbar.getId()){
                    if (firstTouch) {
                        Delete(view);
                    } else {
                        Cursor data = databaOperations.GetID_Data(String.valueOf(view.getId()));
                        data.moveToFirst();
                        view.setX(data.getFloat(2));
                        view.setY(data.getFloat(3));
                        view.setVisibility(View.VISIBLE);
                        DrawBackground();
                        /**musí být vynulováno aby se nezapsala špatná souradnice*/
                        posun_X = posun_Y = 0;
                        if (!Item_Check.poznamkyID.contains(view.getId()))
                            SetObsazenePozice(view.getX(), view.getY(), width, height);
                    }
                    imgThras.setVisibility(View.INVISIBLE);
                }
                else if (thras) {
                    Delete(view);
                }
                else if (Item_Check.poznamkyID.contains(view.getId())){
                    view.setX((v.getX()+event.getX())-poznakmySouradniceBuffer[0]);
                    view.setY((v.getY()+event.getY())-poznakmySouradniceBuffer[1]);
                    view.setVisibility(View.VISIBLE);
                    databaOperations.Updata(String.valueOf(view.getId()),view.getX(),view.getY(),"VISIBLE");
                    Item_Check.AnimationCheck(view,waringLayout.getWidth(),waringLayout.getHeight(),databaOperations);
                }
                else if (obsazeno &&firstTouch) {
                    Delete(view);
                }
                else if (obsazeno) {
                    Cursor data = databaOperations.GetID_Data(String.valueOf(view.getId()));
                    data.moveToFirst();
                    view.setX(data.getFloat(2));
                    view.setY(data.getFloat(3));
                    view.setVisibility(View.VISIBLE);
                    DrawBackground();
                    /**musí být vynulováno aby se nezapsala špatná souradnice*/
                    posun_X = posun_Y = 0;
                    SetObsazenePozice(view.getX(), view.getY(), width, height);
                } else {
                    int[] souradnice = GetPosition(v, view.getWidth(), view.getHeight());
                    view.setX(souradnice[0]);
                    view.setY(souradnice[1]);
                    view.setVisibility(View.VISIBLE);
                    DrawBackground();
                    SetObsazenePozice(v.getX(), v.getY(), width, height);
                    databaOperations.Updata(String.valueOf(view.getId()), view.getX(), view.getY(), "VISIBLE");
                }
                imgThras.setVisibility(View.INVISIBLE);
                firstTouch = false;
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                /**jelikož se nestala žýdná akte tudíž bylo vráce false potom je i event.getResut() false*/
                if (!event.getResult()) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {

                            if (firstTouch) {
                                Delete(view);
                            } else {
                                Cursor data = databaOperations.GetID_Data(String.valueOf(view.getId()));
                                data.moveToFirst();
                                view.setX(data.getFloat(2));
                                view.setY(data.getFloat(3));
                                view.setVisibility(View.VISIBLE);
                                DrawBackground();
                                /**musí být vynulováno aby se nezapsala špatná souradnice*/
                                posun_X = posun_Y = 0;
                                if (!Item_Check.poznamkyID.contains(view.getId()))
                                    SetObsazenePozice(view.getX(), view.getY(), width, height);
                            }
                            imgThras.setVisibility(View.INVISIBLE);
                        }
                    });
                    return true;
                }

                break;

        }
        return false;
    }
    public int[] sizes(){
        /**output[0] je width
         * output[1] je height
         */

        /**mělo by být o jednu menší než násobek velikosti*/

        int [] output = new int[2];
        if (Item_Check.buttonID.contains(id_buffer)) {
            output[0] = 2;
            output[1] = 1;
        }
        else if (Item_Check.switchID.contains(id_buffer)){
            output[0] = 2;
            output[1] = 0;
        }
        else if (Item_Check.plynRltID.contains(id_buffer)) {
            output[0] = 1;
            output[1] = 5;
        }
        else if (Item_Check.volantRltID.contains(id_buffer)) {
            output[0] = 3;
            output[1] = 3;
        }
        else if (Item_Check.posouvacID.contains(id_buffer)){
            output[0] = 7;
            output[1] = 0;
        }
        return output;
    }

    /**vraci id layoutu*/
    private int GetPosiiton(float intput_X,float intput_Y){
        int outpur = 0;
        for (int i = 0; i < layoutSeznam.size(); i++) {
            float positionX = layoutSeznam.get(i).getX();
            float positionY = layoutSeznam.get(i).getY();
            if (intput_X > (positionX-(15*scale)) && intput_X< (positionX+(15*scale)) && intput_Y > (positionY-(15*scale)) && intput_Y < + (positionY+(15*scale))) {
                outpur =  layoutSeznam.get(i).getId();
            }
        }
        return outpur;
    }

    /**vraci presne souradncie na obrayovce*/
    private int[] GetPosition(View v,int widht,int heaight){
        float x = v.getX() - (posun_X * Math.round(size+colums_zbytek));
        float y = v.getY() - (posun_Y * Math.round(size+ rows_zbytek));


        x = (x<0) ? 0 : x;
        y = (y<0) ? 0 : y;

        x = (x> (waringLayout.getWidth()- widht)) ? waringLayout.getWidth()-widht : x;
        y = (y> (waringLayout.getHeight()-heaight)) ? waringLayout.getHeight()-heaight : y;
        LinearLayout l = null;

        for (int i = 0; i < layoutSeznam.size(); i++) {
            float positionX = layoutSeznam.get(i).getX();
            float positionY = layoutSeznam.get(i).getY();
            if (x > (positionX-(15*scale)) && x< (positionX+(15*scale)) && y > (positionY-(15*scale)) && y < + (positionY+(15*scale))) {
                l =  layoutSeznam.get(i);
                break;
            }
        }
        int [] souradnice = new int[2];
        souradnice[0] = (int)l.getX();
        souradnice[1] = (int)l.getY();
        return souradnice;
    }
}
