package khr.easv.pokebotcontroller.app.gui.fragments;

import android.content.Context;

import khr.easv.pokebotcontroller.app.gui.views.AbstractKnobView;
import khr.easv.pokebotcontroller.app.gui.views.OnScreenJoystickView;

public class TestJoystickFragment extends AbstractKnobControlFragment {

    @Override
    protected AbstractKnobView createKnobView(Context context) {
        return new OnScreenJoystickView(context);
    }
}
