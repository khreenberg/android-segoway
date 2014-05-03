package khr.easv.pokebotcontroller.app.gui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.entities.LogEntry;
import khr.easv.pokebotcontroller.app.entities.Logger;
import khr.easv.pokebotcontroller.app.gui.adapters.LogListAdapter;

public class LogFragment extends ListFragment implements Logger.ILoggerListener {

    private ILogEntryClickedListener _listener;
    private LogListAdapter _adapter;

    /** Default constructor */
    public LogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupList();
        Logger.addObserver(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Setup the callback to the activity
            _listener = (ILogEntryClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + activity.getString(R.string.must_implement_interface_log_entry_clicked_listener));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;   // Detach the callback
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (_listener == null) return;
        _listener.onLogEntryClicked(_adapter.getItem(position));
    }

    private void setupList(){
        // Create a copy to avoid illegal state exceptions
        List<LogEntry> entries = new ArrayList<LogEntry>(Logger.getEntries());
        _adapter = new LogListAdapter(getActivity(), R.layout.list_item_log_entry, entries);
        setListAdapter(_adapter);
    }

    public void clear(){
        _adapter.clear();
    }

    @Override
    public void onLog(LogEntry entry) {
        if(isVisible()) addEntryToList(entry);
    }

    private void addEntryToList(final LogEntry newEntry){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _adapter.add(newEntry);
                scrollViewToBottomOfList();
            }
        });
    }

    private void scrollViewToBottomOfList() {
        setSelection(_adapter.getCount() - 1);
    }

    public interface ILogEntryClickedListener {
        public void onLogEntryClicked(LogEntry entry);
    }
}
