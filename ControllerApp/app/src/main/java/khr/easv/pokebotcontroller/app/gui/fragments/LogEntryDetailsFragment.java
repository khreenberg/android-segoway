package khr.easv.pokebotcontroller.app.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.entities.LogEntry;

public class LogEntryDetailsFragment extends Fragment {

    public static final String BUNDLE_KEY_ENTRY = "entry";
    private LogEntry _entry;

    private View _root;

    private TextView
        _txtTitle,
        _txtTag,
        _txtDetails;

    public LogEntryDetailsFragment() { /*Required empty public constructor*/ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _root = inflater.inflate(R.layout.fragment_log_entry_details, container, false);
        _entry = (LogEntry) getArguments().getSerializable(BUNDLE_KEY_ENTRY);
        initialize();
        populate();
        return _root;
    }

    private void initialize(){
        _txtTag = (TextView) _root.findViewById(R.id.txtLogEntryDetailsTag);
        _txtTitle = (TextView) _root.findViewById(R.id.txtLogEntryDetailsTitle);
        _txtDetails = (TextView) _root.findViewById(R.id.txtLogEntryDetailsDetails);
        // Enable text scrolling
        _txtDetails.setMovementMethod(new ScrollingMovementMethod());
        _txtTitle.setMovementMethod(new ScrollingMovementMethod());
    }

    private void populate(){
        _txtTag.setText(_entry.getTag().toString());
        _txtTitle.setText(_entry.getTitle());
        _txtDetails.setText(_entry.getDetails());
    }
}
