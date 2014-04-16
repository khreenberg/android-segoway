package khr.easv.pokebotcontroller.app.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import khr.easv.pokebotcontroller.app.R;

public class OnScreenJoystickView extends AbstractKnobView {

    private int offsetX, offsetY;

    public OnScreenJoystickView(Context context) {
        super(context);
    }

    public OnScreenJoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnScreenJoystickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            offsetX = offsetY = 0;
            updateKnobPosition();
            return true;
        }
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        offsetX = (int) event.getX() - centerX;
        offsetY = (int) event.getY() - centerY;
        updateKnobPosition();
        return true;
    }

    @Override
    protected int getKnobX() {
        return offsetX;
    }

    @Override
    protected int getKnobY() {
        return offsetY;
    }

    @Override
    protected int getDefaultKnobDrawableID() {
        return R.drawable.joyknob;
    }
}
