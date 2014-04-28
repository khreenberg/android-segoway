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

    private View _root;
    private ExternalDeviceControlView _externalDeviceControlView;

    /** Required empty constructor */
    public ExternalDeviceControlFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _root = inflater.inflate(R.layout.fragment_external_device_control, container, false);
        _externalDeviceControlView = (ExternalDeviceControlView) _root.findViewById(R.id.externalDeviceControl);
        _externalDeviceControlView.addListener((AbstractKnobView.KnobUpdateListener) getActivity());
        return _root;
    }
}
