package khr.easv.pokebotcontroller.app.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import khr.easv.pokebotcontroller.app.R;

/** Knob view controlled by touch events */
public class OnScreenJoystickView extends AbstractKnobView {

    private float _offsetX, _offsetY;

    public OnScreenJoystickView(Context context) { super(context); }
    public OnScreenJoystickView(Context context, AttributeSet attrs) { super(context, attrs); }
    public OnScreenJoystickView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchReleased(event)) return true;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        _offsetX = event.getX() - centerX;
        _offsetY = event.getY() - centerY;
        updateKnobPosition();
        return true;
    }

    private boolean touchReleased(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            _offsetX = _offsetY = 0;
            updateKnobPosition();
            return true;
        }
        return false;
    }

    @Override protected float getKnobX() { return _offsetX; }
    @Override protected float getKnobY() { return _offsetY; }
    @Override protected int getDefaultKnobDrawableID() { return R.drawable.knob_red; }
}
