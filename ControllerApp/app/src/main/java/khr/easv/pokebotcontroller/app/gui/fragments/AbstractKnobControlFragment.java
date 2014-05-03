package khr.easv.pokebotcontroller.app.gui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.data.IInputListener;
import khr.easv.pokebotcontroller.app.gui.views.AbstractKnobView;

import static android.widget.FrameLayout.LayoutParams;

public abstract class AbstractKnobControlFragment extends Fragment {

    /** Required empty constructor */
    public AbstractKnobControlFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View _root = inflater.inflate(R.layout.fragment_control, container, false);

        ViewGroup abstractKnobViewContainer = (ViewGroup) _root.findViewById(R.id.controlLayout);

        AbstractKnobView knobView = createKnobView(_root.getContext());
        applyStyling(knobView);
        abstractKnobViewContainer.addView(knobView);
        makeActivityListenForKnobUpdates(knobView);
        return _root;
    }

    private void makeActivityListenForKnobUpdates(AbstractKnobView knobView) {
        Activity activity = getActivity();
        try {
            knobView.addListener((IInputListener) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + activity.getString(R.string.must_implement_interface_inputlistener));
        }
    }

    protected void applyStyling(AbstractKnobView view) {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        view.setLayoutParams(layoutParams);
    }

    protected abstract AbstractKnobView createKnobView(Context context);
}
