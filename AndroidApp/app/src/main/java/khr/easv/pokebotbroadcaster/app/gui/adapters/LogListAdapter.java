package khr.easv.pokebotbroadcaster.app.gui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import khr.easv.pokebotbroadcaster.app.R;
import khr.easv.pokebotbroadcaster.app.entities.logger.LogEntry;

public class LogListAdapter extends ArrayAdapter<LogEntry>{

    private static final int
            EVEN_ALPHA = 75,
            ODD_ALPHA  = 60;

    private List<LogEntry> _entries;

    public LogListAdapter(Context context, int resource, List<LogEntry> entries) {
        super(context, resource, entries);
        _entries = entries;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.list_item_log_entry, null);
        }

        LogEntry entry = _entries.get(index);

        TextView txtLogTitle = (TextView) view.findViewById(R.id.txtLogTitle);
        txtLogTitle.setText(entry.getTitle());

        view.setBackgroundColor(getBackgroundColor(view, index, entry));

        return view;
    }

    int getBackgroundColor(View view, int index, LogEntry entry){
        int colorID;
        switch (entry.getTag()){
            case DEBUG:
                colorID = R.color.LOG_ENTRY_DEBUG;
                break;
            case INFO:
                colorID = R.color.LOG_ENTRY_INFO;
                break;
            case ERROR:
                colorID = R.color.LOG_ENTRY_ERROR;
                break;
            case WARNING:
                colorID = R.color.LOG_ENTRY_WARNING;
                break;
            default:
                colorID = -1; // = Color.WHITE
                break;
        }
        int color = view.getResources().getColor(colorID);
        int alpha = index % 2 == 0 ? EVEN_ALPHA : ODD_ALPHA;
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
