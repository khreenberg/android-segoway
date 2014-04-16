package khr.easv.pokebotbroadcaster.app.gui.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import khr.easv.pokebotbroadcaster.app.R;
import khr.easv.pokebotbroadcaster.app.entities.logger.LogEntry;
import khr.easv.pokebotbroadcaster.app.entities.logger.Logger;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
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
        initialize();populate();
        // Inflate the layout for this fragment
        return _root;
    }

    private void initialize(){
        _txtTitle = (TextView) _root.findViewById(R.id.txtLogEntryDetailsTitle);
        _txtTag = (TextView) _root.findViewById(R.id.txtLogEntryDetailsTag);
        _txtDetails = (TextView) _root.findViewById(R.id.txtLogEntryDetailsDetails);
    }

    private void populate(){
        _txtTag.setText(_entry.getTag().toString());
        _txtTitle.setText(_entry.getTitle());
        _txtDetails.setText(_entry.getDetails());
    }

}
