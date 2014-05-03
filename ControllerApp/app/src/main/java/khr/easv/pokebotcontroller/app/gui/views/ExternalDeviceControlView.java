package khr.easv.pokebotcontroller.app.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import khr.easv.pokebotcontroller.app.R;

public class ExternalDeviceControlView extends AbstractKnobView{

    private static final float AXIS_MULTIPLIER = 333;
    private static final float AXIS_THRESHOLD = 0.01f;

    private float _leftX, _leftY, _rightX, _rightY, _dpadX, _dpadY;

    public ExternalDeviceControlView(Context context) {
        super(context);
        init();
    }

    public ExternalDeviceControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExternalDeviceControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override // Request that this view gets focus, when it's visible
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if( visibility != VISIBLE )return;
        requestFocus();
    }

    private void init() {
        setFocusableInTouchMode(true);
        setFocusable(true);
        requestFocus();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        _leftX = getAxis(event, MotionEvent.AXIS_X);        // Left thumbstick axis X
        _leftY = getAxis(event, MotionEvent.AXIS_Y);        // Left thumbstick axis Y
        _rightX = getAxis(event, MotionEvent.AXIS_Z);       // Right thumbstick axis X
        _rightY = getAxis(event, MotionEvent.AXIS_RZ);      // Right thumbstick axis Y
        _dpadX = getAxis(event, MotionEvent.AXIS_HAT_X);    // D-pad axis X
        _dpadY = getAxis(event, MotionEvent.AXIS_HAT_Y);    // D-pad axis Y
        updateKnobPosition();
        return true; // Return true to say that we handled the input event, thus preventing loss of focus
    }


    private float getAxis(MotionEvent event, int axis) {
        float axisValue = event.getAxisValue(axis);
        return Math.abs(axisValue) < AXIS_THRESHOLD  ? 0 : axisValue * AXIS_MULTIPLIER;
    }

    // Return input
    @Override protected float getKnobX() { return _leftX + _rightX + _dpadX; }
    @Override protected float getKnobY() { return _leftY + _rightY + _dpadY; }

    // Make the knob blue by default
    @Override protected int getDefaultKnobDrawableID() { return R.drawable.knob_blue; }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) { return true; /*Disable all buttons*/ }
}
