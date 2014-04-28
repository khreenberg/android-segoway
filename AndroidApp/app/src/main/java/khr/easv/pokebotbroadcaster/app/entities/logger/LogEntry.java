package khr.easv.pokebotbroadcaster.app.entities.logger;

import java.io.Serializable;

public class LogEntry implements Serializable{

    public enum LogTag{ DEBUG, INFO, ERROR, WARNING }

    private LogTag _tag;
    private String _title, _details;

    public LogEntry(String title, String details, LogTag tag) {
        _tag = tag;
        _title = title;
        _details = details;
    }

    public String getTitle() { return _title; }
    public void setTitle(String title) { _title = title; }

    public String getDetails() { return _details; }
    public void setDetails(String details) { _details = details; }

    public LogTag getTag() { return _tag; }
    public void setTag(LogTag tag) { _tag = tag; }
}
