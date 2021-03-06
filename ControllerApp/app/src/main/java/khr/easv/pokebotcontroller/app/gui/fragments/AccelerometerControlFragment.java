package khr.easv.pokebotcontroller.app.gui.fragments;

import android.content.Context;

import khr.easv.pokebotcontroller.app.gui.views.AbstractKnobView;
import khr.easv.pokebotcontroller.app.gui.views.AccelerometerControlView;

public class AccelerometerControlFragment extends AbstractKnobControlFragment {
    @Override
    protected AbstractKnobView createKnobView(Context context) {
        return new AccelerometerControlView(context);
    }
}
