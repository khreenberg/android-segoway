package khr.easv.pokebotbroadcaster.app.gui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import khr.easv.pokebotbroadcaster.app.R;
import khr.easv.pokebotbroadcaster.app.entities.logger.LogEntry;
import khr.easv.pokebotbroadcaster.app.entities.logger.Logger;
import khr.easv.pokebotbroadcaster.app.gui.adapters.LogListAdapter;

import static khr.easv.pokebotbroadcaster.app.entities.logger.Logger.ILoggerListener;

public class LogFragment extends ListFragment implements ILoggerListener {

    private OnLogEntryClickedListener _listener;
    LogListAdapter _adapter;

    public LogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<LogEntry> entries = new ArrayList<LogEntry>(Logger.getEntries()); // Create a copy to avoid illegal state exceptions
        _adapter = new LogListAdapter(getActivity(), R.layout.list_item_log_entry, entries);
        setListAdapter(_adapter);
        Logger.addObserver(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            _listener = (OnLogEntryClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnLogEntryClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (_listener == null) return;
        _listener.onLogEntryClicked(_adapter.getItem(position));
    }

    public void clear(){
        _adapter.clear();
    }

    @Override
    public void onLog(final LogEntry entry) {
        if(isVisible())
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _adapter.add(entry);
                    setSelection(_adapter.getCount() - 1); // Scroll the view to the bottom
                }
            });
    }

    public interface OnLogEntryClickedListener {
        public void onLogEntryClicked(LogEntry entry);
    }
}
