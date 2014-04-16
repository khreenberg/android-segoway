package khr.easv.pokebotcontroller.app.gui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.Logger;

public abstract class AbstractKnobView extends View {

    private HashSet<KnobUpdateListener> listeners;

    protected float frameStrokeWidth;
    protected int radius;
    protected float knobSizeRatio;

    private float knobOffsetX, knobOffsetY;
    private float controlX, controlY;

    private Drawable knob;
    private int joystickFrameColor = Color.WHITE;

    public AbstractKnobView(Context context) {
        super(context);
        init(null, 0);
    }

    public AbstractKnobView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AbstractKnobView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.KnobView, defStyle, 0);

        radius = a.getInt(R.styleable.KnobView_radius, 200);
        knobSizeRatio = a.getFloat(R.styleable.KnobView_knobRelativeSize, .25f);
        frameStrokeWidth = a.getFloat(R.styleable.KnobView_frameStrokeWidth, 2.5f);
        int knobID = a.hasValue(R.styleable.KnobView_knobGFX) ?
                R.styleable.KnobView_knobGFX : R.drawable.joyknob;
        knob = getResources().getDrawable(knobID);
        joystickFrameColor = a.getColor(R.styleable.KnobView_frameColor, joystickFrameColor);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        // Calculate the knob radius. TODO: Optimize this by caching a result.
        int knobRadius = (int) (radius * knobSizeRatio);

        // Draw the frame of the joystick
        drawFrame(paddingLeft + radius, paddingTop + radius, canvas);
        // Calculate the center position for the knob
        int knobXZero = (int) (paddingLeft + radius - knobRadius);
        int knobYZero = (int) (paddingTop + radius - knobRadius);
        // Draw the knob
        drawKnob(knobXZero, knobYZero, canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredDimension = (int) (radius * 2 + frameStrokeWidth * 2);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredDimension, widthSize);
        } else {
            //Be whatever you want
            width = desiredDimension;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredDimension, heightSize);
        } else {
            //Be whatever you want
            height = desiredDimension;
        }

        int dim = Math.min(width, height);
        radius = (int) (dim / 2 - frameStrokeWidth);

        setMeasuredDimension(dim, dim);
    }

    private Paint joystickFramePaint;
    protected void drawFrame(int x, int y, Canvas canvas){
        if( joystickFramePaint == null ) initFramePaint();
        joystickFramePaint.setStrokeWidth(frameStrokeWidth);
        x += frameStrokeWidth;
        y += frameStrokeWidth;
        canvas.drawCircle(x,y,radius,joystickFramePaint);
    }

    protected void drawKnob(int x, int y, Canvas canvas){
        // Calculate the knob radius. TODO: Optimize this by caching a result.
        int knobRadius = (int) (radius * knobSizeRatio);
        x += frameStrokeWidth + knobOffsetX;
        y += frameStrokeWidth + knobOffsetY;
        knob.setBounds(x, y, x+knobRadius * 2, y+knobRadius * 2);
        knob.draw(canvas);
    }

    protected void initFramePaint(){
        joystickFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        joystickFramePaint.setStyle(Paint.Style.STROKE);
        joystickFramePaint.setColor(joystickFrameColor);
        joystickFramePaint.setStrokeWidth(frameStrokeWidth);
    }

    /** Should return a relative X position, relative to the center of the view */
    protected abstract int getKnobX();
    /** Should return a relative Y position, relative to the center of the view */
    protected abstract int getKnobY();

    public void updateKnobPosition(){
        // Calculate the knob radius. TODO: Optimize this by caching a result.
        int knobRadius = (int) (radius * knobSizeRatio);

        // Get the X & Y positions relative to the center of the view from the subclass
        float x = getKnobX();
        float y = getKnobY();

        // Calculate the length of the vector from the center of the view to the point from the subclass
        float length = (float) Math.sqrt(x*x + y*y);

        // If length is not zero, normalize the vector
        x /= length != 0 ? length : 1;
        y /= length != 0 ? length : 1;

        // Allow the length of the vector to be less than the radius
        float ratio = length > radius ? 1 : length / radius;

        // Set the offsets for the knob graphic.
        knobOffsetX = Math.abs(length) + knobRadius < radius ? getKnobX() : x * (radius - knobRadius);
        knobOffsetY = Math.abs(length) + knobRadius < radius ? getKnobY() : y * (radius - knobRadius);

        // Notify listeners of the calculated point
        notifyListeners(x * ratio, y * ratio);

        // Refresh the view
        invalidate();
    }

    public interface KnobUpdateListener{
        void onKnobUpdate(float x, float y);
    }

    public void addListener(KnobUpdateListener listener){
        if( listeners == null ) listeners = new HashSet<KnobUpdateListener>();
        listeners.add(listener);
    }

    public void removeListener(KnobUpdateListener listener){
        listeners.remove(listener);
        if( listeners.isEmpty() ) listeners = null;
    }

    private void notifyListeners(float x, float y){
        for( KnobUpdateListener listener : listeners ) listener.onKnobUpdate(x, y);
    }
}
