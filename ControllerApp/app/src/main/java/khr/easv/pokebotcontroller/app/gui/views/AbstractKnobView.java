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

public abstract class AbstractKnobView extends View {

    // Default values
    protected static final int   DEFAULT_RADIUS             =   200;
    protected static final float DEFAULT_KNOB_SIZE_RATIO    = 0.25f;
    protected static final float DEFAULT_FRAME_STROKE_WIDTH = 2.50f;

    private HashSet<KnobUpdateListener> _listeners;

    protected float _frameStrokeWidth;
    protected int _radius;
    protected float _knobSizeRatio;

    private float _knobOffsetX, _knobOffsetY;

    private Drawable _knob;
    private int _joystickFrameColor = Color.WHITE;

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

        _radius = a.getInt(R.styleable.KnobView_radius, DEFAULT_RADIUS);
        _knobSizeRatio = a.getFloat(R.styleable.KnobView_knobRelativeSize, DEFAULT_KNOB_SIZE_RATIO);
        _frameStrokeWidth = a.getFloat(R.styleable.KnobView_frameStrokeWidth, DEFAULT_FRAME_STROKE_WIDTH);
        int knobID = a.hasValue(R.styleable.KnobView_knobGFX) ?
                R.styleable.KnobView_knobGFX : getDefaultKnobDrawableID();
        _knob = getResources().getDrawable(knobID);
        _joystickFrameColor = a.getColor(R.styleable.KnobView_frameColor, _joystickFrameColor);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int knobRadius = (int) (_radius * _knobSizeRatio);

        // Draw the frame of the joystick
        drawFrame(paddingLeft + _radius, paddingTop + _radius, canvas);
        // Calculate the center position for the knob
        int knobXZero = (int) (paddingLeft + _radius - knobRadius);
        int knobYZero = (int) (paddingTop + _radius - knobRadius);
        // Draw the knob
        drawKnob(knobXZero, knobYZero, knobRadius, canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode  = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize  = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredDimension = (int) (_radius * 2 + _frameStrokeWidth * 2);

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
        _radius = (int) (dim / 2 - _frameStrokeWidth);

        setMeasuredDimension(dim, dim);
    }

    private Paint joystickFramePaint;
    protected void drawFrame(int x, int y, Canvas canvas){
        if( joystickFramePaint == null ) initFramePaint();
        joystickFramePaint.setStrokeWidth(_frameStrokeWidth);
        x += _frameStrokeWidth;
        y += _frameStrokeWidth;
        canvas.drawCircle(x,y, _radius,joystickFramePaint);
    }

    protected void drawKnob(int x, int y, int knobRadius, Canvas canvas){
        x += _frameStrokeWidth + _knobOffsetX;
        y += _frameStrokeWidth + _knobOffsetY;
        _knob.setBounds(x, y, x + knobRadius * 2, y + knobRadius * 2);
        _knob.draw(canvas);
    }

    protected void initFramePaint(){
        joystickFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        joystickFramePaint.setStyle(Paint.Style.STROKE);
        joystickFramePaint.setColor(_joystickFrameColor);
        joystickFramePaint.setStrokeWidth(_frameStrokeWidth);
    }

    /** Should return a difference in X position, relative to the center of the view */
    protected abstract int getKnobX();
    /** Should return a difference in Y position, relative to the center of the view */
    protected abstract int getKnobY();

    /** Call this to update the actual graphics */
    public void updateKnobPosition(){
        // Calculate the knob radius.
        int knobRadius = (int) (_radius * _knobSizeRatio);
        // The distance the knob can move from the center before hitting the frame
        int availableRadius = _radius - knobRadius;
        // Get the X & Y positions relative to the center of the view from the subclass
        float x = getKnobX();
        float y = getKnobY();

        // Calculate the length of the vector from the center of the view to the point from the subclass
        float length = (float) Math.sqrt(x*x + y*y);

        // If length is not zero, normalize the vector
        x /= length != 0 ? length : 1;
        y /= length != 0 ? length : 1;

        // Allow the length of the vector to be less than the radius
        float ratio = length > availableRadius ? 1 : length / availableRadius;

        // Set the offsets for the knob graphic.
        _knobOffsetX = Math.abs(length) + knobRadius < _radius ? getKnobX() : x * (_radius - knobRadius);
        _knobOffsetY = Math.abs(length) + knobRadius < _radius ? getKnobY() : y * (_radius - knobRadius);

        // Notify listeners of the calculated point
        notifyListeners(x * ratio, -y * ratio); // Flip y to make up positive and down negative

        // Refresh the view
        invalidate();
    }

    protected int getDefaultKnobDrawableID(){ return R.drawable.knob_red; }

    public interface KnobUpdateListener{ void onKnobUpdate(float x, float y); }

    public void addListener(KnobUpdateListener listener){
        if( _listeners == null ) _listeners = new HashSet<KnobUpdateListener>();
        _listeners.add(listener);
    }

    public void removeListener(KnobUpdateListener listener){
        _listeners.remove(listener);
        if( _listeners.isEmpty() ) _listeners = null;
    }

    private void notifyListeners(float x, float y){
        if( _listeners != null )
            for( KnobUpdateListener listener : _listeners) listener.onKnobUpdate(x, y);
    }
}
