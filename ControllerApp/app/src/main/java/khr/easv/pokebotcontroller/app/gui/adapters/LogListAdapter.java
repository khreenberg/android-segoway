package khr.easv.pokebotcontroller.app.gui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.entities.LogEntry;

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
        if (view == null) view = inflateView();

        LogEntry entry = _entries.get(index);
        TextView txtLogTitle = (TextView) view.findViewById(R.id.txtLogTitle);
        txtLogTitle.setText(entry.getTitle());
        view.setBackgroundColor(getBackgroundColor(view, index, entry));

        return view;
    }

    private View inflateView() {
        View view;LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.list_item_log_entry, null);
        return view;
    }

    private int getBackgroundColor(View view, int index, LogEntry entry){
        int colorID = getBackgroundColorId(entry);
        int color = view.getResources().getColor(colorID);
        int alpha = index % 2 == 0 ? EVEN_ALPHA : ODD_ALPHA;
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private int getBackgroundColorId(LogEntry entry) {
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
        return colorID;
    }
}
