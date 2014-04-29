package khr.easv.pokebotcontroller.app.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.views.AbstractKnobView;
import khr.easv.pokebotcontroller.app.gui.views.ExternalDeviceControlView;

public class ExternalDeviceControlFragment extends Fragment {

    /** Required empty constructor */
    public ExternalDeviceControlFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View _root = inflater.inflate(R.layout.fragment_external_device_control, container, false);

        ExternalDeviceControlView _externalDeviceControlView =
                (ExternalDeviceControlView) _root.findViewById(R.id.externalDeviceControl);
        _externalDeviceControlView.addListener((AbstractKnobView.KnobUpdateListener) getActivity());

        return _root;
    }
}
