package khr.easv.pokebotcontroller.app.gui.fragments;

import android.content.Context;

import khr.easv.pokebotcontroller.app.gui.views.AbstractKnobView;
import khr.easv.pokebotcontroller.app.gui.views.ExternalDeviceControlView;

public class ExternalDeviceControlFragment extends AbstractKnobControlFragment {
    @Override
    protected AbstractKnobView createKnobView(Context context) {
        return new ExternalDeviceControlView(context);
    }
}
