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
import khr.easv.pokebotcontroller.app.gui.adapters.LogListAdapter;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class LogFragment extends ListFragment {

    private OnLogEntryClickedListener mListener;
    LogListAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<LogEntry> entries = createDummyData();
        adapter = new LogListAdapter(getActivity(), R.layout.list_item_log_entry, entries);
        setListAdapter(adapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLogEntryClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnLogEntryClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onLogEntryClicked(adapter.getItem(position));
        }
    }

    public interface OnLogEntryClickedListener {
        // TODO: Update argument type and name
        public void onLogEntryClicked(LogEntry entry);
    }

    List<LogEntry> createDummyData(){
        List<LogEntry> dummyList = new ArrayList<LogEntry>();
        dummyList.add(new LogEntry("Sometitle", LogEntry.LogTag.DEBUG));
        dummyList.add(new LogEntry("Some longer title", LogEntry.LogTag.DEBUG));
        dummyList.add(new LogEntry("Some info title", LogEntry.LogTag.INFO));
        dummyList.add(new LogEntry("Random warning", LogEntry.LogTag.WARNING));
        dummyList.add(new LogEntry("FUCK! ERRORS!", LogEntry.LogTag.ERROR));
        dummyList.add(new LogEntry("Just kidding. :D", LogEntry.LogTag.INFO));
        dummyList.add(new LogEntry("Dum dee dum..", LogEntry.LogTag.DEBUG));
        dummyList.add(new LogEntry("Moar info", LogEntry.LogTag.INFO));
        dummyList.add(new LogEntry("Entries ahoy!", LogEntry.LogTag.WARNING));
        dummyList.add(new LogEntry("This is just for testing. :D", LogEntry.LogTag.DEBUG));
        dummyList.add(new LogEntry("More tests", LogEntry.LogTag.DEBUG));
        dummyList.add(new LogEntry("Humungeolongusextremicus. That's latin and means \"a really long title\"", LogEntry.LogTag.INFO));
        dummyList.add(new LogEntry("Just some more data", LogEntry.LogTag.ERROR));
        dummyList.add(new LogEntry("Så testede man lige om den kunne skrive æø&å", LogEntry.LogTag.ERROR));
        dummyList.add(new LogEntry("Moar titles", LogEntry.LogTag.DEBUG));
        dummyList.add(new LogEntry("Even moar titles", LogEntry.LogTag.INFO));
        dummyList.add(new LogEntry("Even, even more titles", LogEntry.LogTag.WARNING));
        return dummyList;
    }
}
