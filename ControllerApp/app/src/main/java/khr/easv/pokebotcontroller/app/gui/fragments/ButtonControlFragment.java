package khr.easv.pokebotcontroller.app.gui.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.HashSet;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.data.IInputListener;
import khr.easv.pokebotcontroller.app.gui.views.ImageButtonIgnoreTransparency;

import static khr.easv.pokebotcontroller.app.gui.views.ImageButtonIgnoreTransparency.IButtonControlListener;

public class ButtonControlFragment extends Fragment implements IButtonControlListener {

    // Constants for assigning values to the buttons
    private static final float NO_INPUT = 0.000f;
    private static final float STRAIGHT = 1.000f;
    private static final float DIAGONAL = 0.707f;

    private View _root;
    private FrameLayout _layout;

    private ProgressBar _loadingBar;

    private ImageButtonIgnoreTransparency
            _btnTurnLeft,       _btnMoveForward,    _btnTurnRight,
            _btnRotateLeft,     _btnCenter,         _btnRotateRight,
            _btnBackLeft,       _btnMoveBack,       _btnBackRight;

    private HashSet<ImageButtonIgnoreTransparency> _buttons;
    private HashSet<IInputListener> _listeners;

    private float[] _xValues, _yValues;

    /** Required empty public constructor */
    public ButtonControlFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _root = inflater.inflate(R.layout.fragment_control, container, false);
        _layout = (FrameLayout) _root.findViewById(R.id.controlLayout);

        showLoadingBar();
        initializationTask.execute();
        // Inflate the layout for this fragment
        return _root;
    }

    private void showLoadingBar() {
        _loadingBar = new ProgressBar(_root.getContext(), null, android.R.attr.progressBarStyleHorizontal);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        _loadingBar.setLayoutParams(layoutParams);
        _loadingBar.setMax(9);
        _layout.addView(_loadingBar);
    }

    /** This method also does minor styling to the buttons. */
    private void addButtonsToLayout() {
        FrameLayout.LayoutParams layoutParams = createLayoutParams();
        for( ImageButtonIgnoreTransparency button : _buttons ){
            button.setLayoutParams(layoutParams);
            button.setScaleType(ImageView.ScaleType.FIT_CENTER);
            button.setBackgroundColor(Color.TRANSPARENT);
            _layout.addView(button);
        }
    }

    private FrameLayout.LayoutParams createLayoutParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    @Override
    public void onButtonEvent(ImageButtonIgnoreTransparency button, boolean isPressed) {
        if( isPressed ) setValues(button);
        else clearValues(button);
        notifyListeners();
    }

    // This could easily have been done in a prettier way...
    public void setValues(View v) {
        if( v == _btnTurnLeft )     {_xValues[0] = -DIAGONAL; _yValues[0] =  DIAGONAL;}
        if( v == _btnMoveForward )  {_xValues[1] =  NO_INPUT; _yValues[1] =  STRAIGHT;}
        if( v == _btnTurnRight )    {_xValues[2] =  DIAGONAL; _yValues[2] =  DIAGONAL;}
        if( v == _btnRotateLeft )   {_xValues[3] = -STRAIGHT; _yValues[3] =  NO_INPUT;}
        if( v == _btnCenter )       {_xValues[4] =  NO_INPUT; _yValues[4] =  NO_INPUT;}
        if( v == _btnRotateRight )  {_xValues[5] =  STRAIGHT; _yValues[5] =  NO_INPUT;}
        if( v == _btnBackLeft )     {_xValues[6] = -DIAGONAL; _yValues[6] = -DIAGONAL;}
        if( v == _btnMoveBack )     {_xValues[7] =  NO_INPUT; _yValues[7] = -STRAIGHT;}
        if( v == _btnBackRight )    {_xValues[8] =  DIAGONAL; _yValues[8] = -DIAGONAL;}
    }

    private void clearValues(View v) {
        if( v == _btnTurnLeft )     {_xValues[0] = -NO_INPUT; _yValues[0] =  NO_INPUT;}
        if( v == _btnMoveForward )  {_xValues[1] =  NO_INPUT; _yValues[1] =  NO_INPUT;}
        if( v == _btnTurnRight )    {_xValues[2] =  NO_INPUT; _yValues[2] =  NO_INPUT;}
        if( v == _btnRotateLeft )   {_xValues[3] = -NO_INPUT; _yValues[3] =  NO_INPUT;}
        if( v == _btnCenter )       {_xValues[4] =  NO_INPUT; _yValues[4] =  NO_INPUT;}
        if( v == _btnRotateRight )  {_xValues[5] =  NO_INPUT; _yValues[5] =  NO_INPUT;}
        if( v == _btnBackLeft )     {_xValues[6] = -NO_INPUT; _yValues[6] = -NO_INPUT;}
        if( v == _btnMoveBack )     {_xValues[7] =  NO_INPUT; _yValues[7] = -NO_INPUT;}
        if( v == _btnBackRight )    {_xValues[8] =  NO_INPUT; _yValues[8] = -NO_INPUT;}
    }

    private AsyncTask<Void, Integer, Void> initializationTask = new AsyncTask<Void, Integer, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            initialize();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            addButtonsToLayout();
            _layout.removeView(_loadingBar);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            _loadingBar.setProgress(values[0]);
        }

        private void initialize(){
            initializeFloatArrays();
            initializeButtons();
            attachClickListener();
            makeActivityListen();
        }

        private void initializeFloatArrays() {
            _xValues = new float[9];
            _yValues = new float[9];
        }

        private void makeActivityListen() {
            try{ addListener((IInputListener) getActivity()); }
            catch (ClassCastException e) { /* We could handle this, but we won't */ }
        }

        private void initializeButtons(){
            createButtons();
            addButtonsToSet();
        }

        private void addButtonsToSet() {
            _buttons = new HashSet<ImageButtonIgnoreTransparency>(9);
            _buttons.add(_btnTurnLeft);
            _buttons.add(_btnMoveForward);
            _buttons.add(_btnTurnRight);
            _buttons.add(_btnRotateLeft);
            _buttons.add(_btnCenter);
            _buttons.add(_btnRotateRight);
            _buttons.add(_btnBackLeft);
            _buttons.add(_btnMoveBack);
            _buttons.add(_btnBackRight);
        }

        private void createButtons() {
            // Top left
            _btnTurnLeft = new ImageButtonIgnoreTransparency(_root.getContext());
            _btnTurnLeft.setImageDrawable(getResources().getDrawable(R.drawable.selector_turn_left));
            publishProgress(1);
            // Top center
            _btnMoveForward = new ImageButtonIgnoreTransparency(_root.getContext());
            _btnMoveForward.setImageDrawable(getResources().getDrawable(R.drawable.selector_move_forward));
            publishProgress(2);
            // Top right
            _btnTurnRight = new ImageButtonIgnoreTransparency(_root.getContext());
            _btnTurnRight.setImageDrawable(getResources().getDrawable(R.drawable.selector_turn_right));
            publishProgress(3);
            // Middle left
            _btnRotateLeft = new ImageButtonIgnoreTransparency(_root.getContext());
            _btnRotateLeft.setImageDrawable(getResources().getDrawable(R.drawable.selector_rotate_left));
            publishProgress(4);
            // Middle center
            _btnCenter = new ImageButtonIgnoreTransparency(_root.getContext());
            _btnCenter.setImageDrawable(getResources().getDrawable(R.drawable.selector_center));
            publishProgress(5);
            // Middle right
            _btnRotateRight = new ImageButtonIgnoreTransparency(_root.getContext());
            _btnRotateRight.setImageDrawable(getResources().getDrawable(R.drawable.selector_rotate_right));
            publishProgress(6);
            // Bottom left
            _btnBackLeft = new ImageButtonIgnoreTransparency(_root.getContext());
            _btnBackLeft.setImageDrawable(getResources().getDrawable(R.drawable.selector_back_left));
            publishProgress(7);
            // Bottom center
            _btnMoveBack = new ImageButtonIgnoreTransparency(_root.getContext());
            _btnMoveBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_move_back));
            publishProgress(8);
            // Bottom right
            _btnBackRight = new ImageButtonIgnoreTransparency(_root.getContext());
            _btnBackRight.setImageDrawable(getResources().getDrawable(R.drawable.selector_back_right));
            publishProgress(9);
        }

        private void attachClickListener(){
            for( ImageButtonIgnoreTransparency b : _buttons) b.addListener(ButtonControlFragment.this);
        }
    };

    public void addListener(IInputListener listener){
        if( _listeners == null ) _listeners = new HashSet<IInputListener>();
        _listeners.add(listener);
    }

    public void removeListener(IInputListener listener) {
        _listeners.remove(listener);
        if( _listeners.isEmpty() ) _listeners = null;
    }

    private void notifyListeners() {
        float x = getArraySum(_xValues);
        float y = getArraySum(_yValues);
        for (IInputListener listener : _listeners)  listener.onInput(x,y);
    }

    private float getArraySum(float[] b) {
        float sum = 0.0f;
        for (int i = 0; i < b.length; i++) sum += b[i];
        return sum;
    }
}
