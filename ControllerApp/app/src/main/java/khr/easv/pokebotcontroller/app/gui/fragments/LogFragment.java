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

import static khr.easv.pokebotcontroller.app.entities.Logger.ILoggerListener;

public class LogFragment extends ListFragment implements ILoggerListener {

    private OnLogEntryClickedListener _listener;
    LogListAdapter adapter;

    public LogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<LogEntry> entries = new ArrayList<LogEntry>(Logger.getEntries()); // Make a copy to prevent illegal state exceptions
        adapter = new LogListAdapter(getActivity(), R.layout.list_item_log_entry, entries);
        setListAdapter(adapter);
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
        _listener.onLogEntryClicked(adapter.getItem(position));
    }

    @Override
    public void onLog(final LogEntry entry) {
        if(isVisible())
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.add(entry);
                    setSelection(adapter.getCount() - 1); // Scroll the view to the bottom
                }
            });
    }

    public interface OnLogEntryClickedListener {
        public void onLogEntryClicked(LogEntry entry);
    }
}