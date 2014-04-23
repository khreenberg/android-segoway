package khr.easv.pokebotcontroller.app.gui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.views.AbstractKnobView;
import khr.easv.pokebotcontroller.app.gui.views.AccelerometerControlView;

public class AccelerometerControlFragment extends Fragment {

    private View _root;
    private AccelerometerControlView _jstkAccelerometer;

    public AccelerometerControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _root =  inflater.inflate(R.layout.fragment_accelerometer_control, container, false);
        _jstkAccelerometer = (AccelerometerControlView) _root.findViewById(R.id.jstkAccelerometer);
        _jstkAccelerometer.addListener((AbstractKnobView.KnobUpdateListener) getActivity());
        return _root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if( !(activity instanceof AbstractKnobView.KnobUpdateListener)) return;
//        _jstkAccelerometer.addListener((AbstractKnobView.KnobUpdateListener) activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Activity activity = getActivity();
        if (!(activity instanceof AbstractKnobView.KnobUpdateListener)) return;
//        _jstkAccelerometer.removeListener((AbstractKnobView.KnobUpdateListener) activity);
    }
}
