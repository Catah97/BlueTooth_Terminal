package yuku.ambilwarna;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Martin on 8. 2. 2016.
 */
public class ColorPicker extends AppCompatActivity implements TextWatcher {

    private boolean supportsAlpha;
    View viewHue;
    HUE_Squar viewSatVal;
    ImageView viewCursor;
    ImageView viewAlphaCursor;
    View viewOldColor;
    View viewNewColor;
    View viewAlphaOverlay;
    ImageView viewTarget;
    ImageView viewAlphaCheckered;
    ViewGroup viewContainer;
    EditText txtA,txtR,txtG,txtB;
    final float[] currentColorHsv = new float[3];
    int alpha;
    boolean save;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1){
                finish();
                overridePendingTransition(R.animator.set_control_left_in,R.animator.set_control_left_out);
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                End();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            End();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void End(){
        if (!save){
            WaringDialog.Dialog(this, handler,"Opravdu chcete pokračovate bez uložení?");
        }
        else {
            Intent intent = new Intent();
            intent.putExtra("ID", getIntent().getExtras().getInt("ID"));
            viewNewColor.setDrawingCacheEnabled(true);
            String finalColor = GetRGB(viewNewColor.getDrawingCache());
            viewNewColor.setDrawingCacheEnabled(false);
            intent.putExtra("color",finalColor);
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.animator.set_control_left_in, R.animator.set_control_left_out);
        }
    }
    private String GetRGB(Bitmap input){
        int pixel;
        pixel = input.getPixel(input.getWidth() / 2, input.getHeight() / 2);
        String output = String.valueOf(alpha)+","+String.valueOf(Color.red(pixel))+","+String.valueOf(Color.green(pixel))+","+String.valueOf(Color.blue(pixel));
        return output;
    }
    private void toHSV(int a,float r,float g,float b){

        r = r/255;
        g = g/255;
        b = b /255;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r,Math.min(g,b));

        /**h*/
        float h = 0;
        if (max == min)
            h =0;
        else if (max == r && g >= b)
            h = (int) (60 * ((g-b)/(max-min))+0);
        else if (max == r && g<b)
            h = (int)  (60 * ((g-b)/(max-min))+360);
        else if (max == g)
            h = (int)  (60 * ((b-r)/(max-min))+120);
        else if (max == b)
            h = (int)  (60 * ((r-g)/(max-min))+240);
        /**s*/
        float s = 0;
        if (max == 0)
            s = 0;
        else
            s =  1-(min/max);
        /**v*/
        float v =  max;

        setAlpha(a);
        setHue(h);
        setSat(s);
        setVal(v);
        moveCursor();
        moveTarget();
        moveAlphaCursor();
        updateAlphaView();
        viewSatVal.setHue(getHue());

    }
    private void SetText(){
        int pixel = getColor();
        txtA.setText(String.valueOf(alpha));
        txtR.setText(String.valueOf(Color.red(pixel)));
        txtG.setText(String.valueOf(Color.green(pixel)));
        txtB.setText(String.valueOf(Color.blue(pixel)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);                  /**zobrazí iconu sipku zpet*/
        actionBar.setHomeButtonEnabled(true);                       /**nastaví iconu eneble pro click*/
        actionBar.setTitle("Nastavení barvy.");
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String colorString = getIntent().getExtras().getString("BITMAP");
        String[] colors = colorString.split(",");
        int color = Color.argb(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]), Integer.parseInt(colors[3]));

        supportsAlpha = true;


        Color.colorToHSV(color, currentColorHsv);
        alpha = Color.alpha(color);


        setContentView(R.layout.color_picker);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save = true;
                End();
            }
        });
        viewHue = findViewById(R.id.ambilwarna_viewHue);
        viewSatVal = (HUE_Squar) findViewById(R.id.ambilwarna_viewSatBri);
        viewCursor = (ImageView) findViewById(R.id.ambilwarna_cursor);
        viewOldColor = findViewById(R.id.ambilwarna_oldColor);
        viewNewColor = findViewById(R.id.ambilwarna_newColor);
        viewTarget = (ImageView) findViewById(R.id.ambilwarna_target);
        viewContainer = (ViewGroup) findViewById(R.id.ambilwarna_viewContainer);
        viewAlphaOverlay = findViewById(R.id.ambilwarna_overlay);
        viewAlphaCursor = (ImageView) findViewById(R.id.ambilwarna_alphaCursor);
        viewAlphaCheckered = (ImageView) findViewById(R.id.ambilwarna_alphaCheckered);

        txtA = (EditText) findViewById(R.id.txtA);
        txtR = (EditText) findViewById(R.id.txtR);
        txtG = (EditText) findViewById(R.id.txtG);
        txtB = (EditText) findViewById(R.id.txtB);


        viewSatVal.setHue(getHue());
        viewOldColor.setBackgroundColor(color);
        viewNewColor.setBackgroundColor(color);

        viewHue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > viewHue.getMeasuredHeight()) {
                        y = viewHue.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                    }
                    float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
                    if (hue == 360.f) hue = 0.f;
                    setHue(hue);

                    // update view
                    viewSatVal.setHue(getHue());
                    moveCursor();
                    viewNewColor.setBackgroundColor(getColor());
                    updateAlphaView();
                    SetText();
                    return true;
                }
                return false;
            }
        });

        if (supportsAlpha) viewAlphaCheckered.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_MOVE)
                        || (event.getAction() == MotionEvent.ACTION_DOWN)
                        || (event.getAction() == MotionEvent.ACTION_UP)) {

                    float y = event.getY();
                    if (y < 0.f) {
                        y = 0.f;
                    }
                    if (y > viewAlphaCheckered.getMeasuredHeight()) {
                        y = viewAlphaCheckered.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                    }
                    final int a = Math.round(255.f - ((255.f / viewAlphaCheckered.getMeasuredHeight()) * y));
                    ColorPicker.this.setAlpha(a);

                    // update view
                    moveAlphaCursor();
                    int col = ColorPicker.this.getColor();
                    int c = a << 24 | col & 0x00ffffff;
                    viewNewColor.setBackgroundColor(c);
                    SetText();
                    return true;
                }
                return false;
            }
        });
        viewSatVal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float x = event.getX(); // touch event are in dp units.
                    float y = event.getY();

                    if (x < 0.f) x = 0.f;
                    if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

                    setSat(1.f / viewSatVal.getMeasuredWidth() * x);
                    setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

                    // update view
                    moveTarget();
                    viewNewColor.setBackgroundColor(getColor());
                    SetText();
                    return true;
                }
                return false;
            }
        });


        // move cursor & target on first draw
        ViewTreeObserver vto = viewHue.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                moveCursor();
                if (ColorPicker.this.supportsAlpha) moveAlphaCursor();
                moveTarget();
                if (ColorPicker.this.supportsAlpha) updateAlphaView();
                viewHue.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                SetText();
                txtA.addTextChangedListener(ColorPicker.this);
                txtR.addTextChangedListener(ColorPicker.this);
                txtB.addTextChangedListener(ColorPicker.this);
                txtG.addTextChangedListener(ColorPicker.this);
            }
        });
    }

    protected void moveCursor() {
        float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
        if (y == viewHue.getMeasuredHeight()) y = 0.f;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewCursor.setLayoutParams(layoutParams);
    }

    protected void moveTarget() {
        float x = getSat() * viewSatVal.getMeasuredWidth();
        float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewTarget.setLayoutParams(layoutParams);
    }

    protected void moveAlphaCursor() {
        final int measuredHeight = this.viewAlphaCheckered.getMeasuredHeight();
        float y = measuredHeight - ((this.getAlpha() * measuredHeight) / 255.f);
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.viewAlphaCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (this.viewAlphaCheckered.getLeft() - Math.floor(this.viewAlphaCursor.getMeasuredWidth() / 2) - this.viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) ((this.viewAlphaCheckered.getTop() + y) - Math.floor(this.viewAlphaCursor.getMeasuredHeight() / 2) - this.viewContainer.getPaddingTop());

        this.viewAlphaCursor.setLayoutParams(layoutParams);
    }

    private int getColor() {
        final int argb = Color.HSVToColor(currentColorHsv);
        return alpha << 24 | (argb & 0x00ffffff);
    }

    private float getHue() {
        return currentColorHsv[0];
    }

    private float getAlpha() {
        return this.alpha;
    }

    private float getSat() {
        return currentColorHsv[1];
    }

    private float getVal() {
        return currentColorHsv[2];
    }

    private void setHue(float hue) {
        currentColorHsv[0] = hue;
    }

    private void setSat(float sat) {
        currentColorHsv[1] = sat;
    }

    private void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    private void setVal(float val) {
        currentColorHsv[2] = val;
    }

    private void updateAlphaView() {
        final GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {
                Color.HSVToColor(currentColorHsv), 0x0
        });
        viewAlphaOverlay.setBackgroundDrawable(gd);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            int a = Integer.parseInt(txtA.getText().toString());
            float r = Float.parseFloat(txtR.getText().toString());
            float g = Float.parseFloat(txtG.getText().toString());
            float b = Float.parseFloat(txtB.getText().toString());
            toHSV(a, r,g,b);
        }
        catch (Exception ignore){
        }
    }
}
